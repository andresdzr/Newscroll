package es.uc3m.android.newscroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import es.uc3m.android.newscroll.ui.screens.NewsAppShell
import es.uc3m.android.newscroll.ui.theme.NewscrollTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewscrollTheme {
                NewsAppShell()
            }
        }
    }
}
