package com.example.appesame

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var db : FirebaseFirestore
    private lateinit var store : FirebaseStorage
    private lateinit var images : HashMap<String?, Uri?>

    fun setImagesUri() {
        db = FirebaseFirestore.getInstance()
        db.collection("images")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    FirebaseStorage.getInstance().reference.child("/images/${document["file_name"] as String}").downloadUrl.addOnCompleteListener { task->
                        if (task.isSuccessful) {
                            val fileName = document["file_name"] as String
                            images.put(fileName.split(".")[0], task.result)
                        } else {
                            Log.d("ERROR", "Error")
                        }
                    }
                }
            }
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
        val layout = inflater.inflate(R.layout.fragment_map, container, false)
        images = HashMap<String?, Uri?>()
        setImagesUri()

        val supportMapFragment : SupportMapFragment = childFragmentManager!!.findFragmentById(R.id.google_map) as SupportMapFragment

        supportMapFragment.getMapAsync(OnMapReadyCallback { map ->

            map.setInfoWindowAdapter(ImageInfoWindowAdapter(this.requireContext(), images))

            map.setOnMarkerClickListener { marker ->
                if (marker.isInfoWindowShown) {
                    marker.hideInfoWindow()
                } else {
                    marker.showInfoWindow()
                }
                true
            }



            db.collection("images")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        val loc = document["location"] as GeoPoint
                        val descr = document["description"] as String
                        val id = document.id

                        val option = MarkerOptions()
                        option.position(com.google.android.gms.maps.model.LatLng(loc.latitude, loc.longitude))
                        option.title(id)
                        option.snippet(descr)
                        map.addMarker(option)


                        /*val loc = document["location"] as GeoPoint
                        val userId = document["user"] as String
                        //val userName = db.collection("users").document(userId).get()
                        val imgName = document["file_name"] as String
                        store = FirebaseStorage.getInstance()
                        Log.d("IMAGE", "images/${imgName}")
                        store.reference.child("/images/$imgName").downloadUrl.addOnSuccessListener { uri ->

                            val descr = document["description"] as String
                            val option = MarkerOptions()

                            option.title("Diego Arcelli")
                            option.position(com.google.android.gms.maps.model.LatLng(loc.latitude, loc.longitude))
                            option.snippet(descr)
                            map.addMarker(option)
                        }*/


                    }
                }


        })

        /*supportMapFragment.getMapAsync(OnMapReadyCallback { map ->
            map.setOnMapClickListener { loc ->
                val markerOption : MarkerOptions = MarkerOptions()
                markerOption.position(loc)
                markerOption.title("UEIIIEIEIEIEI")
                map.clear()
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    loc, 10F
                ))
                map.addMarker(markerOption)
            }
        })*/

        return layout
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}