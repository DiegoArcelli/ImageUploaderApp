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
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.view.VerifiedInputEvent
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.collection.LLRBNode
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
        val storageRef = FirebaseStorage.getInstance().reference
        val imgName = imagesNames[position]
        Log.d(TAG, "images/${imgName}")
        val imageRef = storageRef.child("images/${imgName}")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Log.d("URI", uri.toString())
            Glide.with(context!!)
                .asBitmap()
                .load(uri)
                .into(holder.image)
        }

        holder.description.text = imagesDescr[position]

        updateProfile(holder, position)

        holder.likeButton.setOnClickListener {
            val db : FirebaseFirestore = FirebaseFirestore.getInstance()
            val user_id = FirebaseAuth.getInstance().currentUser!!.uid
            val imgName = imagesNames[position]
            val imgId : String = imgName.split(".")[0]
            val ref = db.collection("images").document(imgId)
            db.collection("likes")
                .whereEqualTo("user", user_id)
                .whereEqualTo("image", ref)
                .get()
                .addOnSuccessListener { documents ->
                    if ( documents.size() == 0 ) {
                        val docRef = db.collection("likes").document()
                        val likeMap : HashMap<String, Any?> = HashMap<String, Any?>()
                        likeMap.put("user", user_id)
                        likeMap.put("image", ref)
                        docRef.set(likeMap).addOnSuccessListener {
                            updateProfile(holder, position)
                        }.addOnFailureListener {

                        }
                    } else {
                        val id = documents.first().id
                        val docRef = db.collection("likes").document(id)
                        docRef.delete()
                        updateProfile(holder, position)
                    }
                }

        }

    }

    fun updateProfile(holder: ImageViewHolder, position: Int) {
        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
        val user_id = FirebaseAuth.getInstance().currentUser!!.uid
        val imgName = imagesNames[position]
        val imgId : String = imgName.split(".")[0]
        val ref = db.collection("images").document(imgId)

        // setting the  like or the unlike button
        db.collection("likes")
            .whereEqualTo("user", user_id)
            .whereEqualTo("image", ref)
            .get()
            .addOnSuccessListener { documents ->
                if ( documents.size() == 0 ) {
                    holder.likeButton.setColorFilter(Color.argb(255, 98, 99, 99))
                    holder.likeButton.setBackgroundColor(Color.argb(255, 214, 215, 215))
                } else {
                    holder.likeButton.setColorFilter(Color.argb(255, 160, 110, 190))
                    holder.likeButton.setBackgroundColor(Color.argb(255, 105, 0, 190))
                }
                holder.likeButton.setPadding(20,20,20,20)
            }

        // getting the number of likes
        var likes_count : Int = 0
        db.collection("likes")
            .whereEqualTo("image", ref)
            .get()
            .addOnSuccessListener { documents ->
                likes_count = documents.size()
                holder.likesCount.text = "Likes: " + likes_count.toString()
            }
    }

    override fun getItemCount(): Int {
        return 5
    }

}