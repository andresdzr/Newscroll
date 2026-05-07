package es.uc3m.android.newscroll.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import es.uc3m.android.newscroll.R

object NotificationHelper {

    const val NEWS_CHANNEL_ID = "news_channel"
    const val MESSAGE_CHANNEL_ID = "message_channel"

    fun createChannels(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val newsChannel = NotificationChannel(
                NEWS_CHANNEL_ID,
                "News Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val messageChannel = NotificationChannel(
                MESSAGE_CHANNEL_ID,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager

            manager.createNotificationChannel(newsChannel)
            manager.createNotificationChannel(messageChannel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNewsNotification(
        context: Context,
        title: String,
        content: String
    ) {

        val notification = NotificationCompat.Builder(
            context,
            NEWS_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(1, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showMessageNotification(
        context: Context,
        username: String,
        message: String
    ) {

        val notification = NotificationCompat.Builder(
            context,
            MESSAGE_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(username)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(System.currentTimeMillis().toInt(), notification)

    }
}



