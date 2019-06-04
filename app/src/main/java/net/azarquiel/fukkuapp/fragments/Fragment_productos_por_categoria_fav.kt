package net.azarquiel.fukkuapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_productos_por_categoria_fav.*
import net.azarquiel.fukkuapp.adapter.CustomAdapterProductos
import net.azarquiel.fukkuapp.Model.Categoria
import net.azarquiel.fukkuapp.Model.Producto
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.util.*

class Fragment_productos_por_categoria_fav : Fragment() {

    private lateinit var adapter : CustomAdapterProductos
    private lateinit var arrayCategoriasInteres:ArrayList<Categoria>
    private lateinit var arrayProductos : ArrayList<Producto>
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_productos_por_categoria_fav, container, false)
        //... nos traemos las cositas del diseño y trabajamos.
        //... si queremos algo de la activity llamamos al método getActivity()
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arrayProductos=ArrayList()
        arrayCategoriasInteres=ArrayList()
        db=FirebaseFirestore.getInstance()
        crearAdapter()
        cargarCategoriasInteres(FirestoreUtil.uidUser())
        refreshProductosCategoriasInteres.setOnRefreshListener {
            cargarCategoriasInteres(FirestoreUtil.uidUser())
            refreshProductosCategoriasInteres.isRefreshing=false
        }
    }

    private fun crearAdapter() {
        adapter= CustomAdapterProductos(activity!!.applicationContext, R.layout.productosrow)
        rvProductosCategoriaFav.layoutManager= LinearLayoutManager(activity!!.applicationContext)
        rvProductosCategoriaFav.adapter=adapter
    }

    private fun cargarCategoriasInteres(idUsuario:String){
        //Este metodo de sacar colecciones de interes puede ser static porque se repite dos veces To Do
        db.collection(COLECCION_USUARIOS).document(idUsuario).collection(SUBCOLECCION_CATEGORIAS_FAVORITOS)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    arrayCategoriasInteres.clear()
                    for (document in task.result!!) {
                        arrayCategoriasInteres.add(document.toObject(Categoria::class.java))
                    }
                    cargarProductos()
                }
            }
    }

    private fun cargarProductos(){
        arrayProductos.clear()
        for(categoria in arrayCategoriasInteres){
            db.collection(COLECCION_CATEGORIA).document(categoria.id).collection(SUBCOLECCION_PRODUCTOS)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            if(document.data.getValue(CAMPO_IDUSUARIO).toString() != FirestoreUtil.uidUser()){
                                arrayProductos.add(document.toObject(Producto::class.java))
                            }
                        }
                        adapter.setProductos(arrayProductos.sortedBy {it.fecha}.reversed())
                    }
                }
        }
    }
}