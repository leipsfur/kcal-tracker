package de.leipsfur.kcal_track.ui.food

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.leipsfur.kcal_track.R
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.FoodTemplate
import de.leipsfur.kcal_track.domain.model.PortionUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodTemplateDialog(
    uiState: FoodUiState,
    onNameChanged: (String) -> Unit,
    onKcalChanged: (String) -> Unit,
    onProteinChanged: (String) -> Unit,
    onCarbsChanged: (String) -> Unit,
    onFatChanged: (String) -> Unit,
    onPortionSizeChanged: (String) -> Unit,
    onPortionUnitChanged: (String) -> Unit,
    onCategoryChanged: (Long) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val isEditing = uiState.editingTemplate != null
    val title = if (isEditing) {
        stringResource(R.string.food_edit_template)
    } else {
        stringResource(R.string.food_create_template)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.templateName,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.food_template_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.templateKcal,
                    onValueChange = onKcalChanged,
                    label = { Text(stringResource(R.string.food_template_kcal)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category dropdown
                CategoryDropdown(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.templateCategoryId,
                    onCategorySelected = onCategoryChanged
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.templatePortionSize,
                        onValueChange = onPortionSizeChanged,
                        label = { Text(stringResource(R.string.food_template_portion_size)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PortionUnitDropdown(
                        selectedUnit = uiState.templatePortionUnit,
                        onUnitSelected = onPortionUnitChanged,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Optional macros
                Text(
                    text = stringResource(R.string.food_macros_optional),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.templateProtein,
                        onValueChange = onProteinChanged,
                        label = { Text(stringResource(R.string.food_protein_short)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = uiState.templateCarbs,
                        onValueChange = onCarbsChanged,
                        label = { Text(stringResource(R.string.food_carbs_short)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = uiState.templateFat,
                        onValueChange = onFatChanged,
                        label = { Text(stringResource(R.string.food_fat_short)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (uiState.templateValidationError != null) {
                    Text(
                        text = uiState.templateValidationError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
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
fun FoodEntryDialog(
    uiState: FoodUiState,
    onNameChanged: (String) -> Unit,
    onKcalChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onProteinChanged: (String) -> Unit,
    onCarbsChanged: (String) -> Unit,
    onFatChanged: (String) -> Unit,
    onCategoryChanged: (Long) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val isEditing = uiState.editingEntry != null
    val title = if (isEditing) {
        stringResource(R.string.food_edit_entry)
    } else {
        stringResource(R.string.food_create_entry)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.entryName,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.food_entry_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.entryKcal,
                    onValueChange = onKcalChanged,
                    label = { Text(stringResource(R.string.food_entry_kcal)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.entryAmount,
                    onValueChange = onAmountChanged,
                    label = { Text(stringResource(R.string.food_entry_amount)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                CategoryDropdown(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.entryCategoryId,
                    onCategorySelected = onCategoryChanged
                )

                Text(
                    text = stringResource(R.string.food_macros_optional),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.entryProtein,
                        onValueChange = onProteinChanged,
                        label = { Text(stringResource(R.string.food_protein_short)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = uiState.entryCarbs,
                        onValueChange = onCarbsChanged,
                        label = { Text(stringResource(R.string.food_carbs_short)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = uiState.entryFat,
                        onValueChange = onFatChanged,
                        label = { Text(stringResource(R.string.food_fat_short)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (uiState.entryValidationError != null) {
                    Text(
                        text = uiState.entryValidationError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
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
fun ConfirmDeleteDialog(
    title: String,
    text: String,
    onConfirm: (() -> Unit)?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            if (onConfirm != null) {
                TextButton(onClick = onConfirm) {
                    Text(
                        stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFromTemplateSheet(
    uiState: FoodUiState,
    onSearchQueryChanged: (String) -> Unit,
    onTemplateSelected: (FoodTemplate) -> Unit,
    onAmountChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.food_add_from_template),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.templateSearchQuery,
                onValueChange = onSearchQueryChanged,
                label = { Text(stringResource(R.string.food_search_template)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            val filteredTemplates = if (uiState.templateSearchQuery.isBlank()) {
                uiState.templates
            } else {
                uiState.templates.filter {
                    it.name.contains(uiState.templateSearchQuery, ignoreCase = true)
                }
            }

            val categoryMap = uiState.categories.associateBy { it.id }
            val selected = uiState.selectedTemplate

            if (selected != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = selected.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${selected.kcal} kcal / ${selected.portionSize.formatAmount()} ${selected.portionUnit}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.templateAmount,
                    onValueChange = onAmountChanged,
                    label = { Text(stringResource(R.string.food_portion_count)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                val amount = uiState.templateAmount.toDoubleOrNull() ?: 0.0
                if (amount > 0) {
                    val calculatedKcal = (selected.kcal * amount).toInt()
                    Text(
                        text = stringResource(R.string.food_calculated_kcal, calculatedKcal),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = {
                        onTemplateSelected(selected) // Reset
                        onSearchQueryChanged("")
                    }) {
                        Text(stringResource(R.string.food_change_template))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Button(onClick = onConfirm) {
                        Text(stringResource(R.string.food_add_entry))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val grouped = filteredTemplates.groupBy { it.categoryId }
                    grouped.forEach { (categoryId, templates) ->
                        val categoryName = categoryMap[categoryId]?.name ?: ""
                        item {
                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        items(templates) { template ->
                            ListItem(
                                headlineContent = { Text(template.name) },
                                supportingContent = {
                                    Text("${template.kcal} kcal / ${template.portionSize.formatAmount()} ${template.portionUnit}")
                                },
                                modifier = Modifier.clickable { onTemplateSelected(template) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCategoryManagementDialog(
    uiState: FoodUiState,
    onAddCategory: () -> Unit,
    onEditCategory: (FoodCategory) -> Unit,
    onDeleteCategory: (FoodCategory) -> Unit,
    onMoveCategoryUp: (FoodCategory) -> Unit,
    onMoveCategoryDown: (FoodCategory) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.food_manage_categories)) },
        text = {
            Column {
                uiState.categories.forEachIndexed { index, category ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(
                            onClick = { onMoveCategoryUp(category) },
                            enabled = index > 0
                        ) {
                            Icon(Icons.Filled.ArrowUpward, contentDescription = stringResource(R.string.food_move_up))
                        }
                        IconButton(
                            onClick = { onMoveCategoryDown(category) },
                            enabled = index < uiState.categories.size - 1
                        ) {
                            Icon(Icons.Filled.ArrowDownward, contentDescription = stringResource(R.string.food_move_down))
                        }
                        IconButton(onClick = { onEditCategory(category) }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.food_edit_category))
                        }
                        IconButton(onClick = { onDeleteCategory(category) }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.food_delete_category),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onAddCategory) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.food_add_category))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
fun FoodCategoryDialog(
    uiState: FoodUiState,
    onNameChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val isEditing = uiState.editingCategory != null
    val title = if (isEditing) {
        stringResource(R.string.food_edit_category)
    } else {
        stringResource(R.string.food_add_category)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = uiState.categoryName,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.food_category_name)) },
                    singleLine = true,
                    isError = uiState.categoryValidationError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.categoryValidationError != null) {
                    Text(
                        text = uiState.categoryValidationError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    categories: List<FoodCategory>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = categories.find { it.id == selectedCategoryId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedCategory?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.food_category)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortionUnitDropdown(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedUnit,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.food_portion_unit)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            PortionUnit.all.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}
