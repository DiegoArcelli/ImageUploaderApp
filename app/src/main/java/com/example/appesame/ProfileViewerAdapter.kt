package com.example.appesame

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.findNavController

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileViewerAdapter(
    private var usersNames : ArrayList<String>,
    private var imagesSet : ArrayList<Boolean>,
    private var userIDs : ArrayList<String>,
    private var context : Context?,
    private var navController: NavController
) : RecyclerView.Adapter<ProfileViewerAdapter.ProfileViewHolder>(){



    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var layout : ConstraintLayout
        var userNameText : TextView
        var userProfilePic : ImageView

        init {
            this.layout = itemView.findViewById(R.id.profiles_viewer_layout)
            this.userNameText = itemView.findViewById(R.id.user_name_viewer)
            this.userProfilePic = itemView.findViewById(R.id.user_profile_picture)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ProfileViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.users_viewer, parent, false)
        val holder : ProfileViewHolder = ProfileViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.userNameText.text = usersNames[position]
        var id : String = userIDs[position]

        holder.userNameText.setOnClickListener {
            val action = SearchBarFragmentDirections.actionSearchBarFragmentToOtherProfileFragment(id)
            navController.navigate(action)
        }

        val picSet = imagesSet[position]
        if (picSet) {
            val storageRef : StorageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("profile_pics/${id}.jpg")
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(context!!)
                    .asBitmap()
                    .load(uri)
                    .into(holder.userProfilePic)
            }
        } else {
            holder.userProfilePic.setImageResource(R.drawable.generic_pro_pic)
        }
    }

    override fun getItemCount(): Int {
        return userIDs.size
    }

}