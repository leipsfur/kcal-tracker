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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.leipsfur.kcal_track.R
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import de.leipsfur.kcal_track.ui.shared.KcalTrackCard
import de.leipsfur.kcal_track.ui.shared.KcalTrackHeader
import de.leipsfur.kcal_track.ui.shared.KcalTrackTopBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var foodEntryToDelete by remember { mutableStateOf<FoodEntry?>(null) }
    var activityEntryToDelete by remember { mutableStateOf<ActivityEntry?>(null) }
    var showBmrEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            KcalTrackTopBar(title = stringResource(R.string.nav_dashboard))
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            DateNavigation(
                selectedDate = uiState.selectedDate,
                onDateChanged = viewModel::onDateChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            SummaryCard(
                tdee = uiState.tdee,
                intake = uiState.totalFoodKcal,
                remaining = uiState.remainingKcal,
                bmr = uiState.bmr,
                onEditBmr = { showBmrEditDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.foodEntries.isNotEmpty()) {
                    item {
                        KcalTrackHeader(text = stringResource(R.string.dashboard_food_title))
                    }

                    uiState.groupedFoodEntries.forEach { (category, entries) ->
                        item(key = "food_cat_${category.id}") {
                            CategoryHeader(categoryName = category.name, totalKcal = entries.sumOf { it.kcal })
                        }
                        items(entries, key = { it.id }) { entry ->
                            EntryItem(
                                name = entry.name,
                                kcal = entry.kcal,
                                amount = entry.amount,
                                unit = entry.portionUnit,
                                time = entry.time,
                                onDelete = { foodEntryToDelete = entry }
                            )
                        }
                    }
                } else if (uiState.activityEntries.isEmpty()) {
                     item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                             Text(
                                 text = stringResource(R.string.dashboard_no_entries),
                                 style = MaterialTheme.typography.bodyLarge,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant,
                                 textAlign = TextAlign.Center
                             )
                        }
                    }
                }

                if (uiState.activityEntries.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        KcalTrackHeader(text = stringResource(R.string.dashboard_activity_title))
                    }

                    if (uiState.groupedActivityEntries.isNotEmpty()) {
                         uiState.groupedActivityEntries.entries.forEach { (category, activityList) ->
                            item(key = "act_cat_${category.id}") {
                                CategoryHeader(categoryName = category.name, totalKcal = activityList.sumOf { it.kcal })
                            }
                            items(activityList, key = { it.id }) { entry ->
                                EntryItem(
                                    name = entry.name,
                                    kcal = entry.kcal,
                                    amount = null,
                                    unit = null,
                                    onDelete = { activityEntryToDelete = entry }
                                )
                            }
                        }
                    } else {
                         items(uiState.activityEntries, key = { it.id }) { entry ->
                            EntryItem(
                                name = entry.name,
                                kcal = entry.kcal,
                                amount = null,
                                unit = null,
                                onDelete = { activityEntryToDelete = entry }
                            )
                        }
                    }
                }
            }
        }
    }

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

    if (showBmrEditDialog) {
        BmrEditDialog(
            currentBmr = uiState.bmr,
            onSave = { newBmr ->
                viewModel.updateBmr(newBmr)
                showBmrEditDialog = false
            },
            onDismiss = { showBmrEditDialog = false }
        )
    }
}

@Composable
fun DateNavigation(
    selectedDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit
) {
    val isToday = selectedDate == LocalDate.now()
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onDateChanged(selectedDate.minusDays(1)) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.dashboard_previous_day))
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
                contentDescription = stringResource(R.string.dashboard_next_day),
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
    onEditBmr: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (bmr == null || bmr == 0) {
                Text(
                    text = stringResource(R.string.dashboard_no_bmr),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable { onEditBmr() }
                        .padding(bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Top
            ) {
                SummaryItem(
                    label = stringResource(R.string.dashboard_tdee),
                    value = tdee.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onEditBmr() }
                )
                SummaryItem(
                    label = stringResource(R.string.dashboard_intake),
                    value = intake.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                SummaryItem(
                    label = stringResource(R.string.dashboard_remaining),
                    value = remaining.toString(),
                    color = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    isBig = true,
                    modifier = Modifier.weight(1f)
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
    modifier: Modifier = Modifier,
    isBig: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = value,
            style = if (isBig) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.kcal_unit),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CategoryHeader(categoryName: String, totalKcal: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = categoryName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = "$totalKcal ${stringResource(R.string.kcal_unit)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun EntryItem(
    name: String,
    kcal: Int,
    amount: Double?,
    unit: String?,
    time: String? = null,
    onDelete: () -> Unit
) {
    KcalTrackCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (time != null) "$time \u2014 $name" else name,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (amount != null) {
                   val amountText = if (amount == amount.toInt().toDouble()) {
                       amount.toInt().toString()
                   } else {
                       "%.1f".format(Locale.getDefault(), amount)
                   }
                   Text(
                        text = "Menge: $amountText ${unit ?: ""}".trim(),
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
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun BmrEditDialog(
    currentBmr: Int?,
    onSave: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var bmrInput by remember { mutableStateOf(currentBmr?.toString() ?: "") }
    var validationError by remember { mutableStateOf<String?>(null) }
    val errorMessage = stringResource(R.string.dashboard_edit_bmr_error)

    fun validate(): Int? {
        val value = bmrInput.trim().toIntOrNull()
        return if (value != null && value in 500..5000) {
            validationError = null
            value
        } else {
            validationError = errorMessage
            null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dashboard_edit_bmr_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = bmrInput,
                    onValueChange = {
                        bmrInput = it
                        validationError = null
                    },
                    label = { Text(stringResource(R.string.dashboard_edit_bmr_label)) },
                    suffix = { Text(stringResource(R.string.kcal_unit)) },
                    isError = validationError != null,
                    supportingText = if (validationError != null) {
                        { Text(text = validationError!!, color = MaterialTheme.colorScheme.error) }
                    } else {
                        null
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { validate()?.let { onSave(it) } }
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { validate()?.let { onSave(it) } }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
