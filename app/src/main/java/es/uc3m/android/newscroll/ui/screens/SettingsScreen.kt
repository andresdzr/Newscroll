package es.uc3m.android.newscroll.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.R
import es.uc3m.android.newscroll.viewmodel.AppTheme
import es.uc3m.android.newscroll.viewmodel.LocationMode
import es.uc3m.android.newscroll.viewmodel.SettingsViewModel

val filterOptions = listOf(
    "Technology, ML & AI",
    "Finance & Investing",
    "Politics & Geopolitics",
    "History & Philosophy",
    "Survival Knowledge"
)

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ThemeSection(viewModel)
        FiltersSection(viewModel)
        LocationSection(viewModel)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThemeSection(viewModel: SettingsViewModel) {

    var expanded by remember { mutableStateOf(false) }
    val selectedTheme = viewModel.appTheme.value

    Card(shape = RoundedCornerShape(16.dp)) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("App Theme", style = MaterialTheme.typography.titleMedium)

                Icon(
                    imageVector = if (expanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {

                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        listOf(AppTheme.SYSTEM, AppTheme.LIGHT, AppTheme.DARK).forEach { theme ->

                            FilterChip(
                                selected = selectedTheme == theme,
                                onClick = { viewModel.setTheme(theme) },
                                label = {
                                    Text(
                                        when (theme) {
                                            AppTheme.SYSTEM -> "System"
                                            AppTheme.LIGHT -> "Light"
                                            AppTheme.DARK -> "Dark"
                                        }
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersSection(viewModel: SettingsViewModel) {

    var expanded by remember { mutableStateOf(false) }
    val selectedFilters = viewModel.selectedFilters.value

    Card(shape = RoundedCornerShape(16.dp)) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(id = R.string.filters), style = MaterialTheme.typography.titleMedium)

                Icon(
                    imageVector = if (expanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {

                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        filterOptions.forEach { filter ->

                            FilterChip(
                                selected = selectedFilters.contains(filter),
                                onClick = { viewModel.toggleFilter(filter) },
                                label = { Text(filter) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LocationSection(viewModel: SettingsViewModel) {

    var expanded by remember { mutableStateOf(false) }
    val selectedMode = viewModel.locationMode.value
    val radius = viewModel.radiusKm.value

    Card(shape = RoundedCornerShape(16.dp)) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(id = R.string.location), style = MaterialTheme.typography.titleMedium)

                Icon(
                    imageVector = if (expanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {

                Column {

                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        listOf(
                            LocationMode.LOCAL,
                            LocationMode.NATIONAL,
                            LocationMode.GLOBAL
                        ).forEach { mode ->

                            FilterChip(
                                selected = selectedMode == mode,
                                onClick = { viewModel.setLocationMode(mode) },
                                label = {
                                    Text(
                                        when (mode) {
                                            LocationMode.LOCAL -> "Local"
                                            LocationMode.NATIONAL -> "National"
                                            LocationMode.GLOBAL -> "Global"
                                        }
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedMode == LocationMode.LOCAL) {

                        Text("Radius: ${radius.toInt()} km")

                        Slider(
                            value = radius,
                            onValueChange = { viewModel.setRadius(it) },
                            valueRange = 5f..500f
                        )
                    }
                }
            }
        }
    }
}