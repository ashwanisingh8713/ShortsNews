package com.ns.shortsnews.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast

object ShowToast {

    fun showGravityToast(context: Context, msg:String){
        val toast = Toast.makeText(context,msg, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM,0,0)
        toast.show()
    }
}