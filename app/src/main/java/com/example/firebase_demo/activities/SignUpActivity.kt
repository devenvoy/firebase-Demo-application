package com.example.firebase_demo.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import com.example.firebase_demo.databinding.ActivitySignUpBinding
import com.example.firebase_demo.utils.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.tvLoginPage.setOnClickListener {
            finish()
        }

        binding.btnSignUp.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser() {
        val name = binding.etSinUpName.text.toString()
        val email = binding.etSinUpEmail.text.toString()
        val password = binding.etSinUpPassword.text.toString()

        if (validateForm(name, email, password)) {
            showProgressBar()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@SignUpActivity)
                { task ->
                    if (task.isSuccessful) {
                        hideProgressBar()
                        showToast(this@SignUpActivity, "User Id Created Successfully")
//                            startActivity(Intent(this@SignUpActivity,MainActivity::class.java))
                            finish()
                    } else {
                        hideProgressBar()
                        showToast(this@SignUpActivity, "User Id not created try again later")
                    }
                }
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                binding.tilName.error = "Enter Name"
                false
            }

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