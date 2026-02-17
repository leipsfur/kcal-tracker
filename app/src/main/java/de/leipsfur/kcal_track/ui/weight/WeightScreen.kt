package de.leipsfur.kcal_track.ui.weight

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.leipsfur.kcal_track.R
import de.leipsfur.kcal_track.data.db.entity.WeightEntry
import java.time.format.DateTimeFormatter
import java.util.Locale

import de.leipsfur.kcal_track.ui.shared.KcalTrackCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    viewModel: WeightViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var entryToDelete by remember { mutableStateOf<WeightEntry?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.weight_title)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.weight_add))
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (uiState.entries.size >= 2) {
                WeightChart(
                    entries = uiState.entries.map { it.entry }.reversed(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                )
            } else if (uiState.entries.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.weight_chart_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (uiState.entries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.weight_no_entries))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.entries) { entryWithDiff ->
                        WeightEntryItem(
                            entryWithDiff = entryWithDiff,
                            onDelete = { entryToDelete = it.entry }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        WeightAddDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { weight ->
                viewModel.addWeight(weight)
                showAddDialog = false
            }
        )
    }

    entryToDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text(stringResource(R.string.weight_delete_title)) },
            text = {
                val dateStr = entry.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                Text(stringResource(R.string.weight_delete_confirm, dateStr))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEntry(entry)
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun WeightEntryItem(
    entryWithDiff: WeightEntryWithDiff,
    onDelete: (WeightEntryWithDiff) -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN) }

    KcalTrackCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entryWithDiff.entry.date.format(dateFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${entryWithDiff.entry.weightKg} ${stringResource(R.string.weight_unit)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            entryWithDiff.diff?.let { diff ->
                val color = if (diff > 0) MaterialTheme.colorScheme.error else if (diff < 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                val text = if (diff > 0) {
                    stringResource(R.string.weight_diff_positive, diff)
                } else {
                    stringResource(R.string.weight_diff_negative, diff)
                }
                Text(
                    text = text,
                    color = color,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            IconButton(onClick = { onDelete(entryWithDiff) }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WeightAddDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var weightInput by remember { mutableStateOf("") }
    val isError = weightInput.isNotEmpty() && weightInput.toDoubleOrNull() == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.weight_add)) },
        text = {
            OutlinedTextField(
                value = weightInput,
                onValueChange = { weightInput = it.replace(',', '.') },
                label = { Text(stringResource(R.string.weight_label)) },
                placeholder = { Text(stringResource(R.string.weight_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = isError,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val weight = weightInput.toDoubleOrNull()
                    if (weight != null && weight > 0) {
                        onConfirm(weight)
                    }
                },
                enabled = weightInput.toDoubleOrNull()?.let { it > 0 } ?: false
            ) {
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

@Composable
fun WeightChart(
    entries: List<WeightEntry>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = modifier) {
        if (entries.size < 2) return@Canvas

        val minWeight = entries.minOf { it.weightKg } - 1.0
        val maxWeight = entries.maxOf { it.weightKg } + 1.0
        val weightRange = maxWeight - minWeight

        val width = size.width
        val height = size.height
        val spacing = width / (entries.size - 1)

        val points = entries.mapIndexed { index, entry ->
            val x = index * spacing
            val y = height - ((entry.weightKg - minWeight) / weightRange * height).toFloat()
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }

        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw points
        points.forEach { point ->
            drawCircle(
                color = primaryColor,
                radius = 4.dp.toPx(),
                center = point
            )
        }
    }
}
