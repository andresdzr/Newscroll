package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import es.uc3m.android.newscroll.R
import es.uc3m.android.newscroll.viewmodel.*
import kotlinx.coroutines.launch




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsAppShell() {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val context = LocalContext.current

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )
    val newsViewModel: NewsViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val username = userViewModel.displayName.value
    val photoUrl = userViewModel.photoUrl.value
    val isLoggedIn = userViewModel.isLoggedIn.value

    val selectedFilters by settingsViewModel.selectedFilters
    val currentTheme by settingsViewModel.appTheme
    val locationMode by settingsViewModel.locationMode

    val currentRoute by navController.currentBackStackEntryAsState()
    val onFeedScreen = currentRoute?.destination?.route == "feed" || currentRoute == null

    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) userViewModel.loadUserProfile()
    }

    LaunchedEffect(selectedFilters) {
        newsViewModel.fetchNews(selectedFilters)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet {

                DrawerHero(
                    username = if (isLoggedIn && username.isNotBlank())
                        username
                    else
                        stringResource(id = R.string.app_name),
                    photoUrl = photoUrl,
                    isLoggedIn = isLoggedIn
                )

                Spacer(Modifier.height(8.dp))

                DrawerNavItem(
                    icon = Icons.Default.Home,
                    label = stringResource(R.string.feed),
                    selected = currentRoute?.destination?.route == "feed",
                    onClick = {
                        navController.navigate("feed")
                        scope.launch { drawerState.close() }
                    }
                )

                DrawerNavItem(
                    icon = Icons.Default.Settings,
                    label = stringResource(id = R.string.user_preferences),
                    selected = currentRoute?.destination?.route == "settings",
                    onClick = {
                        navController.navigate("settings")
                        scope.launch { drawerState.close() }
                    }
                )

                if (isLoggedIn) {
                    DrawerNavItem(
                        icon = Icons.Default.Favorite,
                        label = stringResource(id = R.string.saved_articles),
                        selected = currentRoute?.destination?.route == "saved",
                        onClick = {
                            navController.navigate("saved")
                            scope.launch { drawerState.close() }
                        }
                    )
                }

                DrawerNavItem(
                    icon = Icons.Default.AccountCircle,
                    label = stringResource(id = R.string.profile_settings),
                    selected = currentRoute?.destination?.route == "profile",
                    onClick = {
                        navController.navigate("profile")
                        scope.launch { drawerState.close() }
                    }
                )

                if (isLoggedIn) {
                    DrawerNavItem(
                        icon = Icons.Default.Email,
                        label = stringResource(R.string.messages),
                        selected = currentRoute?.destination?.route == "messages",
                        onClick = {
                            navController.navigate("messages")
                            scope.launch { drawerState.close() }
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp))
                Spacer(Modifier.height(8.dp))

                DrawerNavItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    label = if (isLoggedIn)
                        stringResource(id = R.string.logout)
                    else
                        stringResource(id = R.string.login),
                    selected = false,
                    onClick = {
                        if (isLoggedIn) {
                            userViewModel.logout()
                            navController.navigate("feed")
                        } else {
                            navController.navigate("profile")
                        }
                        scope.launch { drawerState.close() }
                    }
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name),
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
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            settingsViewModel.setTheme(
                                when (currentTheme) {
                                    AppTheme.SYSTEM -> AppTheme.LIGHT
                                    AppTheme.LIGHT -> AppTheme.DARK
                                    AppTheme.DARK -> AppTheme.SYSTEM
                                }
                            )
                        }) {
                            Icon(
                                imageVector = when (currentTheme) {
                                    AppTheme.LIGHT -> Icons.Default.LightMode
                                    AppTheme.DARK -> Icons.Default.DarkMode
                                    AppTheme.SYSTEM -> Icons.Default.AutoAwesome
                                },
                                contentDescription = null
                            )
                        }

                        IconButton(onClick = {
                            settingsViewModel.setLocationMode(
                                when (locationMode) {
                                    LocationMode.GLOBAL -> LocationMode.NATIONAL
                                    LocationMode.NATIONAL -> LocationMode.LOCAL
                                    LocationMode.LOCAL -> LocationMode.GLOBAL
                                }
                            )
                        }) {
                            Icon(
                                imageVector = when (locationMode) {
                                    LocationMode.LOCAL -> Icons.Default.LocationOn
                                    LocationMode.NATIONAL -> Icons.Default.Map
                                    LocationMode.GLOBAL -> Icons.Default.Language
                                },
                                contentDescription = null
                            )
                        }

                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = null)
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
                    if (userViewModel.isLoggedIn.value)
                        SavedScreen(newsViewModel, userViewModel)
                    else
                        AuthScreen(userViewModel)
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

                composable("chat/{chatId}") { backStackEntry ->
                    val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                    ChatScreen(
                        userViewModel = userViewModel,
                        newsViewModel = newsViewModel,
                        chatId = chatId,
                        navController = navController
                    )
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            CategoryFilterSheet(
                settingsViewModel = settingsViewModel,
                onDone = { showFilterSheet = false }
            )
        }
    }
}

@Composable
private fun DrawerHero(
    username: String,
    photoUrl: String?,
    isLoggedIn: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            if (!photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            }

            Text(
                text = username,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = if (isLoggedIn)
                    stringResource(R.string.newscroll_member)
                else
                    stringResource(R.string.sign_in_personalize),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun DrawerNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryFilterSheet(
    settingsViewModel: SettingsViewModel,
    onDone: () -> Unit
) {

    val selectedFilters by settingsViewModel.selectedFilters

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = stringResource(id = R.string.filters),
            style = MaterialTheme.typography.titleLarge
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            filterOptions.forEach { filter ->

                FilterChip(
                    selected = selectedFilters.contains(filter),
                    onClick = { settingsViewModel.toggleFilter(filter) },
                    label = { Text(filter) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.done))
        }
    }
}
