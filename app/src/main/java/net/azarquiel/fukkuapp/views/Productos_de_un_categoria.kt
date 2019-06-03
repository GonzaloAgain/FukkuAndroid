package net.azarquiel.fukkuapp.views

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_productos_de_un_categoria.*
import kotlinx.android.synthetic.main.content_productos_de_un_categoria.*
import net.azarquiel.fukkuapp.adapter.CustomAdapterProductos
import net.azarquiel.fukkuapp.Model.Categoria
import net.azarquiel.fukkuapp.Model.Producto
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.util.*
import org.jetbrains.anko.toast
import android.support.v7.widget.SearchView

class Productos_de_un_categoria : AppCompatActivity(), SearchView.OnQueryTextListener{

    private lateinit var categoria: Categoria
    private lateinit var adapter : CustomAdapterProductos
    private lateinit var arrayProductos : ArrayList<Producto>
    private lateinit var db: FirebaseFirestore
    private var isFavorito:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos_de_un_categoria)
        setSupportActionBar(toolbar)
        inicializate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_productos_categoria, menu)
        // ************* <Filtro> ************
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.setQueryHint("Search...")
        searchView.setOnQueryTextListener(this)
        // ************* </Filtro> ************
        checkFavorite(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_favorito -> addDeleteFavoritos(item)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        adapter.setProductos(arrayProductos.filter { p -> p.nombre.toLowerCase().contains(query!!.toLowerCase()) })
        return false
    }

    private fun inicializate(){
        db = FirebaseFirestore.getInstance()
        categoria=intent.getSerializableExtra("categoria") as Categoria
        title = categoria.nombre
        crearAdapter()
        cargarProductos(COLECCION_CATEGORIA,categoria.id, SUBCOLECCION_PRODUCTOS)
        refreshProductosCategoria.setOnRefreshListener {
            cargarProductos(COLECCION_CATEGORIA,categoria.id, SUBCOLECCION_PRODUCTOS)
            refreshProductosCategoria.isRefreshing=false
        }
    }

    private fun addDeleteFavoritos(item: MenuItem) : Boolean{
        if(!isFavorito){
            FireStoreUtil.addToCategoriasFavoritas(categoria)
            //addToCategoriasFavoritas()
            item.title = resources.getString(R.string.deleteFavortios)
        }else{
            FireStoreUtil.deleteToCategoriasFavoritas(categoria)
            //deleteToCategoriasFavoritas()
            item.title = resources.getString(R.string.addFavortios)
        }
        isFavorito = !isFavorito
        return true
    }

    private fun crearAdapter(){
        adapter= CustomAdapterProductos(this,R.layout.productosrow)
        rvProductosDeUnaCategoria.layoutManager = LinearLayoutManager(this)
        rvProductosDeUnaCategoria.adapter=adapter
    }

    private fun cargarProductos(coleccion:String,id:String,subcoleccion:String){
        arrayProductos=ArrayList()
        db.collection(coleccion).document(id).collection(subcoleccion)
            .orderBy(CAMPO_FECHA, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        if(document.data.getValue(CAMPO_IDUSUARIO).toString() != FireStoreUtil.uidUser()){
                            var producto = document.toObject(Producto::class.java)
                            producto.nombreCategoria = ""
                            arrayProductos.add(producto)
                        }
                    }
                    adapter.setProductos(arrayProductos)
                }
            }
    }

    private fun checkFavorite(menu: Menu) {
        db.collection(COLECCION_USUARIOS).document(FireStoreUtil.uidUser()).collection(SUBCOLECCION_CATEGORIAS_FAVORITOS)
            .document(categoria.id).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var document = task.result
                    if(document!!.exists()){
                        isFavorito = true
                        menu.findItem(R.id.action_favorito).title = resources.getString(R.string.deleteFavortios)
                    }else{
                        isFavorito = false
                        menu.findItem(R.id.action_favorito).title = resources.getString(R.string.addFavortios)
                    }
                }
            }
    }

    fun pinchaProducto(v: View){
        val producto = v.tag as Producto
        db.collection(COLECCION_PRODUCTOS).document(producto.id).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var document = task.result
                    if(document!!.exists()){
                        var intent= Intent(this, DetailProductActivity::class.java)
                        intent.putExtra("producto", document.toObject(Producto::class.java))
                        startActivity(intent)
                    }else{
                        toast("Es posible que el producto haya sido borrado")
                    }
                }
            }
    }
}
