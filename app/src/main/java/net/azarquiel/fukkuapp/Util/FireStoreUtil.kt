package net.azarquiel.fukkuapp.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.fukkuapp.Model.Categoria
import net.azarquiel.fukkuapp.Model.Producto
import net.azarquiel.fukkuapp.model.*
import net.azarquiel.fukkuapp.views.CreateUserActivity

object FirestoreUtil {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("Usuarios/${FirebaseAuth.getInstance().uid
            ?: throw  NullPointerException("UID es null")}")

    fun createUserFirestore(userFirestore: User){
        currentUserDocRef.set(userFirestore)
            .addOnSuccessListener { Log.d(CreateUserActivity.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(CreateUserActivity.TAG, "Error writing document", e) }
    }

    private val chatChannelCollectionRef = firestoreInstance.collection("Canales")

    fun getOrCreateChatChannel( otherUserID: String,
                                productID: String,
                                onComplete: (channelID: String) -> Unit){
        currentUserDocRef.collection("Chats")
            .document(productID).get().addOnSuccessListener {
                if (it.exists()){
                    onComplete(it.toObject(Chat::class.java)!!.channelID)
                    return@addOnSuccessListener
                }

                val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserID, otherUserID)))

                currentUserDocRef
                    .collection("Chats")
                    .document(productID)
                    .set(Chat(newChannel.id,productID))

                firestoreInstance.collection("Usuarios").document(otherUserID)
                    .collection("Chats")
                    .document(productID)
                    .set(Chat(newChannel.id,productID))

                onComplete(newChannel.id)
            }
    }

    fun sendMessage(message: Message, channelID: String){
        chatChannelCollectionRef.document(channelID)
            .collection("Mensajes")
            .add(message)
    }

    fun addToCategoriasFavoritas(categoria: Categoria){
        firestoreInstance.collection(COLECCION_USUARIOS).document(FirebaseAuth.getInstance().currentUser!!.uid).collection(SUBCOLECCION_CATEGORIAS_FAVORITOS)
            .document(categoria.id).set(categoria)
    }

    fun deleteToCategoriasFavoritas(categoria: Categoria){
        firestoreInstance.collection(COLECCION_USUARIOS).document(FirebaseAuth.getInstance().currentUser!!.uid).collection(SUBCOLECCION_CATEGORIAS_FAVORITOS)
            .document(categoria.id).delete()
    }

    fun addProductoColeccionProductos(producto: Producto){
        firestoreInstance.collection(COLECCION_PRODUCTOS).document(producto.id).set(producto)
    }

    fun addProductoColeccionUsuarios(producto: Producto){
        firestoreInstance.collection(COLECCION_USUARIOS).document(FirebaseAuth.getInstance().currentUser!!.uid).collection(SUBCOLECCION_PRODUCTOS).document(producto.id).set(producto)
    }

    fun addProductoColeccionCategorias(producto: Producto){
        firestoreInstance.collection(COLECCION_CATEGORIA).document(producto.categoriaId).collection(
            SUBCOLECCION_PRODUCTOS).document(producto.id).set(producto)
    }

    //region FCM
    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit){
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(User::class.java)!!
            onComplete(user.registrationTokens)
        }
    }

    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>){
        currentUserDocRef.update("registrationTokens", registrationTokens)
    }
    //endregion FCM
}