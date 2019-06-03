package net.azarquiel.fukkuapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
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
            if (dataItem.nombreCategoria != "") {
                itemView.tvCategoriaProducto.text=dataItem.nombreCategoria
                itemView.tvCategoriaProducto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0F)
            } else {
                itemView.tvCategoriaProducto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 0.0F)
            }
            itemView.tvFechaProducto.text=dataItem.fecha
            itemView.tvPrecioProducto.text= dataItem.precio + "€"
            itemView.tag=dataItem
        }

        private fun mostrarImagen(itemView:View,imagen:String){
            if(imagen != ""){
                Glide.with(context).load(imagen).into(itemView.ivImagenProducto)
            }else{
                itemView.ivImagenProducto.setImageResource(R.drawable.notfound)
            }
        }
    }
}