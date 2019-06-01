package net.azarquiel.fukkuapp.Views

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.robertlevonyan.components.picker.set
import kotlinx.android.synthetic.main.activity_detail_product.*
import kotlinx.android.synthetic.main.content_detail_product.*
import net.azarquiel.fukkuapp.Model.Categoria
import net.azarquiel.fukkuapp.Model.Producto
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.Util.*
import org.jetbrains.anko.toast

class DetailProductActivity : AppCompatActivity() {

    private lateinit var producto : Producto
    private lateinit var db : FirebaseFirestore
    private var isFavorito : Boolean=false
    private lateinit var arrayNombresCategorias:ArrayList<String>
    private lateinit var arrayCategorias:ArrayList<Categoria>
    private var categoriaElegida:String?=null
    private var editable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        db = FirebaseFirestore.getInstance()
        producto=intent.getSerializableExtra("producto") as Producto
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
        tvFechaDetail.set(producto.fecha)
        tvUsuarioDetail.set(producto.nombreUsuario)
        cargarCategorias()
    }

    private fun cargarCategorias(){
        arrayNombresCategorias= java.util.ArrayList()
        arrayCategorias= java.util.ArrayList()
        db.collection(COLECCION_CATEGORIA)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        arrayNombresCategorias.add("${document.data.getValue(CAMPO_NOMBRE)}")
                        arrayCategorias.add(document.toObject(Categoria::class.java))
                    }
                    cargarSpinner()
                    categoriaProducto()
                }
            }
    }

    private fun categoriaProducto(){
        db.collection(COLECCION_CATEGORIA).document(producto.categoriaId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var document = task.result
                    if(document!!.exists()){
                        var categoria = document.toObject(Categoria::class.java)
                        spCategoriaDetail.setSelection(arrayNombresCategorias.indexOf(categoria!!.nombre))
                    }else{
                        toast("Es posible que el producto haya sido borrado")
                    }
                }
            }
    }

    private fun cargarSpinner(){
        spCategoriaDetail.adapter= ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayNombresCategorias)
        spCategoriaDetail.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                categoriaElegida=arrayNombresCategorias.get(position)
            }
        }

    }

    private fun mostrarImagen(){
        if(producto.imagen != ""){
            Glide.with(this).load(producto.imagen).into(ivProductDetail)
        }else{
            ivProductDetail.setImageResource(R.drawable.notfound)
        }
    }

    private fun pulsadoUpdate(){

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
            //disable edit text and update product
        }
        editable =!editable
    }

    private fun activarEditText(accion:Boolean){
        tvNombreDetail.isEnabled = accion
        tvPrecioDetail.isEnabled = accion
        tvDescripcionDetail.isEnabled = accion
        spCategoriaDetail.isEnabled = accion
        ivProductDetail.setOnClickListener {
            if (accion){
                toast("Hola")
            }
        }
    }
}
