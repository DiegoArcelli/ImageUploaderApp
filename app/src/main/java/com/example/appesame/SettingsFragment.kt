package com.example.appesame

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var changeNameButton : Button
    private lateinit var changeNameBox : EditText
    private lateinit var db : FirebaseFirestore
    private lateinit var pwdRecoverButton : Button
    private lateinit var changePwdButton : Button
    private lateinit var newPwdBox : EditText
    private lateinit var confirmPwdBox : EditText
    private lateinit var deleteButton : Button

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
        val layout = inflater.inflate(R.layout.fragment_settings, container, false)

        val user = FirebaseAuth.getInstance().currentUser!!

        changeNameButton = layout.findViewById(R.id.change_user_name_button)
        changeNameButton.setOnClickListener {
            changeNameBox = layout.findViewById(R.id.change_user_name_box)
            val text : String = changeNameBox.text.toString()
            if (text != "") {
                db = FirebaseFirestore.getInstance()
                db.collection("users").document(user.uid).update("user_name", text).addOnSuccessListener {
                    Toast.makeText(this.requireContext(), "Username changed!", Toast.LENGTH_LONG).show()
                }
            }
        }

        pwdRecoverButton = layout.findViewById(R.id.pwd_forgot_button)
        pwdRecoverButton.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(user.email.toString()).addOnSuccessListener {
                Toast.makeText(this.requireContext(), "Recovery email sent at ${user.email.toString()}", Toast.LENGTH_LONG ).show()
            }
        }

        changePwdButton = layout.findViewById(R.id.change_pwd_button)
        newPwdBox = layout.findViewById(R.id.change_pwd_box)
        confirmPwdBox = layout.findViewById(R.id.confirm_new_pwd_box)
        changePwdButton.setOnClickListener {
            val newPwd = newPwdBox.text.toString()
            val confPwd = confirmPwdBox.text.toString()
            if (newPwd == confPwd) {
                user.updatePassword(newPwd).addOnSuccessListener {
                    Toast.makeText(this.requireContext(), "Password updated", Toast.LENGTH_LONG ).show()
                }
            }
        }

        deleteButton = layout.findViewById(R.id.delete_account_button)
        deleteButton.setOnClickListener {
            startActivity(Intent(this.requireContext(), DeleteAccountActivity::class.java))
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
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}