package com.example.firebase_demo.activities

import User
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.firebase_demo.databinding.ActivityUpdateUserBinding
import com.example.firebase_demo.utils.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import kotlin.random.Random


class UpdateUser : BaseActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var binding: ActivityUpdateUserBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    lateinit var imgUrl: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userData = intent.getSerializableExtra("user") as User

        auth = Firebase.auth
        storage = Firebase.storage
        database = Firebase.database
        dbRef = database.reference

        val storageRef = storage.reference

        // get path of user path
        val userDbRef =
            database.getReference("MyData/Users/${auth.currentUser!!.uid}")
                .child(userData.userKey.toString())

        // set old data
        binding.edtNameUpd.setText(userData.username)
        binding.edtNumberUpd.setText(userData.number)
        Glide.with(this@UpdateUser).load(userData.imgUrl).into(binding.imgUserUpd)

        // image to showm
        binding.imgUserUpd.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            imgLauncher.launch(intent)
        }

        // delete button
        binding.btnDelete.setOnClickListener {
            userDbRef.removeValue()
            finish()
        }

        binding.btnUpdate.setOnClickListener {

            showProgressBar()

            val name = binding.edtNameUpd.text.toString()
            val number = binding.edtNumberUpd.text.toString()
            val imgname = "$name${Random.nextInt(1000, 9999)}.jpg"

            val imageRef = storageRef.child("Images/$imgname")
            binding.imgUserUpd.isDrawingCacheEnabled = true
            binding.imgUserUpd.buildDrawingCache()
            val bitmap = (binding.imgUserUpd.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // this method store image in storage
            val uploadTask = imageRef.putBytes(data)

            uploadTask.addOnFailureListener {}
                .addOnSuccessListener { taskSnapshot ->

                    val urlTask = uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        imageRef.downloadUrl
                    }
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result
                                val imageurl: String = downloadUri.toString()
                                imgUrl = imageurl

                                val newUser = User(userData.userKey, name, number, imgUrl)

                                userDbRef.setValue(newUser).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@UpdateUser,
                                            "User Updated Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        hideProgressBar()
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@UpdateUser,
                                            "Error Occured",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        hideProgressBar()
                                    }
                                }
                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                }
        }
    }

    private val imgLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                if (it.data != null) {
                    val selectedimgUri = it.data!!.data
                    binding.imgUserUpd.setImageURI(selectedimgUri)
                }
            }
        }
}