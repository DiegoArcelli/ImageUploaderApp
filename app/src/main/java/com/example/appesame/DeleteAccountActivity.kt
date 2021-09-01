package com.example.appesame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class DeleteAccountActivity : AppCompatActivity() {

    private lateinit var undoButton : Button
    private lateinit var deleteButton : Button
    private lateinit var pwdBox : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        val user = FirebaseAuth.getInstance().currentUser

        deleteButton = findViewById(R.id.account_remove_button)
        pwdBox = findViewById(R.id.delete_password_box)
        deleteButton.setOnClickListener {
            val text = pwdBox.text.toString()
            if (text != "") {
                val credentials = EmailAuthProvider.getCredential(user!!.email.toString(), text)
                user.reauthenticate(credentials).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.delete().addOnCompleteListener {
                            Toast.makeText(this, "Account deleted", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, FormActivity::class.java))
                        }
                    } else {
                        Toast.makeText(this, "Wrong password", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        undoButton = findViewById(R.id.undo_account_delete)
        undoButton.setOnClickListener {
            startActivity(Intent(this, AppActivity::class.java))
        }


    }
}