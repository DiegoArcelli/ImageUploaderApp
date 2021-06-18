package com.example.appesame

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text
import java.net.URI

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

    private lateinit var user_title : TextView
    private lateinit var db : FirebaseFirestore
    private lateinit var user : FirebaseUser


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


        // getting user information
        db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
        Log.d("USERNAME", user!!.uid)
        if (user != null) {
            user.let {
                val docRef : DocumentReference = db.collection("users").document(user.uid)
                docRef.addSnapshotListener {snapshot, e ->
                    val username = snapshot!!.get("user_name").toString()
                    user_title.text = username
                }

            }
        }

        // populating and displaying the recycle view
        val imagesNames : ArrayList<String> = ArrayList()
        val imagesDescr : ArrayList<String> = ArrayList()
        val imagesCol = db.collection("images")
        val query = imagesCol
            .whereEqualTo("user", user!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    imagesNames.add(document["file_name"] as String)
                    imagesDescr.add(document["description"] as String)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("QUERY ERROR", "Error getting documents: ", exception)
            }

        val recView : RecyclerView = layout.findViewById(R.id.recycle_images_viewer)
        val imgViewAdapter : ImageViewerAdpater = ImageViewerAdpater(imagesNames, imagesDescr, this.context)
        recView.adapter = imgViewAdapter
        //recView.layoutManager = LinearLayoutManager(this.context)
        return layout
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