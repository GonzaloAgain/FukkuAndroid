package net.azarquiel.fukkuapp.Util

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.fukkuapp.Views.MainActivity

object GenerarEstructura {

    fun generar(){
        val db = FirebaseFirestore.getInstance()
        collectionUsers(db)
        collectionCategoria(db)
        collectionProducto(db)
        collectionImagenes(db)
    }

    fun collectionCategoria(db: FirebaseFirestore) {
        // Create collection categoria
        val categoria : HashMap<String, String> = HashMap()
        categoria.put("Nombre", "Coches")

        // Add a new document with a generated ID
        db.collection("Categorias")
            .add(categoria)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    MainActivity.TAG,
                    "DocumentSnapshot added with ID: " + documentReference.id
                )
            }
            .addOnFailureListener { e -> Log.w(MainActivity.TAG, "Error adding document", e) }
    }

    fun collectionProducto(db: FirebaseFirestore){
        // Create collection producto
        val producto : HashMap<String, String> = HashMap()
        producto.put("Nombre", "Mercedes")
        producto.put("Descripcion", "Un coche mercedes de 2008")
        producto.put("Precio", "1000€")
        producto.put("Fecha", "10/10/2018")
        producto.put("Longitud", "123456")
        producto.put("Latitud", "987654")

        // Add a new document with a generated ID
        db.collection("Productos")
            .add(producto)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    MainActivity.TAG,
                    "DocumentSnapshot added with ID: " + documentReference.id
                )
            }
            .addOnFailureListener { e -> Log.w(MainActivity.TAG, "Error adding document", e) }
    }

    fun collectionImagenes(db: FirebaseFirestore){
        // Create collection imagenes
        val imagenes : HashMap<String, String> = HashMap()
        imagenes.put("Imagen", "Imagen del mercedes")

        // Add a new document with a generated ID
        db.collection("Imagenes")
            .add(imagenes)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    MainActivity.TAG,
                    "DocumentSnapshot added with ID: " + documentReference.id
                )
            }
            .addOnFailureListener { e -> Log.w(MainActivity.TAG, "Error adding document", e) }
    }

    fun collectionUsers(db: FirebaseFirestore){
        // Create collection user
        val users : HashMap<String, String> = HashMap()
        users.put("Nombre", "Pepe")
        users.put("Apellidos", "Garcia Hernandez")
        users.put("Nick", "Pepe89")
        users.put("Avatar", "imagen de pepe")
        users.put("Email", "Pepe@gmail.com")
        users.put("Password", "pepegarcia")
        users.put("Nacimiento", "17/5/2000")
        users.put("Latitud", "54654654")
        users.put("Longitud", "3213213")

        // Add a new document with a generated ID
        db.collection("Usuarios")
            .add(users)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    MainActivity.TAG,
                    "DocumentSnapshot added with ID: " + documentReference.id
                )
            }
            .addOnFailureListener { e -> Log.w(MainActivity.TAG, "Error adding document", e) }
    }
}