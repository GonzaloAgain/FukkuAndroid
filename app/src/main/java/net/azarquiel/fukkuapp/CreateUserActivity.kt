package net.azarquiel.fukkuapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_user.*
import net.azarquiel.fukkuapp.CreateUserActivity.Companion.TAG

class CreateUserActivity : AppCompatActivity() {

    companion object {
        val TAG="Gonzalo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        btnEntrar.setOnClickListener{
            val completeName: EditText = findViewById(R.id.etName)
            val email: EditText = findViewById(R.id.etEmail)
            val password: EditText = findViewById(R.id.etPassword)

            val user = HashMap<String, Any>()
            user["nombre"] = completeName.text.toString()
            user["email"] = email.text.toString()
            user["password"] = password.text.toString()

            val db = FirebaseFirestore.getInstance()
            db.collection("Usuarios").document(completeName.text.toString())
                .set(user)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

        }
    }
}