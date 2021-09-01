package com.example.appesame

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.bumptech.glide.module.AppGlideModule
import com.squareup.picasso.Picasso


class ImageInfoWindowAdapter(
    var context : Context,
    var images : HashMap<String?, Uri?>
) : GoogleMap.InfoWindowAdapter {


    init {
        this.context = context
        this.images = images
    }

    override fun getInfoWindow(marker: Marker): View? {
        val infoView = LayoutInflater.from(this.context).inflate(R.layout.image_info_view, null)
        val userText : TextView = infoView.findViewById(R.id.info_image_user) as TextView
        val imageView : ImageView = infoView.findViewById(R.id.info_image_picture) as ImageView
        val descrText : TextView = infoView.findViewById(R.id.info_image_description) as TextView

        val imageId = marker.title
        userText.text = "Diego Arcelli"
        descrText.text = marker.snippet
        Log.d("IMAGINE", images[imageId].toString())
        Picasso.with(context)
            .load(images[imageId])
            .into(imageView)
        /*Glide.with(context)
            .asBitmap()
            .load(images[imageId])
            .into(imageView)*/


        /*descr.text = marker.snippet
        user.text = marker.title
        val uri = marker.id
        Glide.with(context)
            .asBitmap()
            .load(uri)
            .into(image)
        return infoView*/

        return  infoView
    }

    override fun getInfoContents(marker: Marker): View? {
        TODO("Not yet implemented")
    }


}