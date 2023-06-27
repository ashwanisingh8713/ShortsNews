package com.ns.shortsnews.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ns.shortsnews.MainActivity
import com.ns.shortsnews.ProfileActivity
import com.ns.shortsnews.R


const val channelId = "notification_channel"
const val channelName = "com.ns.shortsnews"

class FirebaseMessagingServiceNew : FirebaseMessagingService() {
    private lateinit var intent:Intent
    private lateinit var pendingIntent: PendingIntent
    override fun onNewToken(token: String) {
        AppPreference.fcmToken = token
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            remoteMessage.notification!!.title?.let {
                remoteMessage.notification!!.body?.let { it1 ->
                    generateNotification(
                        it,
                        it1
                    )
                }
            }
        }
    }

    private fun getRemoteView(title: String, des: String):RemoteViews {
        val remoteView = RemoteViews(channelName, R.layout.notification_const)
        remoteView.setTextViewText(R.id.notification_title, title)
        remoteView.setTextViewText(R.id.notification_des, des)
        remoteView.setImageViewResource(R.id.app_logo, R.mipmap.ic_launcher)
        return remoteView
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun generateNotification(title:String, des:String){
        if (AppPreference.isUserLoggedIn) {
            intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        } else {
            intent = Intent(this, ProfileActivity::class.java)
        }
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        var builder:androidx.core.app.NotificationCompat.Builder = androidx.core.app.NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, des))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.notify(0, builder.build())
    }
}