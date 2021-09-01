package com.example.appesame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var prevButton : Button
    private lateinit var nextButton : Button
    private lateinit var user_title : TextView
    private lateinit var db : FirebaseFirestore
    private lateinit var user : FirebaseUser
    private lateinit var proPic : ImageView
    private lateinit var imgViewAdapter : ImageViewerAdpater
    private lateinit var recView : RecyclerView
    private var page : Int = 0
    private val N : Int = 5


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
        val layout =  inflater.inflate(R.layout.fragment_profile, container, false)
        user_title = layout.findViewById(R.id.profile_name)

        proPic = layout.findViewById(R.id.profile_picture)

        proPic.setOnClickListener {
            val intent = Intent(this.context, ProfilePictureActivity::class.java)
            startActivity(intent)
        }


        // getting user information
        db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
        Log.d("USERNAME", user!!.uid)
        user.let {
            val docRef : DocumentReference = db.collection("users").document(user.uid)
            docRef.addSnapshotListener {snapshot, e ->
                val username = snapshot!!.get("user_name").toString()
                val proPicSet : Boolean = snapshot!!.get("profile_picture") as Boolean
                user_title.text = username
                if (proPicSet) {
                    val storageRef : StorageReference = FirebaseStorage.getInstance().reference
                    val imageRef = storageRef.child("profile_pics/${user.uid}.jpg")
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        Log.d("URI", uri.toString())
                        Glide.with(this.requireContext())
                            .asBitmap()
                            .load(uri)
                            .into(proPic)
                    }
                } else {
                    proPic.setImageResource(R.drawable.generic_pro_pic)
                }
            }

        }

        // populating and displaying the recycle view
        recView = layout.findViewById(R.id.recycle_images_viewer)
        val imagesNames : ArrayList<String> = ArrayList()
        val imagesDescr : ArrayList<String> = ArrayList()



        db.collection("images")
            .whereEqualTo("user", user!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    imagesNames.add(document["file_name"] as String)
                    imagesDescr.add(document["description"] as String)
                }
                initializeImageViewer(imagesNames, imagesDescr)
            }
            .addOnFailureListener { exception ->
                Log.w("QUERY ERROR", "Error getting documents: ", exception)
            }

        prevButton = layout.findViewById(R.id.profile_prev_button)
        nextButton = layout.findViewById(R.id.profile_next_button)

        prevButton.setOnClickListener {
            if (page > 0) {
                page--
            }
        }

        nextButton.setOnClickListener {
            if (true) {
                page++
            }
        }


        return layout
    }

    fun initializeImageViewer(imagesNames : ArrayList<String>, imagesDescr : ArrayList<String>) {

        val selectedImages = ArrayList<String>()
        val selectedDescr = ArrayList<String>()
        Log.d("START", (page*N).toString())
        Log.d("END", (page*N+(N-1)).toString())

        if (page*N >= imagesDescr.size) {
            page--
        }

        for (i in page*N .. page*N+(N-1)) {
            if (i < imagesNames.size) {
                selectedImages.add(imagesNames[i])
                selectedDescr.add(imagesDescr[i])
            }
        }

        imgViewAdapter = ImageViewerAdpater(imagesNames, imagesDescr, this.context)
        recView.adapter = imgViewAdapter
        recView.layoutManager = LinearLayoutManager(this.context)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}