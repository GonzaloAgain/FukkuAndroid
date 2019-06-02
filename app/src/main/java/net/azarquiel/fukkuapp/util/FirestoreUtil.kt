package net.azarquiel.fukkuapp.Util

import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.fukkuapp.Model.Categoria
import net.azarquiel.fukkuapp.Model.Producto

object FireStoreUtil {
    var db = FirebaseFirestore.getInstance()

    fun addToCategoriasFavoritas(categoria: Categoria){
        db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(SUBCOLECCION_CATEGORIAS_FAVORITOS)
            .document(categoria.id).set(categoria)
    }

    fun deleteToCategoriasFavoritas(categoria: Categoria){
        db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(SUBCOLECCION_CATEGORIAS_FAVORITOS)
            .document(categoria.id).delete()
    }

    fun addProductoColeccionProductos(producto: Producto){
        db.collection(COLECCION_PRODUCTOS).document(producto.id).set(producto)
    }

    fun addProductoColeccionUsuarios(producto: Producto){
        db.collection(COLECCION_USUARIOS).document("KGqBjsuqe0747tCzBeyu").collection(SUBCOLECCION_PRODUCTOS).document(producto.id).set(producto)
    }

    fun addProductoColeccionCategorias(producto: Producto){
        db.collection(COLECCION_CATEGORIA).document(producto.categoriaId).collection(
            SUBCOLECCION_PRODUCTOS).document(producto.id).set(producto)
    }

}