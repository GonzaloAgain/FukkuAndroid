package net.azarquiel.fukkuapp.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_fragment_categorias.*
import net.azarquiel.fukkuapp.Adapter.CustomAdapterCategorias
import net.azarquiel.fukkuapp.Model.Categoria
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.Util.COLECCION_CATEGORIA
import java.util.ArrayList

class Fragment_categorias : Fragment() {

    private lateinit var arrayCategorias : ArrayList<Categoria>
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter:CustomAdapterCategorias

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        val rootView = inflater.inflate(R.layout.activity_fragment_categorias, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        crearAdapter()
        cargarCategorias()
    }

    private fun crearAdapter(){
        adapter= CustomAdapterCategorias(activity!!.applicationContext, R.layout.categoriasrow)
        rvAllCategorias.layoutManager= LinearLayoutManager(activity!!.applicationContext)
        rvAllCategorias.adapter=adapter
    }

    private fun cargarCategorias(){
        arrayCategorias= ArrayList()
        db.collection(COLECCION_CATEGORIA)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        arrayCategorias.add(document.toObject(Categoria::class.java))
                    }
                    adapter.setCategorias(arrayCategorias)
                }
            }
    }
}
