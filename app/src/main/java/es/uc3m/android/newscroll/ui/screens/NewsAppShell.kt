package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import es.uc3m.android.newscroll.R
import es.uc3m.android.newscroll.viewmodel.NewsViewModel
import es.uc3m.android.newscroll.viewmodel.SettingsViewModel
import es.uc3m.android.newscroll.viewmodel.SettingsViewModelFactory
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsAppShell() {

    // Drawer, navigation and coroutine scope
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val context = LocalContext.current

    // ViewModels initialization
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )
    val newsViewModel: NewsViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    // User state
    val username = userViewModel.displayName.value
    val isLoggedIn = userViewModel.isLoggedIn.value

    // Load profile when user logs in
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            userViewModel.loadUserProfile()
        }
    }

    // Helper function to navigate and close drawer
    val navigateToMenuScreen: (String) -> Unit = { route ->
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo("feed") { saveState = true }
        }
        scope.launch { drawerState.close() }
    }

    // Navigation drawer container
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                // Close drawer button
                IconButton(
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.close_menu_description)
                    )
                }

                // User name or app title
                Text(
                    text = if (isLoggedIn && username.isNotBlank()) username else "Newscroll",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                HorizontalDivider()

                // Feed navigation
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Feed") },
                    selected = false,
                    onClick = { navigateToMenuScreen("feed") }
                )

                // Settings navigation
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.user_preferences)) },
                    selected = false,
                    onClick = { navigateToMenuScreen("settings") }
                )

                // Saved articles (only if logged in)
                if (isLoggedIn) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        label = { Text(stringResource(id = R.string.saved_articles)) },
                        selected = false,
                        onClick = { navigateToMenuScreen("saved") }
                    )
                }

                // Profile settings
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.profile_settings)) },
                    selected = false,
                    onClick = { navigateToMenuScreen("profile") }
                )

                // Messages (only if logged in)
                if (isLoggedIn) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Email, contentDescription = null) },
                        label = { Text("Mensajes") },
                        selected = false,
                        onClick = { navigateToMenuScreen("messages") }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Login / logout action
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(
                            if (isLoggedIn) stringResource(id = R.string.logout)
                            else stringResource(id = R.string.login)
                        )
                    },
                    selected = false,
                    onClick = {
                        if (isLoggedIn) {
                            userViewModel.logout()
                            navController.navigate("feed") {
                                popUpTo("feed") { inclusive = true }
                            }
                        } else {
                            navController.navigate("profile")
                        }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {

        // Main scaffold with top app bar
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "NEWSCROLL",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                painter = painterResource(id = R.drawable.menu_image),
                                contentDescription = stringResource(id = R.string.open_menu_description),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->

            // Navigation host for all screens
            NavHost(
                navController = navController,
                startDestination = "feed",
                modifier = Modifier.padding(paddingValues)
            ) {

                // Feed screen
                composable("feed") {
                    NewsFeedScreen(Modifier, newsViewModel, userViewModel)
                }

                // Saved articles (requires login)
                composable("saved") {
                    if (userViewModel.isLoggedIn.value)
                        SavedScreen(newsViewModel, userViewModel)
                    else
                        AuthScreen(userViewModel)
                }

                // Settings screen
                composable("settings") {
                    SettingsScreen(settingsViewModel)
                }

                // Profile screen
                composable("profile") {
                    ProfileSettingsScreen(userViewModel)
                }

                // Messages screen
                composable("messages") {
                    MessagesScreen(userViewModel, navController)
                }

                // Add friend screen
                composable("add_friend") {
                    AddFriendScreen(userViewModel)
                }

                // Chat screen with dynamic chatId
                composable("chat/{chatId}") { backStackEntry ->
                    val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                    ChatScreen(userViewModel = userViewModel, chatId = chatId)
                }
            }
        }
    }
}