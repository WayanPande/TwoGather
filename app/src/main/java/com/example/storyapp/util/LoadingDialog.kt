package com.example.storyapp.util

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.storyapp.R

class LoadingDialog(myActivity: Activity) {

    private val activity: Activity = myActivity
    private lateinit var dialog: AlertDialog


    fun startLoadingDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.WrapContentDialog)
        val inflater: LayoutInflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.loading_dialog, null))

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun dismissDialog() {
        if (this::dialog.isInitialized) {
            dialog.dismiss()
        }
    }

}