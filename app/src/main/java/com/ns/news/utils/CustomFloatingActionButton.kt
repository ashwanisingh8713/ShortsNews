package com.ns.news.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.TranslateAnimation
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ns.news.R

class CustomFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FloatingActionButton(context, attrs, defStyle){
    var flag:Boolean = false
     var viewGroup :ViewGroup? = null

    init {
        setImageDrawable(ContextCompat.getDrawable(context,R.drawable.fab_image))
        setOnClickListener {
          setUpUI(true)
        }
    }
    fun setupParentLayout(viewGroup: ViewGroup){
        this.viewGroup = viewGroup
    }
    fun closeBottomSheet(){
//        viewGroup?.let { it1 -> slideDown(it1) }
        setUpUI(false)

    }

    fun setUpUI(from:Boolean){
        if (from) {
            val interpolator = OvershootInterpolator()
            ViewCompat.animate(this).setInterpolator(interpolator).setListener(null)
                .rotationBy(360f).withLayer().setDuration(900).start()
        }
        flag = !flag
        if (flag) {
            this.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fab_cross))
            viewGroup?.let { it1 -> slideUp(it1) }

        } else if (!flag) {
            this.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fab_image))
            viewGroup?.let { it1 -> slideDown(it1) }
        }
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
