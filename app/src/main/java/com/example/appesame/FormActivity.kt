package com.example.appesame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    // handles the pression of the back button
    override fun onSupportNavigateUp(): Boolean {
        finish()
        System.exit(0)
        return true
    }
}