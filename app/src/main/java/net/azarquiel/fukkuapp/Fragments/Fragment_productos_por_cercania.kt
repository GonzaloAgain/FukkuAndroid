package net.azarquiel.fukkuapp.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_productos_cerca_de_ti.*
import net.azarquiel.fukkuapp.Adapter.CustomAdapterProductos
import net.azarquiel.fukkuapp.Model.Producto
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.Util.*
import org.jetbrains.anko.toast

class Fragment_productos_por_cercania : Fragment(){

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter : CustomAdapterProductos
    private var locationManager : LocationManager? = null
    private lateinit var arrayProductosCercanos : ArrayList<Producto>
    private var locationUser = Location("Location user")
    private var locationProduct = Location("Location product")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        val rootView = inflater.inflate(R.layout.fragment_productos_cerca_de_ti, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Create persistent LocationManager reference
        arrayProductosCercanos = ArrayList()
        db = FirebaseFirestore.getInstance()
        locationManager = activity!!.applicationContext.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
        crearAdapter()
        ubicacion()
        refreshProductosCercanos.setOnRefreshListener {
            ubicacion()
            refreshProductosCercanos.isRefreshing=false
        }
    }

    private fun crearAdapter() {
        adapter= CustomAdapterProductos(activity!!.applicationContext, R.layout.productosrow)
        rvProductosCercanos.layoutManager= LinearLayoutManager(activity!!.applicationContext)
        rvProductosCercanos.adapter=adapter
    }

    private fun ubicacion(){
        try {
            // Request location updates
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1L, 500F, locationListener)

        } catch(ex: SecurityException) {
            var permissionCheck=ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
            if(permissionCheck == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
            }
        }
    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            getProductsNearby(location.latitude,location.longitude)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {
            activity!!.applicationContext.toast("Debes activar la ubicación para poder ver los productos cercanos")
        }
    }

    private fun getProductsNearby(latitud:Double, longitud:Double){
        locationUser.latitude=latitud
        locationUser.longitude=longitud
        
        db.collection(COLECCION_PRODUCTOS).orderBy(CAMPO_FECHA, Query.Direction.DESCENDING).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    arrayProductosCercanos.clear()
                    for (document in task.result!!) {
                        if(checkDistance("${document.data.getValue(CAMPO_LATITUD)}", "${document.data.getValue(
                                CAMPO_LONGITUD)}")){
                            arrayProductosCercanos.add(document.toObject(Producto::class.java))
                        }
                    }
                    adapter.setProductos(arrayProductosCercanos)
                }
            }
    }
    
    private fun checkDistance(latitud:String, longitud:String) : Boolean{
        if(!latitud.isEmpty() && !longitud.isEmpty()){
            locationProduct.latitude = latitud.toDouble()
            locationProduct.longitude = longitud.toDouble()
            val distancia = locationProduct.distanceTo(locationUser)

            if(distancia < METROS_PRODUCTOS_CERCANOS){
                return true
            }
        }
        return false
    }
}