package net.azarquiel.fukkuapp.Views

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.robertlevonyan.components.picker.ItemModel
import com.robertlevonyan.components.picker.PickerDialog
import kotlinx.android.synthetic.main.activity_add_producto.*
import net.azarquiel.fukkuapp.Model.Categoria
import net.azarquiel.fukkuapp.Model.Imagen
import net.azarquiel.fukkuapp.Model.Producto
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.Util.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import java.util.*
import java.text.SimpleDateFormat

@Suppress("DEPRECATION")
class AddProductoActivity : AppCompatActivity(){

    private lateinit var db: FirebaseFirestore
    private lateinit var arrayStringCategorias:ArrayList<String>
    private lateinit var arrayCategorias:ArrayList<Categoria>
    private var categoriaElegida:String?=null
    private lateinit var pickerDialog: PickerDialog
    private lateinit var riversRef: StorageReference
    private var imagen: Imagen?=null
    private var locationManager : LocationManager? = null
    private var veces = 0
    private lateinit var p: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_producto)

        db = FirebaseFirestore.getInstance()

        // Create persistent LocationManager reference
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        var permissionCheck=ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
            }
        }

        cargarCategorias()
        btnSubirImagen.setOnClickListener { picker() }
        btnSubirProducto.setOnClickListener { comprobarCampos() }
    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("Jonay", "${location.longitude}      ${location.latitude}")
            addProducto(location.longitude,location.latitude)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {
            toast("Tienes que activar la ubicación del movil")
        }
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
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                categoriaElegida=arrayStringCategorias.get(position)
            }
        }
    }

    private fun comprobarCampos() {
        if(!etNombreProducto.text.isNullOrBlank() && !etDescripcionProducto.text.isNullOrBlank() && !etPrecioProducto.text.isNullOrBlank() && !categoriaElegida.isNullOrEmpty()){
            try {
                inicia("Uploading product")
                // Request location updates
                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1L, 0f, locationListener)

            } catch(ex: SecurityException) {
                var permissionCheck=ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                if(permissionCheck == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
                }
            }
        }else{
            toast("Tienes que rellenar todo")
        }
    }

    private fun addProducto(longitude: Double, latitude: Double){
        if(veces == 0){
            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm")
            var producto=Producto("${etNombreProducto.text} ${formatter.format(Date())}","${etNombreProducto.text}", "nombre Usuario","${etDescripcionProducto.text}","${etPrecioProducto.text}",formatter.format(Date()),"${latitude}",
                "${longitude}",arrayCategorias.get(arrayStringCategorias.indexOf(categoriaElegida)).id, arrayCategorias.get(arrayStringCategorias.indexOf(categoriaElegida)).nombre,"KGqBjsuqe0747tCzBeyu")
            addProductoColeccionProductos(producto)
            addProductoColeccionUsuarios(producto)
            addProductoColeccionCategorias(producto)
            veces = 1
            finish()
            finaliza()
        }
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
        inicia("Uploading image")
        var storageRef = FirebaseStorage.getInstance().reference
        riversRef = storageRef.child("images").child(uri.lastPathSegment)
        var uploadTask = riversRef.putFile(uri)
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            Log.d("Jonay", "Ha dado un fallo")
            Log.d("Jonay", uploadTask.exception.toString())
        }.addOnSuccessListener {
            Log.d("Jonay", "Se ha debido de subir")
            finaliza()
        }
        imagen= Imagen(riversRef.name,riversRef.path,riversRef.bucket)
    }

    private fun inicia(texto:String){
        p=indeterminateProgressDialog(texto)
        p.show()
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private fun finaliza(){
        p.hide()
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
