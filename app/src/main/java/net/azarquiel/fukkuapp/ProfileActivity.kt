package net.azarquiel.fukkuapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.content_profile.*
import net.azarquiel.fukkuapp.model.User
import org.jetbrains.anko.*
import java.util.*


class ProfileActivity : AppCompatActivity() {

    private lateinit var user: FirebaseUser
    private lateinit var docRef: DocumentReference
    private lateinit var db: FirebaseFirestore
    private lateinit var userFirestore: User
    private var editable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        itProfileBirthday.inputType = InputType.TYPE_NULL
        itProfileGender.inputType = InputType.TYPE_NULL

        user = FirebaseAuth.getInstance().currentUser!!

        editable = !editable

        getUser()
        editData()

        fab.setOnClickListener {
            /*editable = !editable
            editData()*/

            updateUser()
        }

        btnProfileChangePass.setOnClickListener {
            alert {
                customView {
                    title = "Cambiar contraseña"
                    verticalLayout {
                        val etPass = editText {
                            hint = "Introduzca la nueva contraseña"
                            padding = dip(20)
                        }
                        val etPassConfirm = editText {
                            hint = "Confirme la nueva contraseña"
                            padding = dip(20)
                        }
                        positiveButton("Enviar") {
                            if (TextUtils.isEmpty(etPass.text) || TextUtils.isEmpty(etPassConfirm.text)){
                                longToast("Debes rellenar todos los campos")
                            } else {
                                if (etPass.text.toString() == etPassConfirm.text.toString()){
                                    val newPassword = etPass.text.toString()

                                    user?.updatePassword(newPassword)
                                        ?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Log.d("Password", "User password updated.")
                                            }
                                        }

                                    longToast("Contraseña cambiada")
                                } else {
                                    longToast("Las contraseñas no coinciden")
                                }
                            }
                        }
                        negativeButton("Cancelar") {
                        }
                    }
                }
            }.show()
        }

        itProfileBirthday.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { datePicker, mYear, mMonth, mDay ->
                    itProfileBirthday.setText("$mDay/$mMonth/$mYear")
                }, year, month, day
            )

            datePickerDialog.show()
        }

        itProfileGender.setOnClickListener {
            lateinit var dialog: AlertDialog
            // Initialize an array of genders
            val array = arrayOf("Hombre","Mujer")

            // Initialize a new instance of alert dialog builder object
            val builder = AlertDialog.Builder(this)

            // Set a title for alert dialog
            builder.setTitle("Sexo")

            // Set the single choice items for alert dialog with initial selection
            builder.setSingleChoiceItems(array,-1) { _, which->
                // Get the dialog selected item
                val gender = array[which]

                itProfileGender.setText(gender)

                // Dismiss the dialog
                dialog.dismiss()
            }

            // Initialize the AlertDialog using builder object
            dialog = builder.create()
            // Finally, display the alert dialog
            dialog.show()
        }
    }

    private fun updateUser() {
        user?.updateEmail(itProfileEmail.text.toString())
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Email", "User email address updated.")
                }
            }

        docRef.update(mapOf(
                "name" to itProfileName.text.toString(),
                "surnames" to itProfileSurnames.text.toString(),
                //"gender" to itProfileGender.text.toString(),
                //"birthday" to itProfileBirthday.text.toString(),
                "email" to itProfileEmail.text.toString()
            ))
    }

    private fun editData() {
        itProfileName.isEnabled = editable
        itProfileSurnames.isEnabled = editable
        itProfileBirthday.isEnabled = editable
        itProfileGender.isEnabled = editable
        itProfileEmail.isEnabled = editable
        if (!editable) btnProfileChangePass.visibility = INVISIBLE else btnProfileChangePass.visibility = VISIBLE
    }

    private fun getUser() {
        db = FirebaseFirestore.getInstance()
        docRef = db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid)
        docRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.w("PROFILE", "Listen failed.", e)
                return@EventListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("PROFILE", "Current data: ${snapshot.data}")
                userFirestore = snapshot.toObject(User::class.java)!!
                pintar()

            } else {
                Log.d("PROFILE", "Current data: null")
            }
        })
    }

    private fun pintar() {

        tvProfileName.text = userFirestore.name + " ." + userFirestore.surnames.substring(0,1)

        itProfileName.setText(userFirestore.name)
        itProfileSurnames.setText(userFirestore.surnames)

        itProfileEmail.setText(userFirestore.email)
    }

}
