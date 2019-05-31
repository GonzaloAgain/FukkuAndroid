package net.azarquiel.fukkuapp.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.categoriasrow.view.*
import net.azarquiel.fukkuapp.Model.Categoria
import net.azarquiel.fukkuapp.R

class CustomAdapterCategorias(val context: Context,
                    val layout: Int
                    ) : RecyclerView.Adapter<CustomAdapterCategorias.ViewHolder>() {

    private var dataList: List<Categoria> = emptyList()

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

    internal fun setCategorias(categorias: List<Categoria>) {
        this.dataList = categorias
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Categoria){
            mostrarIcono(dataItem.icono,itemView)
            itemView.tvNombreCategoria.text=dataItem.nombre
            itemView.tag=dataItem
        }

        private fun mostrarIcono(icono_categoria: String,itemView:View){
            if(icono_categoria.isNotEmpty()){
                var storageRef = FirebaseStorage.getInstance().reference
                storageRef.child(icono_categoria).downloadUrl.addOnSuccessListener {
                    Picasso.with(context).load(it).into(itemView.ivIconoProducto)
                }.addOnFailureListener {
                    // Handle any errors
                    Log.d("Jonay","Error iconos ${it.message}")
                }
            }else{
                itemView.ivIconoProducto.setImageResource(R.drawable.notfound)
            }
        }
    }
}