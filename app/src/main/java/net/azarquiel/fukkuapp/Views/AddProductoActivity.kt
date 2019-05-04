package net.azarquiel.fukkuapp.Views

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_producto.*
import net.azarquiel.fukkuapp.Class.Categoria
import net.azarquiel.fukkuapp.Class.Producto
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.Util.*
import org.jetbrains.anko.toast
import java.util.*


class AddProductoActivity : AppCompatActivity(){

    private lateinit var db: FirebaseFirestore
    private lateinit var arrayStringCategorias:ArrayList<String>
    private lateinit var arrayCategorias:ArrayList<Categoria>
    private var categoriaElegida:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_producto)
        db = FirebaseFirestore.getInstance()

        cargarCategorias()
        btnSubirProducto.setOnClickListener { subirProducto() }
    }

    private fun cargarCategorias(){
        arrayStringCategorias= ArrayList()
        arrayCategorias= ArrayList()
        db.collection(COLECCION_CATEGORIA)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        arrayStringCategorias.add("${document.data.getValue(CAMPO_NOMBRE)}")
                        arrayCategorias.add(Categoria(document.id,"${document.data.getValue(CAMPO_NOMBRE)}",""))
                    }
                    cargarSpinner()
                }
            }
    }

    private fun cargarSpinner(){
        spinnerCategorias.adapter=ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayStringCategorias)
        spinnerCategorias.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                categoriaElegida=arrayStringCategorias.get(position)
            }
        }
    }

    private fun subirProducto(){
        if(!etNombreProducto.text.isNullOrBlank() && !etDescripcionProducto.text.isNullOrBlank() && !etPrecioProducto.text.isNullOrBlank() && !categoriaElegida.isNullOrEmpty()){
            addProducto()
        }else{
            toast("Tienes que rellenar todo")
        }
    }

    private fun addProducto(){
        var producto=Producto("${etNombreProducto.text}${Date()}","${etNombreProducto.text}","${etDescripcionProducto.text}","${etPrecioProducto.text}","${Date()}","",
            "",arrayCategorias.get(arrayStringCategorias.indexOf(categoriaElegida)).id,"KGqBjsuqe0747tCzBeyu")
        db.collection(COLECCION_PRODUCTOS).document(producto.id).set(producto)
        db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(SUBCOLECCION_PRODUCTOS).document(producto.id).set(producto)
        db.collection(COLECCION_CATEGORIA).document(arrayCategorias.get(arrayStringCategorias.indexOf(categoriaElegida)).id).collection(
            SUBCOLECCION_PRODUCTOS).document(producto.id).set(producto)
    }
}
