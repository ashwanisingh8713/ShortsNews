package com.ns.news.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.ns.news.R

class CommonFunctions {
    companion object{
        fun checkForInternet(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }

        fun showSnackBar(activity: Activity, message: String, action: String? = null,
                         actionListener: View.OnClickListener? = null, duration: Int = Snackbar.LENGTH_SHORT) {
            val snackBar = Snackbar.make(activity.findViewById(android.R.id.content), message, duration)
                .setBackgroundTint(Color.parseColor("#CC000000")) // todo update your color
                .setTextColor(Color.WHITE)
            if (action != null && actionListener!=null) {
                snackBar.setAction(action, actionListener)
            }
            snackBar.show()
        }
    }
}