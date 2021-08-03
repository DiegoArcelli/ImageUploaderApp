package com.example.appesame

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class ProfileViewerAdapter(
    private var imagesNames : ArrayList<String>,
    private var usersNames : ArrayList<String>,
    private var context : Context?
) : RecyclerView.Adapter<ProfileViewerAdapter.ProfileViewHolder>(){



    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var layout : ConstraintLayout
        var userNameText : TextView

        init {
            this.layout = itemView.findViewById(R.id.profiles_viewer_layout)
            this.userNameText = itemView.findViewById(R.id.user_name_viewer)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.images_viewer, parent, false)
        val holder : ProfileViewerAdapter.ProfileViewHolder = ProfileViewerAdapter.ProfileViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.userNameText.text = usersNames[position].toString()
        holder.layout.setOnClickListener {
            Log.d("MHANZ", "Culo cane cane")
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

}