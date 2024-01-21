package com.example.firebase_demo.utils

import android.app.Activity
import android.app.Dialog
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_demo.R

open class BaseActivity : AppCompatActivity() {

    lateinit var dialog: Dialog

    fun showProgressBar() {
        dialog = Dialog(this@BaseActivity)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.apply {
            setContentView(R.layout.progress_view)
            setCancelable(false)
            show()
        }
    }

    fun hideProgressBar() {
        dialog.dismiss()
    }

    fun showToast(activity: Activity, msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }


}