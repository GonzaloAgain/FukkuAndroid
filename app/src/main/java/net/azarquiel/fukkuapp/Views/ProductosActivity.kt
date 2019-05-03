package net.azarquiel.fukkuapp.Views

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_productos.*
import kotlinx.android.synthetic.main.content_productos.*
import net.azarquiel.fukkuapp.Adapter.CustomAdapterProductos
import net.azarquiel.fukkuapp.Class.Producto
import net.azarquiel.fukkuapp.Util.*
import net.azarquiel.fukkuapp.R

class ProductosActivity : AppCompatActivity() {

    private lateinit var accion : String
    private lateinit var adapter : CustomAdapterProductos
    private lateinit var arrayProductos : ArrayList<Producto>
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
            cargarProductos(SUBCOLECCION_PRODUCTOS)
        }else if(accion == TUS_PRODUCTOS_FAVORITOS){
            cargarProductos(SUBCOLECCION_PRODUCTOS_FAVORITOS)
        }
    }

    private fun crearAdapter(){
        adapter= CustomAdapterProductos(this,R.layout.productosrow)
        rvProductos.layoutManager= LinearLayoutManager(this)
        rvProductos.adapter=adapter
    }

    private fun cargarProductos(subcoleccion:String){
        arrayProductos=ArrayList()
        //KGqBjsuqe0747tCzBeyu --> esto es el id del usuario
        db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(subcoleccion)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        //Log.d("Jonay", "${document.data.getValue("Descripcion")}")
                        arrayProductos.add(Producto(document.id,"${document.data.getValue(CAMPO_NOMBRE)}", "${document.data.getValue(
                            CAMPO_DESCRIPCION)}","${document.data.getValue(CAMPO_PRECIO)}","${document.data.getValue(
                            CAMPO_FECHA)}","${document.data.getValue(CAMPO_LATITUD)}","${document.data.getValue(
                            CAMPO_LONGITUD)}","${document.data.getValue(CAMPO_CATEGORIAID)}","${document.data.getValue(
                            CAMPO_USUARIOID)}"))
                    }
                    adapter.setProductos(arrayProductos)
                }
            }
    }
}
