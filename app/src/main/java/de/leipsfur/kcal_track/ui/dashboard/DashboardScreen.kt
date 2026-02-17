package de.leipsfur.kcal_track.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.leipsfur.kcal_track.R
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.db.entity.ActivityCategory
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var foodEntryToDelete by remember { mutableStateOf<FoodEntry?>(null) }
    var activityEntryToDelete by remember { mutableStateOf<ActivityEntry?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.nav_dashboard)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Date Navigation
            DateNavigation(
                selectedDate = uiState.selectedDate,
                onDateChanged = viewModel::onDateChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Card
            SummaryCard(
                tdee = uiState.tdee,
                intake = uiState.totalFoodKcal,
                remaining = uiState.remainingKcal,
                bmr = uiState.bmr,
                onNavigateToSettings = onNavigateToSettings
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Entries List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Food Section
                if (uiState.foodEntries.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.dashboard_food_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    uiState.groupedFoodEntries.forEach { (category, entries) ->
                        item {
                            CategoryHeader(categoryName = category.name, totalKcal = entries.sumOf { it.kcal })
                        }
                        items(entries) { entry ->
                            EntryItem(
                                name = entry.name,
                                kcal = entry.kcal,
                                amount = entry.amount, // Using amount as specific info for food
                                onDelete = { foodEntryToDelete = entry }
                            )
                        }
                    }
                } else if (uiState.activityEntries.isEmpty()) { // Show empty only if both empty
                     item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                             Text(
                                 text = stringResource(R.string.dashboard_no_food_entries),
                                 style = MaterialTheme.typography.bodyMedium,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant
                             )
                        }
                    }
                }

                // Activity Section
                if (uiState.activityEntries.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.dashboard_activity_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    if (uiState.groupedActivityEntries.isNotEmpty()) {
                         uiState.groupedActivityEntries.entries.forEach { (category, activityList) ->
                            item {
                                CategoryHeader(categoryName = category.name, totalKcal = activityList.sumOf { it.kcal })
                            }
                            items(activityList) { entry ->
                                EntryItem(
                                    name = entry.name,
                                    kcal = entry.kcal,
                                    amount = null,
                                    onDelete = { activityEntryToDelete = entry }
                                )
                            }
                        }
                    } else {
                        // Fallback if grouping fails or no categories loaded yet
                         items(uiState.activityEntries) { entry ->
                            EntryItem(
                                name = entry.name,
                                kcal = entry.kcal,
                                amount = null,
                                onDelete = { activityEntryToDelete = entry }
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Dialogs
    if (foodEntryToDelete != null) {
        AlertDialog(
            onDismissRequest = { foodEntryToDelete = null },
            title = { Text(text = stringResource(R.string.food_delete_entry_title)) },
            text = { Text(text = stringResource(R.string.dashboard_delete_entry_confirm, foodEntryToDelete?.name ?: "")) },
            confirmButton = {
                TextButton(onClick = {
                    foodEntryToDelete?.let { viewModel.deleteFoodEntry(it) }
                    foodEntryToDelete = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { foodEntryToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (activityEntryToDelete != null) {
        AlertDialog(
            onDismissRequest = { activityEntryToDelete = null },
            title = { Text(text = stringResource(R.string.activity_delete_title)) },
            text = { Text(text = stringResource(R.string.dashboard_delete_entry_confirm, activityEntryToDelete?.name ?: "")) },
            confirmButton = {
                TextButton(onClick = {
                    activityEntryToDelete?.let { viewModel.deleteActivityEntry(it) }
                    activityEntryToDelete = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { activityEntryToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun DateNavigation(
    selectedDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit
) {
    val isToday = selectedDate == LocalDate.now()
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMANY)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onDateChanged(selectedDate.minusDays(1)) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Day")
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isToday) stringResource(R.string.dashboard_today) else selectedDate.format(formatter),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (isToday) {
                 Text(
                    text = selectedDate.format(formatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(
            onClick = { onDateChanged(selectedDate.plusDays(1)) },
            enabled = !isToday
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next Day",
                tint = if (isToday) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SummaryCard(
    tdee: Int,
    intake: Int,
    remaining: Int,
    bmr: Int?,
    onNavigateToSettings: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (bmr == null) {
                Text(
                    text = stringResource(R.string.dashboard_no_bmr),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clickable { onNavigateToSettings() }
                        .padding(bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    label = stringResource(R.string.dashboard_tdee),
                    value = tdee.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SummaryItem(
                    label = stringResource(R.string.dashboard_intake),
                    value = intake.toString(),
                    color = MaterialTheme.colorScheme.primary
                )
                SummaryItem(
                    label = stringResource(R.string.dashboard_remaining),
                    value = if (remaining < 0) remaining.toString() else remaining.toString(),
                    color = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    isBig = true
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    value: String,
    color: Color,
    isBig: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isBig) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = "kcal",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CategoryHeader(categoryName: String, totalKcal: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = categoryName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "$totalKcal kcal",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EntryItem(
    name: String,
    kcal: Int,
    amount: Double?,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (amount != null) {
                   Text(
                        text = "Menge: $amount", // Format if needed, maybe "1.5 Portionen" or so. But amount is raw Double multiplier? No, wait.
                        // In FoodEntry, amount is the multiplier. But we don't store unit name there easily unless we fetch template.
                        // Actually FoodEntry stores `amount`.
                        // For simplicity, just showing nothing for now or just "x $amount" if useful.
                        // The user story says: "Jeder Eintrag zeigt Name, Menge und kcal".
                        // So I should show Menge.
                       style = MaterialTheme.typography.bodySmall,
                       color = MaterialTheme.colorScheme.onSurfaceVariant
                   )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$kcal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}