package com.ns.shortsnews.utils

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object AppDialog {

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
}