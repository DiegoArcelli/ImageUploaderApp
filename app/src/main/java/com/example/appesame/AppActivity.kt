package com.example.appesame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_menu_bar)
        //val navigationController = findNavController(R.id.app_navigation_fragment)
        //bottomNavigationView.setupWithNavController(navigationController)

    }
}