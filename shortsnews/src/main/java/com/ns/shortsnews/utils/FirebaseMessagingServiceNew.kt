package com.ns.shortsnews.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ns.shortsnews.MainActivity
import com.ns.shortsnews.ProfileActivity
import com.ns.shortsnews.R


const val channelId = "notification_channel"
const val channelName = "com.ns.shortsnews"

class FirebaseMessagingServiceNew : FirebaseMessagingService() {
    private lateinit var intent: Intent
    override fun onNewToken(token: String) {
        AppPreference.fcmToken = token
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i("firebaseNotification", remoteMessage.data.toString())
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("intent_newLaunch", "Message data payload: ${remoteMessage.data}")
        }
            if (remoteMessage.notification != null) {
//            remoteMessage.notification!!.title?.let {
                remoteMessage.notification!!.body?.let { it1 ->
                    Log.i("intent_newLaunch","Received notification data ::  ${remoteMessage.data}")
                    generateNotification(
                        remoteMessage.notification!!.title!!,
                        it1, remoteMessage.data
                    )
                }
//            }
        }
    }

    private fun getRemoteView(title: String, des: String): RemoteViews {
        val remoteView = RemoteViews(channelName, R.layout.notification_const)
        remoteView.setTextViewText(R.id.notification_title, title)
        remoteView.setTextViewText(R.id.notification_des, des)
//        remoteView.setImageViewResource(R.id.app_logo, R.mipmap.ic_launcher)
        return remoteView
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun generateNotification(title: String, des: String, notificationData: Map<String, String>) {
//        if (AppPreference.isUserLoggedIn) {
        if (isForegrounded()){
            intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        } else {
            intent = Intent(this, ProfileActivity::class.java)
        }


        intent.putExtra("videoId", notificationData["id"])
        intent.putExtra("type", notificationData["type"])
        intent.putExtra("preview_url", notificationData["videoPreviewUrl"])
        intent.putExtra("video_url", notificationData["video_url"])

        Log.i("intent_newLaunch", "intent data is in firebase notification class:: id ::" +
                " ${intent.getStringExtra("videoId") + "preview :: ${intent.getStringExtra("videoPreviewUrl")
                 + "video ${intent.getStringExtra("video_url")}"}"
                }")


//        } else {
//            intent = Intent(this, ProfileActivity::class.java)
//        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder: androidx.core.app.NotificationCompat.Builder =
            androidx.core.app.NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(
                    IconCompat.createWithAdaptiveBitmap(
                        BitmapFactory.decodeResource(
                            resources,
                            R.drawable.notification
                        )
                    )
                )
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, des))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.notify(0, builder.build())
    }

    fun isForegrounded(): Boolean {
        val appProcessInfo = RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE
    }
}