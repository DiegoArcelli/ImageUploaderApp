package com.example.appesame

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

public class ImageViewerAdpater(
    private var imagesNames : ArrayList<String>,
    private var imagesDescr : ArrayList<String>,
    private var context : Context?
) : RecyclerView.Adapter<ImageViewerAdpater.ImageViewHolder>() {

    private val TAG = "ImageViewerAdpater"

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var layout : ConstraintLayout
        var image : ImageView
        var description : TextView
        var likesCount : TextView
        var likeButton : ImageButton

        init {
            this.image = itemView.findViewById(R.id.viewer_image)
            this.description = itemView.findViewById(R.id.viewer_description)
            this.likesCount = itemView.findViewById(R.id.likes_counter)
            this.likeButton = itemView.findViewById(R.id.like_button)
            this.layout = itemView.findViewById(R.id.viewer_layout)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.images_viewer, parent, false)
        val holder : ImageViewHolder = ImageViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: called.")
        val storageRef = FirebaseStorage.getInstance().reference
        val imgName = imagesNames[position]
        val imageRef = storageRef.child("images/{$imgName}")

        holder.description.text = imagesDescr[position]
        // holder.image.setImageURI()

    }

    override fun getItemCount(): Int {
        return 5
    }

}