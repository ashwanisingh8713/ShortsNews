package com.videopager.utils

import android.annotation.SuppressLint
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*


fun CommentPostTime (time:String):String{
    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
    val parsePosition = ParsePosition(0)
    val then:Long = simpleDateFormat.parse(time,parsePosition).time
    val now: Long = Date().getTime()
    val seconds: Long = (now - then) / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    var friendly: String? = null
    var num: Long = 0
    if (days > 0) {
        num = days;
        friendly = "$days day";
    }
    else if (hours > 0) {
        num = hours;
        friendly ="$hours hour"
    }
    else if (minutes > 0) {
        num = minutes
        friendly = "$minutes minute";
    }
    else {
        num = seconds
        friendly = "$seconds second";
    }
    if (num > 1) {
        friendly += "s"
    }

    return friendly + " ago";
}