package net.azarquiel.fukkuapp.views

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.View
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

import kotlinx.android.synthetic.main.activity_productos.*
import kotlinx.android.synthetic.main.content_productos.*
import net.azarquiel.fukkuapp.adapter.CustomAdapterProductos
import net.azarquiel.fukkuapp.Model.Producto
import net.azarquiel.fukkuapp.util.*
import net.azarquiel.fukkuapp.R
import org.jetbrains.anko.toast

class ProductosActivity : AppCompatActivity(), SearchView.OnQueryTextListener{

    private lateinit var accion : String
    private lateinit var adapter : CustomAdapterProductos
    private lateinit var db: FirebaseFirestore
    private lateinit var arrayProductos:ArrayList<Producto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)
        setSupportActionBar(toolbar)

        inicializate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_productos, menu)
        // ************* <Filtro> ************
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.setQueryHint("Search...")
        searchView.setOnQueryTextListener(this)
        // ************* </Filtro> ************
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
        accion=intent.getSerializableExtra("accion") as String
        crearAdapter()

        if(accion == TUS_PRODUCTOS){
            //KGqBjsuqe0747tCzBeyu --> esto es el id del usuario
            title = resources.getString(R.string.productosNav)
            cargarProductos(COLECCION_USUARIOS,FirestoreUtil.uidUser(),SUBCOLECCION_PRODUCTOS)
            fab.setOnClickListener { view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }else if(accion == TUS_PRODUCTOS_FAVORITOS){
            //KGqBjsuqe0747tCzBeyu --> esto es el id del usuario
            title = resources.getString(R.string.productosFavNav)
            cargarProductos(COLECCION_USUARIOS,FirestoreUtil.uidUser(),SUBCOLECCION_PRODUCTOS_FAVORITOS)
            fab.hide()
        }
    }

    private fun crearAdapter(){
        adapter= CustomAdapterProductos(this,R.layout.productosrow)
        rvProductos.layoutManager= LinearLayoutManager(this)
        rvProductos.adapter=adapter
    }

    private fun cargarProductos(coleccion:String,id:String,subcoleccion:String){
        arrayProductos= ArrayList()
        db.collection(coleccion).document(id).collection(subcoleccion)
            .orderBy(CAMPO_FECHA, Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e)
                    return@EventListener
                }
                arrayProductos.clear()
                for (document in value!!) {
                    arrayProductos.add(document.toObject(Producto::class.java))
                }
                adapter.setProductos(arrayProductos)
            })
    }

    fun pinchaProducto(v: View){
        val producto = v.tag as Producto
        var intent= Intent(this, DetailProductActivity::class.java)
        intent.putExtra("producto", producto.id)
        startActivity(intent)
    }
}
