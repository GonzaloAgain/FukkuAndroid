package net.azarquiel.fukkuapp.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.fukkuapp.model.Categoria
import net.azarquiel.fukkuapp.model.Producto
import net.azarquiel.fukkuapp.model.Chat
import net.azarquiel.fukkuapp.model.ChatChannel
import net.azarquiel.fukkuapp.model.Message
import net.azarquiel.fukkuapp.model.User
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

    fun addToCategoriasFavoritas(categoria: Categoria){
        firestoreInstance.collection(COLECCION_USUARIOS).document(uidUser()).collection(SUBCOLECCION_CATEGORIAS_FAVORITOS)
            .document(categoria.id).set(categoria)
    }

    fun deleteToCategoriasFavoritas(categoria: Categoria){
        firestoreInstance.collection(COLECCION_USUARIOS).document(uidUser()).collection(SUBCOLECCION_CATEGORIAS_FAVORITOS)
            .document(categoria.id).delete()
    }

    fun addToProductosFavoritos(producto: Producto){
        firestoreInstance.collection(COLECCION_USUARIOS).document(uidUser()).collection(SUBCOLECCION_PRODUCTOS_FAVORITOS)
            .document(producto.id).set(producto)
    }

    fun deleteToProductosFavoritos(producto: Producto){
        firestoreInstance.collection(COLECCION_USUARIOS).document(uidUser()).collection(SUBCOLECCION_PRODUCTOS_FAVORITOS)
            .document(producto.id).delete()
    }

    fun updateProducto(idProducto:String, producto:Producto){
        firestoreInstance.collection(COLECCION_USUARIOS).document(uidUser()).collection(SUBCOLECCION_PRODUCTOS).document(idProducto).set(producto)
        firestoreInstance.collection(COLECCION_CATEGORIA).document(producto.categoriaId).collection(SUBCOLECCION_PRODUCTOS).document(idProducto).set(producto)
        firestoreInstance.collection(COLECCION_PRODUCTOS).document(idProducto).set(producto)
    }

    fun addProducto(producto: Producto){
        firestoreInstance.collection(COLECCION_PRODUCTOS).document(producto.id).set(producto)
        firestoreInstance.collection(COLECCION_CATEGORIA).document(producto.categoriaId).collection(
            SUBCOLECCION_PRODUCTOS).document(producto.id).set(producto)
        firestoreInstance.collection(COLECCION_USUARIOS).document(uidUser()).collection(SUBCOLECCION_PRODUCTOS).document(producto.id).set(producto)
    }

    fun deleteProducto(producto:Producto){
        firestoreInstance.collection(COLECCION_USUARIOS).document(uidUser()).collection(SUBCOLECCION_PRODUCTOS).document(producto.id).delete()
        firestoreInstance.collection(COLECCION_CATEGORIA).document(producto.categoriaId).collection(SUBCOLECCION_PRODUCTOS).document(producto.id).delete()
        firestoreInstance.collection(COLECCION_PRODUCTOS).document(producto.id).delete()
    }

    fun getUsers(idProducto: String){
        var arrayUsuarios=ArrayList<User>()
        firestoreInstance.collection(COLECCION_USUARIOS)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        arrayUsuarios.add(document.toObject(User::class.java))
                    }

                }
            }
    }

    fun deleteChat(producto: Producto){
        val docRef  = firestoreInstance.document("$COLECCION_USUARIOS/${uidUser()}/Chats/${producto.id}")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val channelID = document.toObject(Chat::class.java)!!.channelID
                    getOtherUserIDChat(channelID, producto)
                }
            }
    }

    private fun getOtherUserIDChat(channelID: String, producto: Producto) {
        val docRef  = firestoreInstance.document("Canales/$channelID")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val channel = document.toObject(ChatChannel::class.java)
                    for (userID in channel!!.usersID){
                        if (userID != uidUser()) deleteChats(channelID, userID, producto)
                    }
                }
            }
    }

    private fun deleteChats(channelID: String, otherUserID: String, producto: Producto) {
        firestoreInstance.document("$COLECCION_USUARIOS/${uidUser()}/Chats/${producto.id}").delete()
        firestoreInstance.document("$COLECCION_USUARIOS/$otherUserID/Chats/${producto.id}").delete()
        firestoreInstance.document("Canales/$channelID").delete()
    }

    fun uidUser():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun nameUser():String{
        return FirebaseAuth.getInstance().currentUser!!.displayName!!
    }
}