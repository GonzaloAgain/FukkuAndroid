package net.azarquiel.fukkuapp.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.productosrow.view.*
import net.azarquiel.fukkuapp.Class.Imagen
import net.azarquiel.fukkuapp.Class.Producto
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.Util.*

class CustomAdapterProductos(val context: Context,
                    val layout: Int
                    ) : RecyclerView.Adapter<CustomAdapterProductos.ViewHolder>() {

    private var dataList: List<Producto> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setProductos(productos: List<Producto>) {
        this.dataList = productos
        notifyDataSetChanged()
    }

    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Producto){
            cargarImagen(dataItem.id,itemView)
            itemView.tvNombreProducto.text=dataItem.nombre
            itemView.tvDescripcionProducto.text=dataItem.descripcion
            itemView.tvFechaProducto.text=dataItem.fecha
            itemView.tvPrecioProducto.text=dataItem.precio
            itemView.tag=dataItem
        }

        private fun cargarImagen(id:String,itemView:View){
            var db=FirebaseFirestore.getInstance()
            var imagen:Imagen?=null

            db.collection(COLECCION_PRODUCTOS).document(id).collection(SUBCOLECCION_IMAGENES).limit(1)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for(document in task.result!!) {
                            imagen= Imagen("${document.data.getValue(CAMPO_ID)}","${document.data.getValue(CAMPO_IMAGEN)}","${document.data.getValue(
                                CAMPO_PRODUCTOID)}")
                        }
                        mostrarImagen(itemView,imagen)
                    }
                }
        }

        private fun mostrarImagen(itemView:View,imagen:Imagen?){
            imagen?.let {
                var storageRef = FirebaseStorage.getInstance().reference

                storageRef.child(imagen.imagen).downloadUrl.addOnSuccessListener {
                    Picasso.with(context).load(it).into(itemView.ivImagenProducto)
                }.addOnFailureListener {
                    // Handle any errors
                }
            }?: run {
                itemView.ivImagenProducto.setImageResource(R.drawable.notfound)
            }
        }
    }
}