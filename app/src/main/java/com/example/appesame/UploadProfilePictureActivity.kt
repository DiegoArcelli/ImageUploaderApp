package com.example.appesame

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UploadProfilePictureActivity : AppCompatActivity() {

    private lateinit var preview : ImageView
    private lateinit var uploadButton : Button
    private lateinit var undoButton : Button
    private lateinit var storageRef : StorageReference
    private lateinit var db : FirebaseFirestore
    private lateinit var user : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_profile_picture)

        val bundle : Bundle = intent.extras!!
        preview = findViewById<ImageView>(R.id.pro_pic_preview)
        val uri = Uri.parse(bundle.getString("uri"))
        preview.setImageURI(uri)

        user = FirebaseAuth.getInstance().currentUser!!

        uploadButton = findViewById<Button>(R.id.upload_pro_pic_button)
        uploadButton.setOnClickListener {
            db = FirebaseFirestore.getInstance()
            storageRef = FirebaseStorage.getInstance().reference

            var picSet : Boolean = false

            db.collection("images")
                .whereEqualTo("user", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        picSet = document["profile_picture"] as Boolean
                    }
                }

            val imageRef = storageRef.child("profile_pics/${user.uid}.jpg")
            if (picSet) {
                imageRef.delete().addOnSuccessListener {
                    imageRef.putFile(uri).addOnSuccessListener {
                        val intent = Intent(this, AppActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {

                imageRef.putFile(uri).addOnSuccessListener {
                    /* db.collection("users").document(user.uid).update("profile_picture", true).addOnSuccessListener {

                    }*/
                    val intent = Intent(this, AppActivity::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this, "ERRRE", Toast.LENGTH_LONG).show()
                }
            }

            undoButton = findViewById(R.id.undo_pro_pic_upload_button)
            undoButton.setOnClickListener {
                Toast.makeText(this, "PIGIATO", Toast.LENGTH_LONG).show()
                val intent = Intent(this, AppActivity::class.java)
                startActivity(intent)
            }

            // val userRef = db.collection("users").document(user.uid).get()
            // val imageRef = storageRef.child("profile_pics/${dbRef.id}.jpg")
            /* dbRef.putFile(uri).addOnSuccessListener {

            } */
        }
    }
}