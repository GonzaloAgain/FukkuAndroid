package net.azarquiel.fukkuapp.Views

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

import kotlinx.android.synthetic.main.activity_productos.*
import kotlinx.android.synthetic.main.content_productos.*
import net.azarquiel.fukkuapp.Adapter.CustomAdapterProductos
import net.azarquiel.fukkuapp.Model.Categoria
import net.azarquiel.fukkuapp.Model.Producto
import net.azarquiel.fukkuapp.Util.*
import net.azarquiel.fukkuapp.R

class ProductosActivity : AppCompatActivity() {

    private lateinit var accion : String
    private lateinit var adapter : CustomAdapterProductos
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)
        setSupportActionBar(toolbar)
        db = FirebaseFirestore.getInstance()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        accion=intent.getSerializableExtra("accion") as String
        crearAdapter()

        if(accion == TUS_PRODUCTOS){
            //KGqBjsuqe0747tCzBeyu --> esto es el id del usuario
            cargarProductos(COLECCION_USUARIOS,"KGqBjsuqe0747tCzBeyu",SUBCOLECCION_PRODUCTOS)
        }else if(accion == TUS_PRODUCTOS_FAVORITOS){
            //KGqBjsuqe0747tCzBeyu --> esto es el id del usuario
            cargarProductos(COLECCION_USUARIOS,"KGqBjsuqe0747tCzBeyu",SUBCOLECCION_PRODUCTOS_FAVORITOS)
        }
    }

    private fun crearAdapter(){
        adapter= CustomAdapterProductos(this,R.layout.productosrow)
        rvProductos.layoutManager= LinearLayoutManager(this)
        rvProductos.adapter=adapter
    }

    private fun cargarProductos(coleccion:String,id:String,subcoleccion:String){
        db.collection(coleccion).document(id).collection(subcoleccion)
            .orderBy(CAMPO_FECHA, Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e)
                    return@EventListener
                }

                val arrayProductos = ArrayList<Producto>()
                for (document in value!!) {
                    arrayProductos.add(document.toObject(Producto::class.java))
                }

                adapter.setProductos(arrayProductos)
            })
        /*db.collection(coleccion).document(id).collection(subcoleccion)
            .orderBy(CAMPO_FECHA, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        //Log.d("Jonay", "${document.data.getValue("Descripcion")}")
                        arrayProductos.add(Producto(document.id,"${document.data.getValue(CAMPO_NOMBRE)}", "${document.data.getValue(CAMPO_NOMBREUSUARIO)}","${document.data.getValue(
                            CAMPO_DESCRIPCION)}","${document.data.getValue(CAMPO_PRECIO)}","${document.data.getValue(
                            CAMPO_FECHA)}","${document.data.getValue(CAMPO_LATITUD)}","${document.data.getValue(
                            CAMPO_LONGITUD)}","${document.data.getValue(CAMPO_CATEGORIAID)}","${document.data.getValue(CAMPO_NOMBRECATEGORIA)}","${document.data.getValue(
                            CAMPO_USUARIOID)}"))
                    }
                    adapter.setProductos(arrayProductos)
                }
            }*/
    }

    fun pinchaProducto(v: View){
        val producto = v.tag as Producto
        var intent= Intent(this, DetailProductActivity::class.java)
        intent.putExtra("producto", producto)
        startActivity(intent)
    }
}
