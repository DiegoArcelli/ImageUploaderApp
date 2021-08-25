package com.example.appesame

import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.reflect.typeOf

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [PhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var galleryButton : ImageButton
    private lateinit var photoButton : ImageButton
    private lateinit var launcher : ActivityResultLauncher<Intent>
    private lateinit var photoUri : Uri
    private lateinit var locationProvider : FusedLocationProviderClient
    private var latitude : Double? = null;
    private var longitude : Double? = null;
    private var uri : Uri? = null
    private lateinit var uploadBundle : Bundle


    private fun openActivity() {
        this.uploadBundle = Bundle()
        this.uploadBundle.putString("uri", this.uri.toString())
        this.uploadBundle.putDouble("latitude", this.latitude!!)
        this.uploadBundle.putDouble("longitude", this.longitude!!)
        val intent : Intent = Intent(this.context, ImageUploaderActivity::class.java)
        intent.putExtras(this.uploadBundle)
        startActivity(intent)
    }


    private fun fetchLocation() {

        val task = locationProvider.lastLocation

        if (ActivityCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }

        task.addOnSuccessListener { loc ->
            if(loc != null) {
                this.latitude = loc.latitude
                this.longitude = loc.longitude
                Log.d("GPS", "$latitude, $longitude")
                openActivity()
            }
        }

    }

    val pickerContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        this.uri = uri
        fetchLocation()
        /* val bundle : Bundle = Bundle()
        bundle.putString("uri", uri.toString())
        val intent : Intent = Intent(this.context, ImageUploaderActivity::class.java)
        Log.d("UNO", "UNO")
        fetchLocation()
        bundle.putDouble("latitude", this.latitude!!)
        bundle.putDouble("longitude", this.longitude!!)
        intent.putExtras(bundle)
        startActivity(intent) */
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout =  inflater.inflate(R.layout.fragment_photo, container, false)
        this.locationProvider = LocationServices.getFusedLocationProviderClient(this.activity)

        galleryButton = layout.findViewById(R.id.select_from_gallery_button)
        galleryButton.setOnClickListener{
            pickerContent.launch("image/*")
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                Log.d("QUA", "QUA CI ARRIVO")
                Log.d("MHANZ", result.toString())
                val bundle : Bundle = result!!.data!!.extras!!
                val bitmap : Bitmap = bundle.get("data") as Bitmap

                val file : File = File(requireContext().cacheDir,"temp.jpg")
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
                fetchLocation()
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

        photoButton = layout.findViewById(R.id.take_photo_button)
        photoButton.setOnClickListener {
            Log.d("PREMUTO", "Pigiato")
            val intent : Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                launcher.launch(intent)
            }
        }

        return layout
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PhotoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}


