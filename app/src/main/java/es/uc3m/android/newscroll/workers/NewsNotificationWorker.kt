package es.uc3m.android.newscroll.workers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

import androidx.core.content.ContextCompat

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

import es.uc3m.android.newscroll.notifications.NotificationHelper

class NewsNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        return try {
            if (
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                NotificationHelper.showNewsNotification(
                    context = applicationContext,
                    title = "Newscroll",
                    content = "Descubre las últimas noticias mundiales"
                )
            }

            Result.success()

        } catch (e: Exception) {

            e.printStackTrace()

            Result.failure()
        }
    }
}