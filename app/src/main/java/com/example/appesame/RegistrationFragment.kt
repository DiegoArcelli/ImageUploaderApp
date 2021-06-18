package com.example.appesame

import android.nfc.Tag
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var username : EditText
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var fAuth: FirebaseAuth
    private lateinit var registerButton : Button
    private lateinit var toLogin : TextView
    private lateinit var db : FirebaseFirestore

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
        val layout = inflater.inflate(R.layout.fragment_registration, container, false)

        username = layout.findViewById(R.id.registration_username_box)
        email = layout.findViewById(R.id.registration_email_box)
        password = layout.findViewById(R.id.registration_password_box)
        registerButton = layout.findViewById(R.id.registration_button)
        toLogin = layout.findViewById<TextView>(R.id.go_to_login)

        fAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (fAuth.currentUser != null) {
            findNavController().navigate(R.id.action_registration_to_login)
        }

        toLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registration_to_login)
        }

        registerButton.setOnClickListener() {
            val email_text: String = email.text.toString()
            val username_text: String = username.text.toString()
            val password_text: String = password.text.toString()

            // checks if the value of the fields are empty or not
            if (TextUtils.isEmpty(email_text)) {
                email.setError("Email field is required")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(username_text)) {
                email.setError("Username field is required")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password_text)) {
                email.setError("Password field is required")
                return@setOnClickListener
            }

            if (password_text.length < 8) {
                password.setError("Passowrd requires at least 8 characters")
                return@setOnClickListener
            }

            fAuth.createUserWithEmailAndPassword(email_text, password_text).addOnCompleteListener { task: Task<AuthResult> ->

                if (task.isSuccessful) {

                    val user : FirebaseUser = fAuth.currentUser!!

                    /*val request = UserProfileChangeRequest.Builder()
                        .setDisplayName(username_text)
                        .build()

                    user.updateProfile(request)*/

                    user!!.sendEmailVerification().addOnSuccessListener {
                        Toast.makeText(
                            activity,
                            "Verification email has been sent",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        Log.d("ERROR", "Errore")
                    }

                    val docRef : DocumentReference = db.collection("users").document(user.uid)
                    val userMap : HashMap<String, Any?> = HashMap<String, Any?>()
                    userMap.put("user_name", username_text)
                    userMap.put("profile_picture", null)
                    docRef.set(userMap).addOnSuccessListener {
                        Toast.makeText(activity, "User created", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registration_to_login)
                    }.addOnFailureListener {
                        Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(activity, "Error!" + task.exception, Toast.LENGTH_SHORT).show()
                }

            }
        }

        return  layout
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegistrationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}