package net.azarquiel.fukkuapp.views

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

import kotlinx.android.synthetic.main.activity_categoria.*
import kotlinx.android.synthetic.main.content_categoria.*
import net.azarquiel.fukkuapp.adapter.CustomAdapterCategorias
import net.azarquiel.fukkuapp.Model.Categoria
import net.azarquiel.fukkuapp.util.*
import net.azarquiel.fukkuapp.R

class CategoriaActivity : AppCompatActivity() {

    private lateinit var adapter:CustomAdapterCategorias
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categoria)
        setSupportActionBar(toolbar)
        inicializate()
    }

    private fun inicializate(){
        db = FirebaseFirestore.getInstance()
        title = resources.getString(R.string.categoriasFavNav)
        crearAdapter()
        cargarCategoriasDeInteres()
    }

    private fun crearAdapter(){
        adapter= CustomAdapterCategorias(this, R.layout.categoriasrow)
        rvCategorias.layoutManager= LinearLayoutManager(this)
        rvCategorias.adapter=adapter
    }

    private fun cargarCategoriasDeInteres(){
        db.collection(COLECCION_USUARIOS).document(FirestoreUtil.uidUser()).collection(SUBCOLECCION_CATEGORIAS_FAVORITOS)
            .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e)
                    return@EventListener
                }

                val arrayCategorias = ArrayList<Categoria>()
                for (document in value!!) {
                    arrayCategorias.add(document.toObject(Categoria::class.java))
                }

                adapter.setCategorias(arrayCategorias)
            })
    }

    fun pulsarCategoria(v: View){
        val categoria=v.tag as Categoria
        var intent= Intent(this, Productos_de_un_categoria::class.java)
        intent.putExtra("categoria", categoria)
        startActivity(intent)
    }
}
