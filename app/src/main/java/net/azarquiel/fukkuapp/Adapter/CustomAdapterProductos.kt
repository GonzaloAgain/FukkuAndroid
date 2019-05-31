package net.azarquiel.fukkuapp.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.productosrow.view.*
import net.azarquiel.fukkuapp.Model.Producto
import net.azarquiel.fukkuapp.R

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
            mostrarImagen(itemView, dataItem.imagen)
            itemView.tvNombreProducto.text=dataItem.nombre
            itemView.tvDescripcionProducto.text=dataItem.descripcion
            itemView.tvFechaProducto.text=dataItem.fecha
            itemView.tvPrecioProducto.text=dataItem.precio
            itemView.tag=dataItem
        }

        private fun mostrarImagen(itemView:View,imagen:String){
            if(imagen != ""){
                var storageRef = FirebaseStorage.getInstance().reference
                storageRef.child(imagen).downloadUrl.addOnSuccessListener {
                    Picasso.with(context).load(it).into(itemView.ivImagenProducto)
                }
            }else{
                itemView.ivImagenProducto.setImageResource(R.drawable.notfound)
            }
        }
    }
}