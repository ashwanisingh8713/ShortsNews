package com.ns.news.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.videopager.ui.MainActivity
import com.google.android.material.snackbar.Snackbar

class CustomArticleOptionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle){

    var viewGroupArticle: ViewGroup? = null
    var viewGroupVideo: ViewGroup? = null
    var viewGroupPodcast:ViewGroup? = null
    var activity: Activity? = null
    var viewContext: Context? = null

    init {
        setOnClickListener {
            if (it.equals(viewGroupArticle)) {

                if (CommonFunctions.checkForInternet(context)) {
                    IntentUtils.openArticleShorts(context, Constants.shortsArticleIntentTag)
                } else {
                    CommonFunctions.showSnackBar(
                        this.activity!!,
                        "No Internet connection",
                        duration = Snackbar.LENGTH_LONG
                    )
                }
            } else if (it.equals(viewGroupVideo)){
                if (CommonFunctions.checkForInternet(context)) {
                    val intent = Intent(this.activity, MainActivity::class.java)
                    this.activity?.startActivity(intent)
                } else {
                    this.activity?.let { it1 ->
                        CommonFunctions.showSnackBar(
                            it1,
                            "No Internet connection",
                            duration = Snackbar.LENGTH_LONG
                        )
                    }
                }
            } else if (it.equals(viewGroupPodcast)){
                if (CommonFunctions.checkForInternet(context)) {
                    IntentUtils.openArticleShorts(context, Constants.podcastIntentTag)
                } else {
                    this.activity?.let { it1 ->
                        CommonFunctions.showSnackBar(
                            it1,
                            "No Internet connection",
                            duration = Snackbar.LENGTH_LONG
                        )
                    }
                }
            }
        }
    }

    fun setupArticleOptionView(activity: Activity, viewGroup: ViewGroup, context: Context) {
        this.viewGroupArticle = viewGroup
        this.activity = activity
        viewContext = context
    }

    fun setupVideoOptionView(activity: Activity, viewGroup: ViewGroup, context: Context) {
        this.viewGroupVideo = viewGroup
        this.activity = activity
        viewContext = context
    }

    fun setupPodcastOptionView(activity: Activity,viewGroup: ViewGroup, context: Context){
        this.viewGroupPodcast = viewGroup
        this.activity = activity
        viewContext = context
    }
}