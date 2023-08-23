package com.ns.shortsnews.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ns.shortsnews.MainActivity
import com.ns.shortsnews.PlainVideoActivity
import com.ns.shortsnews.R
import com.ns.shortsnews.data.model.VideoClikedItem
import com.videopager.utils.CategoryConstants


const val channelId = "notification_channel"
const val channelName = "com.ns.shortsnews"

class FirebaseMessagingServiceNew : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        AppPreference.fcmToken = token
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i("firebaseNotification", remoteMessage.data.toString())
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("intent_newLaunch", "Message data payload: ${remoteMessage.data}")
        }
        if (remoteMessage.notification != null) {
            remoteMessage.notification!!.body?.let { it1 ->
                Log.i("intent_newLaunch", "Received notification data ::  ${remoteMessage.data}")
                generateNotification(
                    remoteMessage.notification!!.title!!,
                    remoteMessage.notification?.body.toString(), remoteMessage.data
                )
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun generateNotification(title: String, body: String, notificationData: Map<String, String>) {
        val videoId = notificationData[AppConstants.ID]!!
        val type = notificationData[AppConstants.TYPE]!!
        val previewUrl = notificationData[AppConstants.VIDEO_PREVIEW_URL]!!
        val videoUrl = notificationData[AppConstants.VIDEO_URL]!!
        Log.i("intent_newLaunch", " on generate notification id: $videoId type:$type previewUrl: $previewUrl videoUrl:$videoUrl")

        val intent = Intent(applicationContext, PlainVideoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(AppConstants.ID, videoId)
        intent.putExtra(AppConstants.TYPE, type)
        intent.putExtra(AppConstants.VIDEO_PREVIEW_URL, previewUrl)
        intent.putExtra(AppConstants.VIDEO_URL, videoUrl)
        intent.putExtra(PlainVideoActivity.KEY_VIDEO_CLICKED_ITEM,VideoClikedItem("",
            0, CategoryConstants.NOTIFICATION_VIDEO_DATA))
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )

        var builder: androidx.core.app.NotificationCompat.Builder =
            androidx.core.app.NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor = Color.BLUE
                enableVibration(true)
            }
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.notify(0, builder.build())
    }
}