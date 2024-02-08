package com.example.firebase_demo.activities

import User
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase_demo.R
import com.example.firebase_demo.databinding.ActivityShowUsersBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.math.log


class ShowUsers : AppCompatActivity() {

    private lateinit var binding: ActivityShowUsersBinding
    private lateinit var auth: FirebaseAuth

    lateinit var adapter: FirebaseRecyclerAdapter<*, *>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()
        getToken()

        auth = Firebase.auth

        val user = auth.currentUser!!

        Glide.with(this@ShowUsers).load(user.photoUrl).into(binding.CUimg)

        val userData = "${user.email}\n${user.uid}\n${user.metadata.toString()}\n${user.tenantId}"

        if (userData.isNotEmpty()) {
            binding.CVdata.text = userData
        }

        val query: Query =
            FirebaseDatabase.getInstance().reference.child("MyData/Users/${user.uid}")

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

                holder.itemView.setOnLongClickListener {
                    val intt = Intent(this@ShowUsers, UpdateUser::class.java)
                    intt.putExtra("user", model)
                    startActivity(intt)
                    true
                }
            }
        }

        binding.recyclerView.adapter = adapter

    }


    override fun onStart() {
        super.onStart()
        Log.e("======", "onStart: ")
        adapter.startListening()
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.e("======", "onDestroy: ")
        adapter.stopListening()

    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("_MS_", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("_MS_", token)
//            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
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
