package net.azarquiel.fukkuapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private lateinit var messageReceiverName: String
    private lateinit var messageReceiverID: String
    private lateinit var messageSenderID: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth = FirebaseAuth.getInstance()
        messageSenderID = auth.currentUser!!.uid
        database = FirebaseFirestore.getInstance()

        messageReceiverID = "uDi9Nbm2Pjb1zUi5f7SAOSu57wY2"
        messageReceiverName = "Jonay Molero Medina de Ávila"


        btnSendMessage.setOnClickListener {

        }

    }

}
