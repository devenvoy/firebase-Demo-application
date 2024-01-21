package com.example.firebase_demo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.firebase_demo.databinding.ActivitySignInBinding
import com.example.firebase_demo.utils.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }


        binding.btnSignIn.setOnClickListener {
            loginUser()
        }


    }

    private fun loginUser() {

        val email = binding.etSinInEmail.text.toString()
        val password = binding.etSinInPassword.text.toString()

        if (validateForm(email, password)) {
            showProgressBar()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        hideProgressBar()
                        // Sign in success, update UI with the signed-in user's information
                        showToast(this@SignInActivity, "Logged In\nSuccessfully")
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        finish()
                        val user = auth.currentUser
                    } else {
                        // If sign in fails, display a message to the user.
                        hideProgressBar()
                        showToast(
                            this@SignInActivity, "Authentication Failed\n" +
                                    "Try again later"
                        )
                    }
                }
        } else {
            showToast(this@SignInActivity, "Enter correct email and password")
        }

    }


    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = "Enter valid email address"
                false
            }

            TextUtils.isEmpty(password) -> {
                binding.tilPassword.error = "Enter Password"
                false
            }

            else -> {
                true
            }
        }
    }
}