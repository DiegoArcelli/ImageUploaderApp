package com.example.appesame

import android.content.Intent
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var fAuth: FirebaseAuth
    private lateinit var loginButton : Button
    private lateinit var toRegistration : TextView


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
        val layout = inflater.inflate(R.layout.fragment_login, container, false)

        email = layout.findViewById(R.id.login_email_box)
        password = layout.findViewById(R.id.login_password_box)
        loginButton = layout.findViewById(R.id.login_button)
        toRegistration = layout.findViewById<TextView>(R.id.go_to_registration)

        fAuth = FirebaseAuth.getInstance()

        toRegistration.setOnClickListener {
            Log.d("GO TO REG", "Go to registration page")
            findNavController().navigate(R.id.action_login_to_registration)
        }

        loginButton.setOnClickListener {
            val email_text: String = email.text.toString()
            val password_text: String = password.text.toString()


            // checks if the value of the fields are empty or not
            if (TextUtils.isEmpty(email_text)) {
                email.setError("Email field is required")
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

            // authentication

            fAuth.signInWithEmailAndPassword(email_text, password_text).addOnCompleteListener { task: Task<AuthResult> ->

                if (task.isSuccessful) {

                    if (fAuth.currentUser!!.isEmailVerified) {
                        Toast.makeText(activity, "Log in successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this.context, AppActivity::class.java))
                    } else {
                        fAuth.signOut()
                        Toast.makeText(activity, "Email hasn't been verified", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }

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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}