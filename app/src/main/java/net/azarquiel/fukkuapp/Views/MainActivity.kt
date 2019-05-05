package net.azarquiel.fukkuapp.Views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import net.azarquiel.fukkuapp.Class.ViewPagerAdapter
import net.azarquiel.fukkuapp.Fragments.Fragment_productos_por_categoria_fav
import net.azarquiel.fukkuapp.Fragments.Fragment_productos_por_cercania
import net.azarquiel.fukkuapp.R
import org.jetbrains.anko.toast
import net.azarquiel.fukkuapp.Util.*
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.StorageReference
import java.io.File


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            addProducto()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        setupViewPager(viewPager)
        tabs.setupWithViewPager(viewPager)

        //val db = FirebaseFirestore.getInstance()
        //val categoria = Categoria("categoria1", "Categoria 1", "icono de categoria 1")
        //db.collection("Categorias").document("categoria1").set(categoria)
        //val producto = Producto("producto 1", "producto 1","producto 1","producto 1","producto 1","producto 1","producto 1","producto 1","producto 1")
        //db.collection("Categorias").document("categoria1").collection("Productos").add(producto)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_productos -> {
                productos(TUS_PRODUCTOS)
            }
            R.id.nav_productosFav -> {
                productos(TUS_PRODUCTOS_FAVORITOS)
            }
            R.id.nav_categoriasFav -> {
                categorias()
            }
            R.id.nav_chat -> {

            }
            R.id.nav_cerrarSesion -> {

            }
            R.id.nav_exit -> {
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(Fragment_productos_por_categoria_fav(), "Productos por categoria de interes")
        adapter.addFragment(Fragment_productos_por_cercania(), "Productos por cercania")
        viewPager.adapter = adapter
    }

    private fun productos(accion : String){
        var intent= Intent(this, ProductosActivity::class.java)
        intent.putExtra("accion", accion)
        startActivity(intent)
    }

    private fun categorias(){
        var intent= Intent(this, CategoriaActivity::class.java)
        startActivity(intent)
    }

    private fun addProducto(){
        var intent= Intent(this, AddProductoActivity::class.java)
        startActivity(intent)
    }

    private fun subirArchivoPrueba(){
        var storageRef = FirebaseStorage.getInstance().reference

        var file = Uri.fromFile(File("path/to/images/rivers.jpg"))
        val riversRef = storageRef.child("images/${file.lastPathSegment}")
        var uploadTask = riversRef.putFile(file)
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...

        }
    }

    private fun bajarFoto(){

    }
}
