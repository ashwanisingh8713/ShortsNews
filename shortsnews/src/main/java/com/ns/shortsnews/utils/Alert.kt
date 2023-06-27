package com.ns.shortsnews.utils

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Alert {

    fun showErrorDialog(title:String, msg:String, context: Context){
        val alertDialog = MaterialAlertDialogBuilder(context)
        alertDialog.apply {
            this.setTitle(title)
            this.setMessage(msg)
            this.setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            this.show()
        }

    }

    fun showGravityToast(context: Context, msg:String){
        val toast = Toast.makeText(context,msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM,0,0)
        toast.show()
    }
}