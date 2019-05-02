package net.azarquiel.fukkuapp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_categoria.*
import kotlinx.android.synthetic.main.content_categoria.*
import net.azarquiel.fukkuapp.Adapter.CustomAdapterCategorias
import net.azarquiel.fukkuapp.Class.Categoria
import net.azarquiel.fukkuapp.Constantes.*

class CategoriaActivity : AppCompatActivity() {

    private lateinit var adapter:CustomAdapterCategorias
    private lateinit var arrayCategorias : ArrayList<Categoria>
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categoria)
        setSupportActionBar(toolbar)
        db = FirebaseFirestore.getInstance()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        crearAdapter()
        cargarCategorias()
    }

    private fun crearAdapter(){
        adapter= CustomAdapterCategorias(this,R.layout.categoriasrow)
        rvCategorias.layoutManager= LinearLayoutManager(this)
        rvCategorias.adapter=adapter
    }

    private fun cargarCategorias(){
        arrayCategorias= ArrayList()
        //KGqBjsuqe0747tCzBeyu --> esto es el id del usuario
        db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(SUBCOLECCION_CATEGORIAS_FAVORITOS)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        //Log.d("Jonay", "${document.data.getValue("Descripcion")}")
                        arrayCategorias.add(Categoria(document.id, "${document.data.getValue(CAMPO_NOMBRE)}","${document.data.getValue(
                            CAMPO_ICONO)}"))
                    }
                    adapter.setCategorias(arrayCategorias)
                }
            }
    }
}
