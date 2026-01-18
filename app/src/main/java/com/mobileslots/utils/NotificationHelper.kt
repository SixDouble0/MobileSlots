package com.mobileslots.utils

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.mobileslots.MobileSlotsApplication
import com.mobileslots.R

object NotificationHelper {
    private var notificationId = 0

    fun showWinNotification(context: Context, winAmount: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, MobileSlotsApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_big_win))
            .setContentText(context.getString(R.string.notification_big_win_message, winAmount))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId++, notification)
    }

    fun showAchievementNotification(context: Context, achievementName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, MobileSlotsApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Achievement Unlocked!")
            .setContentText(achievementName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId++, notification)
    }
}
