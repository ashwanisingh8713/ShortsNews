package com.ns.news.utils

import android.app.Dialog
import android.content.Context
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
import com.ns.news.R

class CommonFunctions {
    companion object{
        fun animateFab(flags: Boolean, fab: FloatingActionButton, parentView: View, context: Context) {
            val interpolator = OvershootInterpolator()
            ViewCompat.animate(fab).setInterpolator(interpolator).setListener(null)
                .rotationBy(360f).withLayer().setDuration(900).start()
            if (flags) {
                fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fab_cross))
                slideUp(parentView)

            } else if (!flags) {
                fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fab_image))
               slideDown(parentView)

            }
        }


        fun slideUp(view: View) {
            val animate = TranslateAnimation(
                0F,  // fromXDelta
                0F,  // toXDelta
                view.height.toFloat(),  // fromYDelta
                0F
            ) // toYDelta
            animate.duration = 500
            animate.fillBefore = false
            animate.fillBefore = true
            view.startAnimation(animate)
            animate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    view.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
            })
        }

        fun slideDown(view: View) {
            var animate = TranslateAnimation(
                0F,  // fromXDelta
                0F,  // toXDelta
                0F,  // fromYDelta
                view.height.toFloat()
            ) // toYDelta
            animate.duration = 500
            animate.isFillEnabled = false
            animate.repeatCount = 0
            view.startAnimation(animate)
            animate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    view.clearAnimation()
                    view.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {
                    Log.i("","")
                }
            })
        }
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
    }
}