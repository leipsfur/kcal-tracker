package de.leipsfur.kcal_track.ui.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.leipsfur.kcal_track.R
import de.leipsfur.kcal_track.data.db.entity.Recipe
import de.leipsfur.kcal_track.data.db.entity.RecipeIngredient
import de.leipsfur.kcal_track.ui.food.ConfirmDeleteDialog
import de.leipsfur.kcal_track.ui.shared.KcalTrackCard
import de.leipsfur.kcal_track.ui.shared.KcalTrackTopBar
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showRecipeDetail) {
        RecipeDetailContent(
            uiState = uiState,
            viewModel = viewModel,
            modifier = modifier
        )
    } else {
        RecipeListContent(
            uiState = uiState,
            viewModel = viewModel,
            modifier = modifier
        )
    }

    // Dialogs rendered outside Scaffold

    if (uiState.showCreateRecipeDialog) {
        CreateRecipeDialog(
            uiState = uiState,
            onNameChanged = viewModel::onNewRecipeNameChanged,
            onSave = viewModel::createRecipe,
            onDismiss = viewModel::dismissCreateRecipeDialog
        )
    }

    if (uiState.showDeleteRecipeDialog) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.recipe_delete_title),
            text = stringResource(R.string.recipe_delete_confirm, uiState.deletingRecipe?.name ?: ""),
            onConfirm = viewModel::confirmDeleteRecipe,
            onDismiss = viewModel::dismissDeleteRecipeDialog
        )
    }

    if (uiState.showDeleteIngredientDialog) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.recipe_ingredient_delete_title),
            text = stringResource(R.string.recipe_ingredient_delete_confirm, uiState.deletingIngredient?.name ?: ""),
            onConfirm = viewModel::confirmDeleteIngredient,
            onDismiss = viewModel::dismissDeleteIngredientDialog
        )
    }

    if (uiState.showFinishDialog) {
        FinishRecipeDialog(
            uiState = uiState,
            onPortionsCookedChanged = viewModel::onFinishPortionsCookedChanged,
            onPortionsEatenChanged = viewModel::onFinishPortionsEatenChanged,
            onCategoryChanged = viewModel::onFinishCategoryChanged,
            onConfirm = viewModel::confirmFinishRecipe,
            onDismiss = viewModel::dismissFinishDialog
        )
    }

    if (uiState.showAddPortionsDialog) {
        AddPortionsDialog(
            uiState = uiState,
            onAmountChanged = viewModel::onAddPortionsAmountChanged,
            onCategoryChanged = viewModel::onAddPortionsCategoryChanged,
            onConfirm = viewModel::confirmAddPortions,
            onDismiss = viewModel::dismissAddPortionsDialog
        )
    }

    if (uiState.showIngredientDialog) {
        IngredientDialog(
            uiState = uiState,
            onNameChanged = viewModel::onIngredientNameChanged,
            onKcalPer100Changed = viewModel::onIngredientKcalPer100Changed,
            onReferenceUnitChanged = viewModel::onIngredientReferenceUnitChanged,
            onAmountChanged = viewModel::onIngredientAmountChanged,
            onProteinChanged = viewModel::onIngredientProteinChanged,
            onCarbsChanged = viewModel::onIngredientCarbsChanged,
            onFatChanged = viewModel::onIngredientFatChanged,
            onSuggestionSelected = viewModel::selectIngredientSuggestion,
            onSave = viewModel::saveIngredient,
            onDismiss = viewModel::dismissIngredientDialog
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeListContent(
    uiState: RecipeUiState,
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            KcalTrackTopBar(title = stringResource(R.string.recipe_title))
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateRecipeDialog() }
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.recipe_create))
            }
        }
    ) { innerPadding ->
        if (uiState.recipes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.recipe_no_recipes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.recipes, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { viewModel.openRecipeDetail(recipe) },
                        onAddPortions = { viewModel.showAddPortionsDialog(recipe) },
                        onCopy = { viewModel.copyRecipe(recipe) },
                        onDelete = { viewModel.showDeleteRecipeConfirmation(recipe) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onAddPortions: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())

    KcalTrackCard(
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = recipe.createdDate.format(dateFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (recipe.status == "in_progress") {
                    Text(
                        text = stringResource(R.string.recipe_status_in_progress),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (recipe.totalPortions != null) {
                IconButton(onClick = onAddPortions) {
                    Icon(
                        Icons.Filled.Restaurant,
                        contentDescription = stringResource(R.string.recipe_add_portions)
                    )
                }
            }
            IconButton(onClick = onCopy) {
                Icon(
                    Icons.Filled.ContentCopy,
                    contentDescription = stringResource(R.string.recipe_copy)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.recipe_delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailContent(
    uiState: RecipeUiState,
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.currentRecipe?.name ?: "",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.closeRecipeDetail() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.settings_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showFinishDialog() }) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(R.string.recipe_finish)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddIngredientDialog() }
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.recipe_add_ingredient))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Summary card
            item {
                RecipeSummaryCard(uiState = uiState)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.currentIngredients.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.recipe_no_ingredients),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(uiState.currentIngredients, key = { it.id }) { ingredient ->
                    IngredientCard(
                        ingredient = ingredient,
                        onClick = { viewModel.showEditIngredientDialog(ingredient) },
                        onDelete = { viewModel.showDeleteIngredientConfirmation(ingredient) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeSummaryCard(uiState: RecipeUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.recipe_total_kcal, uiState.totalKcal),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${stringResource(R.string.food_protein_short)}: ${"%.1f".format(uiState.totalProtein)}g  |  " +
                        "${stringResource(R.string.food_carbs_short)}: ${"%.1f".format(uiState.totalCarbs)}g  |  " +
                        "${stringResource(R.string.food_fat_short)}: ${"%.1f".format(uiState.totalFat)}g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val totalPortions = uiState.currentRecipe?.totalPortions
            if (totalPortions != null && totalPortions > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                val kcalPerPortion = (uiState.totalKcal / totalPortions).toInt()
                Text(
                    text = stringResource(R.string.recipe_kcal_per_portion, kcalPerPortion),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun IngredientCard(
    ingredient: RecipeIngredient,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val kcal = (ingredient.kcalPer100 * ingredient.amount / 100).toInt()

    KcalTrackCard(
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${ingredient.amount.formatIngredientAmount()} ${ingredient.referenceUnit} \u00b7 $kcal kcal",
                    style = MaterialTheme.typography.bodySmall
                )
                val hasMacros = ingredient.protein != null || ingredient.carbs != null || ingredient.fat != null
                if (hasMacros) {
                    val proteinText = ingredient.protein?.let { "%.1f".format(it * ingredient.amount / 100) } ?: "-"
                    val carbsText = ingredient.carbs?.let { "%.1f".format(it * ingredient.amount / 100) } ?: "-"
                    val fatText = ingredient.fat?.let { "%.1f".format(it * ingredient.amount / 100) } ?: "-"
                    Text(
                        text = "P: ${proteinText}g | K: ${carbsText}g | F: ${fatText}g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.recipe_ingredient_delete_title),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CreateRecipeDialog(
    uiState: RecipeUiState,
    onNameChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.recipe_create)) },
        text = {
            Column {
                OutlinedTextField(
                    value = uiState.newRecipeName,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.recipe_name)) },
                    singleLine = true,
                    isError = uiState.createRecipeValidationError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.createRecipeValidationError != null) {
                    Text(
                        text = uiState.createRecipeValidationError,
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

private fun Double.formatIngredientAmount(): String {
    return if (this == this.toLong().toDouble()) {
        this.toLong().toString()
    } else {
        this.toString()
    }
}
