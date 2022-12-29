package com.ns.news.utils

import android.content.Context
import android.content.Intent
import com.ns.news.presentation.activity.ui.shorts.ShortsActivity

object IntentUtils {
     fun openArticleShorts(context: Context, flag:String){
        val intent = Intent(context, ShortsActivity::class.java)
        intent.putExtra(Constants.shortsOptionIntentTag,flag)
        context.startActivity(intent)
    }
}