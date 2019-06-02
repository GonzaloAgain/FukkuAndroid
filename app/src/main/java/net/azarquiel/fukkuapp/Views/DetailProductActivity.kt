package net.azarquiel.fukkuapp.Views

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.robertlevonyan.components.picker.ItemModel
import com.robertlevonyan.components.picker.PickerDialog
import com.robertlevonyan.components.picker.set
import kotlinx.android.synthetic.main.activity_detail_product.*
import kotlinx.android.synthetic.main.content_detail_product.*
import net.azarquiel.fukkuapp.Model.Producto
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.Util.*
import net.azarquiel.fukkuapp.Util.Util
import org.jetbrains.anko.toast

class DetailProductActivity : AppCompatActivity() {

    private lateinit var producto : Producto
    private lateinit var db : FirebaseFirestore
    private var isFavorito : Boolean=false
    private var editable = false
    private lateinit var pickerDialog: PickerDialog
    private var uriImagen: Uri?=null
    private lateinit var riversRef: StorageReference
    private var imagenRuta: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        inicializate()
    }

    private fun inicializate(){
        db = FirebaseFirestore.getInstance()
        producto=intent.getSerializableExtra("producto") as Producto
        title = producto.nombre
        showProduct()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_detail_product, menu)
        //checkFavorite(menu)
        checkUser(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_update_product -> editable()
            R.id.action_delete_product -> deleteProducto()
            R.id.action_favorito_product -> addDeleteFavoritos(item)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun showProduct(){
        activarEditText(false)
        mostrarImagen()
        tvNombreDetail.set(producto.nombre)
        tvPrecioDetail.set(producto.precio)
        tvDescripcionDetail.set(producto.descripcion)
        tvFechaDetail.text = producto.fecha
        tvUsuarioDetail.text = producto.nombreUsuario
        tvCategoriaDetail.text = producto.nombreCategoria
    }

    private fun mostrarImagen(){
        if(producto.imagen != ""){
            Glide.with(this).load(producto.imagen).into(ivProductDetail)
        }else{
            ivProductDetail.setImageResource(R.drawable.notfound)
        }
    }

    private fun deleteProducto(){
        deleteForProductos()
        deleteForCategoria()
        deleteForTusProductos()
        finish()
    }

    private fun deleteForProductos(){
        db.collection(COLECCION_PRODUCTOS).document(producto.id).delete()
    }

    private fun deleteForCategoria(){
        db.collection(COLECCION_CATEGORIA).document(producto.categoriaId).collection(SUBCOLECCION_PRODUCTOS).document(producto.id).delete()
    }

    private fun deleteForTusProductos(){
        db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(SUBCOLECCION_PRODUCTOS).document(producto.id).delete()
    }

    private fun addDeleteFavoritos(item: MenuItem): Boolean{
        if(!isFavorito){
            addToProductosFavoritos()
            item.title = resources.getString(R.string.deleteFavortios)
        }else{
            deleteToProductosFavoritos()
            item.title = resources.getString(R.string.addFavortios)
        }
        isFavorito = !isFavorito
        return true
    }

    private fun addToProductosFavoritos(){
        db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(SUBCOLECCION_PRODUCTOS_FAVORITOS)
            .document(producto.id).set(producto)
    }

    private fun deleteToProductosFavoritos(){
        db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(SUBCOLECCION_PRODUCTOS_FAVORITOS)
            .document(producto.id).delete()
    }

    private fun checkFavorite(menu: Menu) {
        db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(SUBCOLECCION_PRODUCTOS_FAVORITOS)
            .document(producto.id).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var document = task.result
                    if(document!!.exists()){
                        isFavorito = true
                        menu.findItem(R.id.action_favorito_product).title = resources.getString(R.string.deleteFavortios)
                    }else{
                        isFavorito = false
                        menu.findItem(R.id.action_favorito_product).title = resources.getString(R.string.addFavortios)
                    }
                }
            }
    }

    private fun checkUser(menu: Menu){
        if(producto.usuarioId == "KGqBjsuqe0747tCzBeyu"){
            menu.findItem(R.id.action_favorito_product).isVisible = false
            //Todo quitar boton flotante
        }else{
            menu.findItem(R.id.action_delete_product).isVisible = false
            menu.findItem(R.id.action_update_product).isVisible = false
            checkFavorite(menu)
        }
    }

    private fun editable(){
        if(!editable){
            //enable edit text
            activarEditText(true)
        }else{
            activarEditText(false)
            checkUpdate()
        }
        editable =!editable
    }

    private fun activarEditText(accion:Boolean){
        tvNombreDetail.isEnabled = accion
        tvPrecioDetail.isEnabled = accion
        tvDescripcionDetail.isEnabled = accion
        ivProductDetail.setOnClickListener {
            if (accion){
                picker()
            }
        }
    }

    private fun checkUpdate(){
        if(uriImagen !=null){
            subirImagen()
        }else{
            updateProduct()
        }
    }

    private fun updateProduct(){
        producto.nombre = "${tvNombreDetail.text}"
        producto.descripcion = "${tvDescripcionDetail.text}"
        producto.precio = "${tvPrecioDetail.text}"
        if(imagenRuta !=null){
            producto.imagen = imagenRuta!!
        }
        db.collection(COLECCION_PRODUCTOS).document(producto.id).set(producto)
        db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(SUBCOLECCION_PRODUCTOS).document(producto.id).set(producto)
        db.collection(COLECCION_CATEGORIA).document(producto.categoriaId).collection(SUBCOLECCION_PRODUCTOS).document(producto.id).set(producto)
    }

    private fun picker(){
        val itemModelc = ItemModel(ItemModel.ITEM_CAMERA)
        val itemModelg = ItemModel(ItemModel.ITEM_GALLERY)
        pickerDialog = PickerDialog.Builder(this)
            .setListType(PickerDialog.TYPE_GRID)
            .setItems(arrayListOf(itemModelg, itemModelc))
            .setDialogStyle(PickerDialog.DIALOG_MATERIAL)
            .create()

        pickerDialog.setPickerCloseListener { type, uri ->
            when (type) {
                ItemModel.ITEM_CAMERA -> {
                    uriImagen=uri
                    ivProductDetail.setImageURI(uriImagen)
                }
                ItemModel.ITEM_GALLERY -> {
                    uriImagen=uri
                    ivProductDetail.setImageURI(uriImagen)
                }
            }
        }
        pickerDialog.show(supportFragmentManager, "")
    }

    private fun subirImagen(){
        Util.inicia(this)
        var storageRef = FirebaseStorage.getInstance().reference
        riversRef = storageRef.child("images").child(uriImagen!!.lastPathSegment)
        var uploadTask = riversRef.putFile(uriImagen!!)
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            toast("Fallo al subir la imagen")
        }.addOnSuccessListener {
            sacarUrlImagen(riversRef.path)
        }
    }

    private fun sacarUrlImagen(path: String) {
        var storageRef = FirebaseStorage.getInstance().reference
        storageRef.child(path).downloadUrl.addOnSuccessListener {
            imagenRuta = it.toString()
            Util.finaliza()
            updateProduct()
        }
    }
}
