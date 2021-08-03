package com.example.appesame

import android.app.ProgressDialog
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileDescriptor
import java.sql.Date
import java.sql.Timestamp

class ImageUploaderActivity : AppCompatActivity() {

    private lateinit var preview : ImageView
    private lateinit var uploadButton : Button
    private lateinit var undoButton : Button
    private lateinit var description : EditText
    private lateinit var storageRef : StorageReference
    private lateinit var db : FirebaseFirestore
    private lateinit var user : FirebaseUser
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_uploader)

        // retrieving the uri of the images selected by the user
        val bundle : Bundle = intent.extras!!
        preview = findViewById<ImageView>(R.id.image_preview)
        val uri = Uri.parse(bundle.getString("uri"))
        preview.setImageURI(uri)

        user = FirebaseAuth.getInstance().currentUser!!
        description = findViewById(R.id.description_box)

        uploadButton = findViewById<Button>(R.id.upload_image_button)
        uploadButton.setOnClickListener{
            Log.d("URI", uri.path!!.toString())
            Log.d("USER", user!!.uid)

            // store the image in the database
            db = FirebaseFirestore.getInstance()
            storageRef = FirebaseStorage.getInstance().reference
            val dbRef = db.collection("images").document()
            val imageRef = storageRef.child("images/${dbRef.id}.jpg")

            // handels the success or the error of uploading the image
            imageRef.putFile(uri).addOnSuccessListener {

                val imgInfo : HashMap<String, Any?> = HashMap()
                imgInfo.put("file_name", "${dbRef.id}.jpg")
                imgInfo.put("description", description.text.toString())
                imgInfo.put("user", user.uid.toString())
                imgInfo.put("date", Timestamp(System.currentTimeMillis()))
                dbRef.set(imgInfo)
                Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AppActivity::class.java))
            }.addOnFailureListener {
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AppActivity::class.java))
            }
        }

        undoButton = findViewById<Button>(R.id.undo_upload_button)
        undoButton.setOnClickListener{
            startActivity(Intent(it.context, AppActivity::class.java))
        }

    }
}