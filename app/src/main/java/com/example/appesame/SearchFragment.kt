package com.example.appesame

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirestoreRegistrar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var searchBox : EditText
    private lateinit var searchButton : ImageButton
    private lateinit var db : FirebaseFirestore
    private lateinit var usersNames : ArrayList<String>
    private lateinit var profileViewerAdpater: ProfileViewerAdapter
    private lateinit var recView : RecyclerView

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
        val layout =  inflater.inflate(R.layout.fragment_search, container, false)
        searchBox = layout.findViewById(R.id.search_user_box)
        searchButton = layout.findViewById(R.id.search_user_button)
        recView = layout.findViewById(R.id.profile_list_viewer)

        searchButton.setOnClickListener {
            val keyword : String = searchBox.text.toString()
            if (keyword != "") {
                db = FirebaseFirestore.getInstance()
                usersNames = ArrayList()
                db.collection("users").get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        val user_name : String = document["user_name"] as String
                        val mod_name = user_name.replace("\\s".toRegex(), "").lowercase()
                        val mod_key = keyword.replace("\\s".toRegex(), "").lowercase()
                        if (mod_name.indexOf(mod_key) != -1) {
                            usersNames.add(user_name)
                        }
                    }
                    Log.d("USERS", usersNames.toString())
                }
            } else {
                Toast.makeText(this.context, "Insert something", Toast.LENGTH_SHORT).show()
            }
        }

        return layout
    }

    fun initializeImageViewer(imagesNames : ArrayList<String>, imagesDescr : ArrayList<String>) {
        profileViewerAdpater = ProfileViewerAdapter(imagesNames, imagesDescr, this.context)
        recView.adapter = profileViewerAdpater
        recView.layoutManager = LinearLayoutManager(this.context)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}