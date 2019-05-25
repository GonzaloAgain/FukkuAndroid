package net.azarquiel.fukkuapp.util

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.fukkuapp.view.CreateUserActivity
import net.azarquiel.fukkuapp.model.ChatChannel
import net.azarquiel.fukkuapp.model.Message
import net.azarquiel.fukkuapp.model.User

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
                                onComplete: (channelID: String) -> Unit){
        currentUserDocRef.collection("Chats")
            .document(otherUserID).get().addOnSuccessListener {
                if (it.exists()){
                    onComplete(it["channelID"] as String)
                    return@addOnSuccessListener
                }

                val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserID, otherUserID)))

                currentUserDocRef
                    .collection("Chats")
                    .document(otherUserID)
                    .set(mapOf("channelID" to newChannel.id))

                firestoreInstance.collection("Usuarios").document(otherUserID)
                    .collection("Chats")
                    .document(currentUserID)
                    .set(mapOf("channelID" to newChannel.id))

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
}