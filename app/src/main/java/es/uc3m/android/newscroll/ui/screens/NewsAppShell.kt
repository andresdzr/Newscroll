package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size

import androidx.navigation.compose.*
import es.uc3m.android.newscroll.viewmodel.SettingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uc3m.android.newscroll.viewmodel.NewsViewModel
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsAppShell() {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val settingsViewModel: SettingsViewModel = viewModel()
    val newsViewModel: NewsViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val username = userViewModel.displayName.value
    val isLoggedIn = userViewModel.isLoggedIn.value

    // 🔥 Cargar perfil SIEMPRE
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            userViewModel.loadUserProfile()
        }
    }

    val navigateToMenuScreen: (String) -> Unit = { route ->
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo("feed") { saveState = true }
        }
        scope.launch { drawerState.close() }
    }

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
                    text = if (isLoggedIn && username.isNotBlank()) username else "Newscroll",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                HorizontalDivider()

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Feed") },
                    selected = false,
                    onClick = { navigateToMenuScreen("feed") }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.user_preferences)) },
                    selected = false,
                    onClick = { navigateToMenuScreen("settings") }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.saved_articles)) },
                    selected = false,
                    onClick = { navigateToMenuScreen("saved") }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.profile_settings)) },
                    selected = false,
                    onClick = { navigateToMenuScreen("profile") }
                )

                // 🔹 MENSAJES SOLO SI LOGUEADO
                if (isLoggedIn) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Email, contentDescription = null) },
                        label = { Text("Mensajes") },
                        selected = false,
                        onClick = { navigateToMenuScreen("messages") }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(
                            if (isLoggedIn)
                                stringResource(id = R.string.logout)
                            else
                                stringResource(id = R.string.login)
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
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isLoggedIn && username.isNotBlank())
                                username
                            else
                                stringResource(id = R.string.app_name)
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

            NavHost(
                navController = navController,
                startDestination = "feed",
                modifier = Modifier.padding(paddingValues)
            ) {

                composable("feed") {
                    NewsFeedScreen(Modifier, newsViewModel, userViewModel)
                }

                composable("saved") {
                    if (userViewModel.isLoggedIn.value) {
                        SavedScreen(newsViewModel, userViewModel)
                    } else {
                        AuthScreen(userViewModel)
                    }
                }

                composable("settings") {
                    SettingsScreen(settingsViewModel)
                }

                composable("profile") {
                    ProfileSettingsScreen(userViewModel)
                }

                composable("messages") {
                    MessagesScreen(userViewModel, navController)
                }

                composable("add_friend") {
                    AddFriendScreen(userViewModel)
                }

                // 🔥 CHAT SCREEN (CLAVE)
                composable("chat/{chatId}") { backStackEntry ->

                    val chatId = backStackEntry.arguments?.getString("chatId") ?: ""

                    ChatScreen(
                        userViewModel = userViewModel,
                        chatId = chatId
                    )
                }
            }
        }
    }
}