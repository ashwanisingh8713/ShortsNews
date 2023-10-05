package com.videopager.utils

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.ns.shortsnews.R

/**
 * Created by Ashwani Kumar Singh on 13,June,2023.
 */
class NoConnection {
    companion object {
        fun noConnectionSnackBarInfinite(view: View?, context: AppCompatActivity) {
            if (view == null) {
                return
            }
            val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
            val snackbarView = snackbar.view as SnackbarLayout
            snackbar.setBackgroundTint(Color.BLACK)
            val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            val textView1 = snackbarView.findViewById(com.google.android.material.R.id.snackbar_action) as TextView

            textView.visibility = View.INVISIBLE
            textView1.visibility = View.INVISIBLE
            val snackView: View =
                context.layoutInflater.inflate(R.layout.thp_noconnection_snackbar, null)
            snackView.findViewById<View>(R.id.action_button).backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)

            snackView.findViewById<View>(R.id.action_button).setOnClickListener {
                val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)
            }
            snackbarView.addView(snackView)
            snackbar.show()
        }
    }
}