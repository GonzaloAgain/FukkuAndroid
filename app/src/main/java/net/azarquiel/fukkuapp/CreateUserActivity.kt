package net.azarquiel.fukkuapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_create_user.*

class CreateUserActivity : AppCompatActivity() {

    companion object {
        val TAG="Gonzalo"
    }

    private lateinit var completeName: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        auth = FirebaseAuth.getInstance()


        btnEntrar.setOnClickListener{
            completeName = etCompleteName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            createAccount(email, password)

            /*val user = HashMap<String, Any>()
            user["nombre"] = completeName
            user["email"] = email
            user["password"] = password

            val db = FirebaseFirestore.getInstance()
            db.collection("Usuarios").document(completeName)
                .set(user)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }*/

        }
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    addDisplayName(user)

                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addDisplayName(user: FirebaseUser?) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(completeName)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                }
            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val nombre = etCompleteName.text.toString()
        if (TextUtils.isEmpty(nombre)) {
            etCompleteName.error = "Required."
            valid = false
        } else {
            etCompleteName.error = null
        }

        val email = etEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            etEmail.error = "Required."
            valid = false
        } else {
            etEmail.error = null
        }

        val password = etPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            etPassword.error = "Required."
            valid = false
        } else {
            etPassword.error = null
        }

        return valid
    }

}