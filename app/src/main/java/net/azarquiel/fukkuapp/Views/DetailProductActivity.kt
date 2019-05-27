package net.azarquiel.fukkuapp.Views

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_detail_product.*
import kotlinx.android.synthetic.main.content_detail_product.*
import kotlinx.android.synthetic.main.productosrow.view.*
import net.azarquiel.fukkuapp.Model.Imagen
import net.azarquiel.fukkuapp.Model.Producto
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.Util.*

class DetailProductActivity : AppCompatActivity() {

    private lateinit var producto : Producto
    private lateinit var db : FirebaseFirestore
    private var isFavorito : Boolean=false

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
            R.id.action_update_product -> return true
            R.id.action_delete_product -> deleteProducto()
            R.id.action_favorito_product -> addDeleteFavoritos()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun showProduct(){
        foundImage()
        tvNombreDetail.text = producto.nombre
        tvPrecioDetail.text = producto.precio
        tvDescripcionDetail.text = producto.descripcion
        tvFechaDetail.text = producto.fecha
        tvUsuarioDetail.text = producto.nombreUsuario
        tvCategoriaDetail.text = producto.nombreCategoria
    }

    private fun foundImage(){
        var db= FirebaseFirestore.getInstance()
        var imagen: Imagen?=null
        db.collection(COLECCION_PRODUCTOS).document(producto.id).collection(SUBCOLECCION_IMAGENES).limit(1)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for(document in task.result!!) {
                        imagen= Imagen("${document.data.getValue(CAMPO_ID)}","${document.data.getValue(CAMPO_IMAGEN)}","${document.data.getValue(
                            CAMPO_PRODUCTOID
                        )}")
                    }
                    mostrarImagen(imagen)
                }
            }
    }

    private fun mostrarImagen(imagen:Imagen?){
        imagen?.let {
            var storageRef = FirebaseStorage.getInstance().reference

            storageRef.child(imagen.imagen).downloadUrl.addOnSuccessListener {
                Picasso.with(this).load(it).into(ivProductDetail)
            }.addOnFailureListener {
                // Handle any errors
            }
        }?: run {
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

    private fun addDeleteFavoritos() : Boolean{
        if(!isFavorito){
            addToProductosFavoritos()
            //Todo
        }else{
            deleteToProductosFavoritos()
            //Todo
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
}
