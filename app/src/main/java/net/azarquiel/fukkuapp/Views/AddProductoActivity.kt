package net.azarquiel.fukkuapp.Views

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.robertlevonyan.components.picker.ItemModel
import com.robertlevonyan.components.picker.PickerDialog
import kotlinx.android.synthetic.main.activity_add_producto.*
import net.azarquiel.fukkuapp.Class.Categoria
import net.azarquiel.fukkuapp.Class.Imagen
import net.azarquiel.fukkuapp.Class.Producto
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.Util.*
import org.jetbrains.anko.toast
import java.util.*
import java.text.SimpleDateFormat


class AddProductoActivity : AppCompatActivity(){

    private lateinit var db: FirebaseFirestore
    private lateinit var arrayStringCategorias:ArrayList<String>
    private lateinit var arrayCategorias:ArrayList<Categoria>
    private var categoriaElegida:String?=null
    private lateinit var pickerDialog: PickerDialog
    private lateinit var riversRef: StorageReference
    private var imagen: Imagen?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_producto)
        db = FirebaseFirestore.getInstance()

        cargarCategorias()
        btnSubirImagen.setOnClickListener { picker() }
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
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        var producto=Producto("${etNombreProducto.text} ${formatter.format(Date())}","${etNombreProducto.text}","${etDescripcionProducto.text}","${etPrecioProducto.text}",formatter.format(Date()),"",
            "",arrayCategorias.get(arrayStringCategorias.indexOf(categoriaElegida)).id,"KGqBjsuqe0747tCzBeyu")
        addProductoColeccionProductos(producto)
        addProductoColeccionUsuarios(producto)
        addProductoColeccionCategorias(producto)
        finish()
    }

    private fun addProductoColeccionProductos(producto:Producto){
        db.collection(COLECCION_PRODUCTOS).document(producto.id).set(producto)
        if(imagen != null){
            db.collection(COLECCION_PRODUCTOS).document(producto.id).collection(SUBCOLECCION_IMAGENES).document(riversRef.name).set(imagen!!)
        }
    }

    private fun addProductoColeccionUsuarios(producto: Producto){
        db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(SUBCOLECCION_PRODUCTOS).document(producto.id).set(producto)
        if(imagen != null){
            db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(SUBCOLECCION_PRODUCTOS).document(producto.id).collection(SUBCOLECCION_IMAGENES).document(riversRef.name).set(imagen!!)
        }
    }

    private fun addProductoColeccionCategorias(producto: Producto){
        db.collection(COLECCION_CATEGORIA).document(arrayCategorias.get(arrayStringCategorias.indexOf(categoriaElegida)).id).collection(
            SUBCOLECCION_PRODUCTOS).document(producto.id).set(producto)
        if(imagen != null){
            db.collection(COLECCION_CATEGORIA).document(arrayCategorias.get(arrayStringCategorias.indexOf(categoriaElegida)).id).collection(
                SUBCOLECCION_PRODUCTOS).document(producto.id).collection(SUBCOLECCION_IMAGENES).document(riversRef.name).set(imagen!!)
        }
    }

    private fun picker(){
        val itemModelc = ItemModel(ItemModel.ITEM_CAMERA)
        val itemModelg = ItemModel(ItemModel.ITEM_GALLERY)
        pickerDialog = PickerDialog.Builder(this)
            .setListType(PickerDialog.TYPE_GRID)
            .setItems(arrayListOf(itemModelg, itemModelc))
            .setDialogStyle(PickerDialog.DIALOG_MATERIAL)
            .create()

        pickerDialog.setPickerCloseListener { type, uri ->
            when (type) {
                ItemModel.ITEM_CAMERA -> {
                    subirImagen(uri)
                }
                ItemModel.ITEM_GALLERY -> {
                    subirImagen(uri)
                }
            }
        }

        pickerDialog.show(supportFragmentManager, "")
    }

    private fun subirImagen(uri : Uri){
        var storageRef = FirebaseStorage.getInstance().reference
        riversRef = storageRef.child("images").child(uri.lastPathSegment)
        var uploadTask = riversRef.putFile(uri)
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            Log.d("Jonay", "Ha dado un fallo")
            Log.d("Jonay", uploadTask.exception.toString())
        }.addOnSuccessListener {
            Log.d("Jonay", "Se ha debido de subir")
        }
        imagen= Imagen(riversRef.name,riversRef.path,riversRef.bucket)
    }
}
