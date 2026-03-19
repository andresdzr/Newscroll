package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import es.uc3m.android.newscroll.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.AccountCircle

import androidx.navigation.compose.*
import es.uc3m.android.newscroll.viewmodel.SettingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uc3m.android.newscroll.viewmodel.NewsViewModel
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsAppShell() {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()

    // Helper to save state when switching screens
    val navigateToMenuScreen: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo("feed") {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        scope.launch { drawerState.close() }
    }

    val settingsViewModel: SettingsViewModel = viewModel()
    val newsViewModel: NewsViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val username by userViewModel.username
    val isLoggedIn by userViewModel.isLoggedIn

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                IconButton(
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.close_menu_description)
                    )
                }

                Text(
                    text = username,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                HorizontalDivider()

                // Feed
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Feed") },
                    selected = false,
                    onClick = { navigateToMenuScreen("feed") }
                )

                // Settings
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.user_preferences)) },
                    selected = false,
                    onClick = { navigateToMenuScreen("settings") }
                )
                // Saved
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.saved_articles)) },
                    selected = false,
                    onClick = { navigateToMenuScreen("saved") }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    label = { Text("Profile Settings") },
                    selected = false,
                    onClick = { navigateToMenuScreen("profile") }
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(if (isLoggedIn) "Logout" else "Login")
                    },
                    selected = false,
                    onClick = {
                        if (isLoggedIn) {
                            userViewModel.logout()
                        } else {
                            navController.navigate("profile")
                        }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            AsyncImage(
                                model = "https://picsum.photos/seed/user/100/100",
                                contentDescription = stringResource(id = R.string.open_menu_description),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->

            // Screens
            NavHost(
                navController = navController,
                startDestination = "feed",
                modifier = Modifier.padding(paddingValues)
            ) {

                composable("feed") {
                    NewsFeedScreen(Modifier, newsViewModel)
                }

                composable("saved") {
                    SavedScreen(newsViewModel)
                }

                composable("settings") {
                    SettingsScreen(settingsViewModel)
                }

                composable("profile") {
                    ProfileSettingsScreen(userViewModel)
                }
            }
        }
    }
}