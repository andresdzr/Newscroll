package es.uc3m.android.newscroll

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.isSystemInDarkTheme

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager

import com.google.firebase.FirebaseApp

import es.uc3m.android.newscroll.notifications.NotificationHelper

import es.uc3m.android.newscroll.ui.screens.NewsAppShell
import es.uc3m.android.newscroll.ui.theme.NewscrollTheme

import es.uc3m.android.newscroll.viewmodel.AppTheme
import es.uc3m.android.newscroll.viewmodel.SettingsViewModel
import es.uc3m.android.newscroll.viewmodel.SettingsViewModelFactory

import es.uc3m.android.newscroll.workers.FetchNewsWorker
import es.uc3m.android.newscroll.workers.NewsNotificationWorker

import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)

        // 🔹 Crear canales de notificación
        NotificationHelper.createChannels(this)

        // 🔹 Pedir permisos Android 13+
        requestNotificationPermission()

        // 🔹 Lanzar workers
        scheduleBackgroundFetch()
        scheduleNewsNotifications()

        setContent {

            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(this)
            )

            val currentTheme = settingsViewModel.appTheme.value

            val useDarkTheme = when (currentTheme) {
                AppTheme.DARK -> true
                AppTheme.LIGHT -> false
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            NewscrollTheme(
                darkTheme = useDarkTheme
            ) {
                NewsAppShell()
            }
        }
    }

    // 🔹 Pedir permiso notificaciones
    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }

    // 🔹 Worker noticias background
    private fun scheduleBackgroundFetch() {

        val request =
            PeriodicWorkRequestBuilder<FetchNewsWorker>(
                15,
                TimeUnit.MINUTES
            ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "fetch_news",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

    // 🔹 Worker notificaciones periódicas
    private fun scheduleNewsNotifications() {

        val request =
            PeriodicWorkRequestBuilder<NewsNotificationWorker>(
                15,
                TimeUnit.MINUTES
            ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "news_notifications",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}