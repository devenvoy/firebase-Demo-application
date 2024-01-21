package com.example.firebase_demo.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.firebase_demo.R
import com.example.firebase_demo.databinding.ActivityMainBinding
import com.example.firebase_demo.utils.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.imgLogout.setOnClickListener {
            Log.e("====", "clicked ")
            if (auth.currentUser != null) {
                auth.signOut()
                startActivity(Intent(this@MainActivity, GetStartedActivity::class.java))
                finish()
            }
        }
    }
}