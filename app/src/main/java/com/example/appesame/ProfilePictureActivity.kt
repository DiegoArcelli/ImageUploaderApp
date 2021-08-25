package com.example.appesame

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ProfilePictureActivity : AppCompatActivity() {

    private lateinit var cameraButton : ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var launcher : ActivityResultLauncher<Intent>
    private lateinit var uri : Uri
    private lateinit var uploadBundle : Bundle


    private fun openActivity() {
        this.uploadBundle = Bundle()
        this.uploadBundle.putString("uri", this.uri.toString())

        val intent : Intent = Intent(this, UploadProfilePictureActivity::class.java)
        intent.putExtras(this.uploadBundle)
        startActivity(intent)
    }

    val pickerContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        this.uri = uri
        openActivity()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_picture)

        cameraButton = findViewById(R.id.pro_pic_camera_button)
        galleryButton = findViewById(R.id.pro_pic_gallery_button)


        galleryButton.setOnClickListener{
            pickerContent.launch("image/*")
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                Log.d("QUA", "QUA CI ARRIVO")
                Log.d("MHANZ", result.toString())
                val bundle : Bundle = result!!.data!!.extras!!
                val bitmap : Bitmap = bundle.get("data") as Bitmap

                val file : File = File(this.cacheDir,"temp.jpg")
                file.delete()
                file.createNewFile()
                val fileOutStream : FileOutputStream = file.outputStream()
                val byteArrayOutStream : ByteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutStream)
                val byteArray : ByteArray = byteArrayOutStream.toByteArray()
                fileOutStream.write(byteArray)
                fileOutStream.flush()
                fileOutStream.close()
                byteArrayOutStream.close()

                this.uri = file.toUri()
                openActivity()
                /* Toast.makeText(activity, uri.toString(), Toast.LENGTH_SHORT).show()

                val uploadBundle : Bundle = Bundle()
                uploadBundle.putString("uri", uri.toString())
                val intent : Intent = Intent(this.context, ImageUploaderActivity::class.java)
                fetchLocation()
                uploadBundle.putDouble("latitude", this.latitude!!)
                uploadBundle.putDouble("longitude", this.longitude!!)
                intent.putExtras(uploadBundle)
                startActivity(intent) */

            }
        })

        cameraButton.setOnClickListener {
            Log.d("PREMUTO", "Pigiato")
            val intent : Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(this.packageManager) != null) {
                launcher.launch(intent)
            }
        }




    }
}