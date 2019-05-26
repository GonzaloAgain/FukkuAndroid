package net.azarquiel.fukkuapp.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_chat.*
import net.azarquiel.fukkuapp.AppConstants
import net.azarquiel.fukkuapp.R
import net.azarquiel.fukkuapp.model.Message
import net.azarquiel.fukkuapp.util.FirestoreUtil
import net.azarquiel.fukkuapp.viewmodel.MessagesAdapter
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val db = FirebaseFirestore.getInstance()
        val channelID = intent.getStringExtra(AppConstants.CHANNEL_ID)
        val otherUserID = intent.getStringExtra(AppConstants.OTHER_USER_ID)

        adapter = MessagesAdapter(this, R.layout.row_message)
        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = adapter

        db.collection("Canales/${channelID}/Mensajes")
            .orderBy("time")
            .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e)
                    return@EventListener
                }

                val mensajes = ArrayList<Message>()
                for (doc in value!!) {
                    mensajes.add(doc.toObject(Message::class.java))
                }

                adapter.setMessages(mensajes)
                rvMessages.scrollToPosition(adapter.itemCount-1)
            })



        btnSendMessage.setOnClickListener {

            val mensaje = Message(etMessage.text.toString(),
                Calendar.getInstance().time,
                FirebaseAuth.getInstance().currentUser!!.uid,
                otherUserID,
                FirebaseAuth.getInstance().currentUser!!.displayName!!,
                channelID)
            etMessage.text.clear()

            FirestoreUtil.sendMessage(mensaje,channelID)
        }

    }

}
