package com.example.tomatonotpotato.services

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tomatonotpotato.MainActivity
import com.example.tomatonotpotato.R


fun showTimerFinishedNotification(context: Context, title: String, message: String) {
    val channelId = "pomodoro_timer_channel"
    val notificationId = 1
    val largeIconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.official_icon)
    val intent = Intent(
        context, MainActivity::class.java
    )

    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.status_bar_icon)
        .setLargeIcon(largeIconBitmap)
        .setContentIntent(pendingIntent)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        // This is a safeguard; it checks for permission before trying to notify.
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If permission is not granted, do nothing.
            return
        }
        notify(notificationId, builder.build())
    }

}