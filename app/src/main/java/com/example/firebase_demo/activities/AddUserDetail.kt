package com.example.firebase_demo.activities

import User
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebase_demo.databinding.ActivityAddUserBinding
import com.example.firebase_demo.utils.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import kotlin.random.Random

class AddUserDetail : BaseActivity() {

    private lateinit var database: FirebaseDatabase

    //    private lateinit var database:
    private lateinit var storage: FirebaseStorage
    private lateinit var binding: ActivityAddUserBinding

    private lateinit var auth: FirebaseAuth

    lateinit var imgUrl: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        storage = Firebase.storage

        binding.imgUser.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            imgLauncher.launch(intent)
        }

        binding.submit.setOnClickListener {

            showProgressBar()
            val storageRef = storage.reference
            val name = binding.edtName.text.toString()
            val number = binding.edtNumber.text.toString()
            val imgname = "$name${Random.nextInt(1000, 9999)}.jpg"

            val imageRef = storageRef.child("Images/$imgname")

            binding.imgUser.isDrawingCacheEnabled = true
            binding.imgUser.buildDrawingCache()
            val bitmap = (binding.imgUser.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // this method store image in storage
            var uploadTask = imageRef.putBytes(data)

            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener { taskSnapshot ->

                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val imageurl: String = downloadUri.toString()

                        imgUrl = imageurl

                        Log.e("===", "submit: $imageurl")

// Write a message to the database
                        database = Firebase.database
                        val myRef =
                            database.getReference("MyData/Users/${auth.currentUser?.uid}").push()

                        val user = User(myRef.key, name, number, imgUrl)

                        myRef.setValue(user)
                        Toast.makeText(
                            this@AddUserDetail,
                            "User Added Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        hideProgressBar()
//                        startActivity(this)
                        finish()

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            }
        }
    }

    val imgLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            if (it.data != null) {
                val selectedimgUri = it.data!!.data
                binding.imgUser.setImageURI(selectedimgUri)
            }
        }
    }
}