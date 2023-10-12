package com.ns.shortsnews.callbacks

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View


/**
 * Created by Ashwani Kumar Singh on 11,October,2023.
 */
class OnSwipeTouchListener(context: Context, private val singleTap:()->Unit, private val doubleTap:()->Unit): View.OnTouchListener {

    private var gestureDetector: GestureDetector? = null
   init {
       gestureDetector = GestureDetector(context, GestureListener())
   }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        Log.d("AOnSwipeTouchListener", "onTouch ${gestureDetector!!}")
        return gestureDetector!!.onTouchEvent(event!!)
    }

    inner class GestureListener: GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            singleTap()
            return super.onSingleTapConfirmed(e)
        }

        override
        fun onDoubleTap(e: MotionEvent): Boolean {
            doubleTap()
            return super.onDoubleTap(e!!)
        }

    }

}