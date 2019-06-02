package net.azarquiel.fukkuapp.views

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import net.azarquiel.fukkuapp.Model.Categoria
import net.azarquiel.fukkuapp.Model.ViewPagerAdapter
import net.azarquiel.fukkuapp.fragments.Fragment_categorias
import net.azarquiel.fukkuapp.fragments.Fragment_productos_por_categoria_fav
import net.azarquiel.fukkuapp.fragments.Fragment_productos_por_cercania
import net.azarquiel.fukkuapp.Model.Producto
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.util.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var db: FirebaseFirestore

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
        db = FirebaseFirestore.getInstance()
        setupViewPager(viewPager)
        tabs.setupWithViewPager(viewPager)
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
                startActivity(intentFor<OpenChatsActivity>())
            }
            R.id.nav_cerrarSesion -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(intentFor<LoginActivity>().newTask().clearTask())
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
        adapter.addFragment(Fragment_categorias(), "Categorías")
        adapter.addFragment(Fragment_productos_por_cercania(), "Productos por cercania")
        adapter.addFragment(Fragment_productos_por_categoria_fav(), "Productos por categoria de interes")
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

    fun pulsarCategoria(v: View){
        val categoria=v.tag as Categoria
        var intent= Intent(this, Productos_de_un_categoria::class.java)
        intent.putExtra("categoria", categoria)
        startActivity(intent)
    }

    fun pinchaProducto(v: View){
        val producto = v.tag as Producto
        db.collection(COLECCION_PRODUCTOS).document(producto.id).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var document = task.result
                    if(document!!.exists()){
                        var intent= Intent(this, DetailProductActivity::class.java)
                        intent.putExtra("producto", document.toObject(Producto::class.java))
                        startActivity(intent)
                    }else{
                        toast("Es posible que el producto haya sido borrado")
                    }
                }
            }
    }
}
