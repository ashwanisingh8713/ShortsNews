package com.ns.shortsnews.utils

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ns.shortsnews.domain.models.ProfileData

class Alert {

    fun showErrorDialog(title:String, msg:String, context: Context){
        val alertDialog = MaterialAlertDialogBuilder(context)
        alertDialog.apply {
            this.setTitle(title)
            this.setMessage(msg)
            this.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            this.show()
        }

    }

companion object {
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}

    fun showGravityToast(context: Context, msg:String){
        val toast = Toast.makeText(context,msg, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM,0,0)
        toast.show()
    }
}