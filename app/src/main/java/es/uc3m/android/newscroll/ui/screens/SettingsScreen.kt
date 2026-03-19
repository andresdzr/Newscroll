package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.R
import es.uc3m.android.newscroll.viewmodel.SettingsViewModel
import es.uc3m.android.newscroll.viewmodel.LocationMode

val filterOptions = listOf(
    "Technology, ML & AI",
    "Finance & Investing",
    "Politics & Geopolitics",
    "History & Philosophy",
    "Survival Knowledge"
)


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        FiltersSection(viewModel)

        Spacer(modifier = Modifier.height(12.dp))

        LocationSection(viewModel)
    }
}

@Composable
fun FiltersSection(viewModel: SettingsViewModel) {

    var expanded by remember { mutableStateOf(false) }
    val selectedFilters = viewModel.selectedFilters.value

    Column(modifier = Modifier.fillMaxWidth()) {

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.filters),
                style = MaterialTheme.typography.titleMedium
            )

            Icon(
                imageVector = if (expanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = expanded) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    FilterChip(
                        selected = selectedFilters.contains(filter),
                        onClick = { viewModel.toggleFilter(filter) },
                        label = { Text(filter) }
                    )
                }
            }
        }
    }
}

@Composable
fun LocationSection(viewModel: SettingsViewModel) {

    var expanded by remember { mutableStateOf(false) }

    val selectedMode = viewModel.locationMode.value
    val radius = viewModel.radiusKm.value

    Column(modifier = Modifier.fillMaxWidth()) {

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(id = R.string.location), style = MaterialTheme.typography.titleMedium)

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

                val modes = remember {
                    listOf(
                        LocationMode.LOCAL,
                        LocationMode.NATIONAL,
                        LocationMode.GLOBAL
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modes.forEach { mode ->

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
                            }
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