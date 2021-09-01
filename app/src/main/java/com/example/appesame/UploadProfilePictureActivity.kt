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
import com.google.firebase.firestore.DocumentReference
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

            val docRef : DocumentReference = db.collection("users").document(user.uid)
            docRef.addSnapshotListener {snapshot, e ->
                picSet = snapshot!!.get("profile_picture") as Boolean
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
                    db.collection("users").document(user.uid).update("profile_picture", true).addOnSuccessListener {
                        startActivity(Intent(this, AppActivity::class.java))
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "ERRRE", Toast.LENGTH_LONG).show()
                }
            }

            undoButton = findViewById<Button>(R.id.undo_pro_pic_upload_button)
            undoButton.setOnClickListener{
                Toast.makeText(this, "PIGIATO", Toast.LENGTH_LONG).show()
                startActivity(Intent(it.context, AppActivity::class.java))
            }

        }
    }
}