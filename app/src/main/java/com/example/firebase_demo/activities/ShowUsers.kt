package com.example.firebase_demo.activities

import User
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase_demo.R
import com.example.firebase_demo.databinding.ActivityShowUsersBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.ktx.Firebase


class ShowUsers : AppCompatActivity() {

    private lateinit var binding: ActivityShowUsersBinding
    private lateinit var auth: FirebaseAuth

    lateinit var adapter: FirebaseRecyclerAdapter<*, *>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val query: Query = FirebaseDatabase.getInstance().reference.child("MyData/Users")

        val options: FirebaseRecyclerOptions<User?> =
            FirebaseRecyclerOptions.Builder<User>().setQuery(query, User::class.java).build()

        binding.imgLogout.setOnClickListener {
            Log.e("====", "clicked ")
            if (auth.currentUser != null) {
                auth.signOut()
                startActivity(Intent(this@ShowUsers, GetStartedActivity::class.java))
                finish()
            }
        }

        binding.fabAddUser.setOnClickListener {
            startActivity(Intent(this@ShowUsers, AddUserDetail::class.java))
        }

        adapter = object : FirebaseRecyclerAdapter<User?, MyViewHolder?>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val itemView = LayoutInflater.from(this@ShowUsers)
                    .inflate(R.layout.user_item_view, parent, false)
                return MyViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: User) {
                val userdata = "Name : " + model.username + "\nNumber : " + model.number
                holder.UserDetails.text = userdata
                Glide.with(this@ShowUsers).load(model.imgUrl).into(holder.UserImage)
            }

        }

        binding.recyclerView.adapter = adapter

    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var UserImage: ImageView
    var UserDetails: TextView

    init {
        UserImage = itemView.findViewById(R.id.UserImage)
        UserDetails = itemView.findViewById(R.id.UserDetails)
    }
}
