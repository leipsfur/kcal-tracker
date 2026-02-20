package de.leipsfur.kcal_track.ui.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.leipsfur.kcal_track.R
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.Ingredient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientDialog(
    uiState: RecipeUiState,
    onNameChanged: (String) -> Unit,
    onKcalPer100Changed: (String) -> Unit,
    onReferenceUnitChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onProteinChanged: (String) -> Unit,
    onCarbsChanged: (String) -> Unit,
    onFatChanged: (String) -> Unit,
    onSuggestionSelected: (Ingredient) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val isEditing = uiState.editingIngredient != null
    val title = if (isEditing) {
        stringResource(R.string.recipe_edit)
    } else {
        stringResource(R.string.recipe_add_ingredient)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Name field with autocomplete suggestions
                OutlinedTextField(
                    value = uiState.ingredientName,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.recipe_ingredient_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.ingredientSuggestions.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 150.dp)
                    ) {
                        items(uiState.ingredientSuggestions, key = { it.id }) { suggestion ->
                            ListItem(
                                headlineContent = { Text(suggestion.name) },
                                supportingContent = {
                                    Text("${suggestion.kcalPer100.toInt()} kcal/100${suggestion.referenceUnit}")
                                },
                                modifier = Modifier.clickable { onSuggestionSelected(suggestion) }
                            )
                        }
                    }
                }

                // kcal per 100g/ml
                OutlinedTextField(
                    value = uiState.ingredientKcalPer100,
                    onValueChange = onKcalPer100Changed,
                    label = { Text("kcal pro 100${uiState.ingredientReferenceUnit}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Reference unit selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.ingredientReferenceUnit == "g",
                        onClick = { onReferenceUnitChanged("g") },
                        label = { Text(stringResource(R.string.recipe_ingredient_unit_g)) }
                    )
                    FilterChip(
                        selected = uiState.ingredientReferenceUnit == "ml",
                        onClick = { onReferenceUnitChanged("ml") },
                        label = { Text(stringResource(R.string.recipe_ingredient_unit_ml)) }
                    )
                }

                // Amount
                OutlinedTextField(
                    value = uiState.ingredientAmount,
                    onValueChange = onAmountChanged,
                    label = {
                        Text(
                            "${stringResource(R.string.recipe_ingredient_amount)} (${uiState.ingredientReferenceUnit})"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Macros (optional)
                Text(
                    text = "${stringResource(R.string.food_macros_optional)} pro 100${uiState.ingredientReferenceUnit}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.ingredientProtein,
                        onValueChange = onProteinChanged,
                        label = { Text(stringResource(R.string.food_protein_short)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = uiState.ingredientCarbs,
                        onValueChange = onCarbsChanged,
                        label = { Text(stringResource(R.string.food_carbs_short)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = uiState.ingredientFat,
                        onValueChange = onFatChanged,
                        label = { Text(stringResource(R.string.food_fat_short)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (uiState.ingredientValidationError != null) {
                    Text(
                        text = uiState.ingredientValidationError,
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
fun FinishRecipeDialog(
    uiState: RecipeUiState,
    onPortionsCookedChanged: (String) -> Unit,
    onPortionsEatenChanged: (String) -> Unit,
    onCategoryChanged: (Long) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.recipe_finish)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Total kcal display
                Text(
                    text = stringResource(R.string.recipe_total_kcal, uiState.totalKcal),
                    style = MaterialTheme.typography.titleMedium
                )

                // Portions cooked
                OutlinedTextField(
                    value = uiState.finishPortionsCooked,
                    onValueChange = onPortionsCookedChanged,
                    label = { Text(stringResource(R.string.recipe_portions_cooked)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Portions eaten
                OutlinedTextField(
                    value = uiState.finishPortionsEaten,
                    onValueChange = onPortionsEatenChanged,
                    label = { Text(stringResource(R.string.recipe_portions_eaten)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Calculated kcal
                val portionsCooked = uiState.finishPortionsCooked.trim().toDoubleOrNull()
                val portionsEaten = uiState.finishPortionsEaten.trim().toDoubleOrNull()
                if (portionsCooked != null && portionsCooked > 0 && portionsEaten != null && portionsEaten > 0) {
                    val calculatedKcal = (uiState.totalKcal / portionsCooked * portionsEaten).toInt()
                    Text(
                        text = stringResource(R.string.food_calculated_kcal, calculatedKcal),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Category dropdown
                RecipeCategoryDropdown(
                    categories = uiState.foodCategories,
                    selectedCategoryId = uiState.finishCategoryId,
                    onCategorySelected = onCategoryChanged
                )

                if (uiState.finishValidationError != null) {
                    Text(
                        text = uiState.finishValidationError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
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
fun AddPortionsDialog(
    uiState: RecipeUiState,
    onAmountChanged: (String) -> Unit,
    onCategoryChanged: (Long) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.recipe_add_portions)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Recipe name subtitle
                uiState.addPortionsRecipe?.name?.let { name ->
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Portions amount
                OutlinedTextField(
                    value = uiState.addPortionsAmount,
                    onValueChange = onAmountChanged,
                    label = { Text(stringResource(R.string.food_portion_count)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category dropdown
                RecipeCategoryDropdown(
                    categories = uiState.foodCategories,
                    selectedCategoryId = uiState.addPortionsCategoryId,
                    onCategorySelected = onCategoryChanged
                )

                if (uiState.addPortionsValidationError != null) {
                    Text(
                        text = uiState.addPortionsValidationError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.food_add_entry))
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
private fun RecipeCategoryDropdown(
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
