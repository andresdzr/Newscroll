package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import es.uc3m.android.newscroll.R
import kotlinx.coroutines.launch

enum class Screen {
    FEED,
    SAVED,
    SETTINGS
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsAppShell() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf(Screen.FEED) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // 1. The Back Arrow to close the menu
                IconButton(
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.close_menu_description)
                    )
                }

                // 2. A placeholder user header to make it look complete
                Text(
                    text = "Jane Doe", // We can hardcode this just for the visual mockup for now
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                HorizontalDivider()

                Spacer(modifier = Modifier.height(8.dp))

                // 3. The Menu Items with Icons
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Feed") },
                    selected = currentScreen == Screen.FEED,
                    onClick = {
                        currentScreen = Screen.FEED
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(text = stringResource(id = R.string.user_preferences)) },
                    selected = currentScreen == Screen.SETTINGS,
                    onClick = {
                        currentScreen = Screen.SETTINGS
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text(text = stringResource(id = R.string.saved_articles)) },
                    selected = currentScreen == Screen.SAVED,
                    onClick = {
                        currentScreen = Screen.SAVED
                        scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.weight(1f)) // This pushes the logout button to the very bottom!

                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                    label = { Text(text = stringResource(id = R.string.logout)) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    navigationIcon = {
                        // 4. The Circular User Profile Icon
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            AsyncImage(
                                model = "https://picsum.photos/seed/user/100/100", // Dummy profile pic
                                contentDescription = stringResource(id = R.string.open_menu_description),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape) // This is what makes it a perfect circle!
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            when (currentScreen) {
                Screen.FEED -> NewsFeedScreen(modifier = Modifier.padding(paddingValues))
                Screen.SAVED -> SavedScreen(modifier = Modifier.padding(paddingValues))
                Screen.SETTINGS -> SettingsScreen(modifier = Modifier.padding(paddingValues))
            }
        }
    }
}