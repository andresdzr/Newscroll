package es.uc3m.android.newscroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import es.uc3m.android.newscroll.ui.screens.NewsAppShell
import es.uc3m.android.newscroll.ui.theme.NewscrollTheme
import es.uc3m.android.newscroll.viewmodel.AppTheme
import es.uc3m.android.newscroll.viewmodel.SettingsViewModel
import es.uc3m.android.newscroll.viewmodel.SettingsViewModelFactory
import es.uc3m.android.newscroll.workers.FetchNewsWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        scheduleBackgroundFetch()

        setContent {
            // 1. Get the ViewModel here at the root level of the app
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(this)
            )

            // 2. Observe the theme state dynamically.
            // Because this is a Compose State, any change will trigger a recomposition of the whole app
            val currentTheme = settingsViewModel.appTheme.value

            // 3. Determine if we should use dark mode based on the LIVE state
            val useDarkTheme = when (currentTheme) {
                AppTheme.DARK -> true
                AppTheme.LIGHT -> false
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            // 4. Apply the theme
            NewscrollTheme(darkTheme = useDarkTheme) {
                NewsAppShell()
            }
        }
    }

    private fun scheduleBackgroundFetch() {
        val request = PeriodicWorkRequestBuilder<FetchNewsWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "fetch_news",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}