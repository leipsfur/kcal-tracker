package de.leipsfur.kcal_track.ui.food

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.leipsfur.kcal_track.R
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import de.leipsfur.kcal_track.data.db.entity.FoodTemplate

import de.leipsfur.kcal_track.ui.shared.KcalTrackCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(
    viewModel: FoodViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTabIndex = if (uiState.showTemplatesTab) 1 else 0

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (!uiState.showTemplatesTab) {
                    SmallFloatingActionButton(
                        onClick = { viewModel.showManualEntryDialog() }
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.food_manual_entry))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FloatingActionButton(
                        onClick = { viewModel.showAddFromTemplateSheet() }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.food_add_from_template))
                    }
                } else {
                    SmallFloatingActionButton(
                        onClick = { viewModel.showCategoryManagement() }
                    ) {
                        Icon(Icons.Filled.Category, contentDescription = stringResource(R.string.food_manage_categories))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FloatingActionButton(
                        onClick = { viewModel.showCreateTemplateDialog() }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.food_add_template))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { if (uiState.showTemplatesTab) viewModel.toggleTemplatesTab() },
                    text = { Text(stringResource(R.string.food_tab_entries)) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { if (!uiState.showTemplatesTab) viewModel.toggleTemplatesTab() },
                    text = { Text(stringResource(R.string.food_tab_templates)) }
                )
            }

            if (!uiState.showTemplatesTab) {
                FoodEntryList(
                    entries = uiState.entries,
                    categories = uiState.categories,
                    onEdit = { viewModel.showEditEntryDialog(it) },
                    onDelete = { viewModel.showDeleteEntryConfirmation(it) }
                )
            } else {
                FoodTemplateList(
                    templates = uiState.templates,
                    categories = uiState.categories,
                    onEdit = { viewModel.showEditTemplateDialog(it) },
                    onDelete = { viewModel.showDeleteTemplateConfirmation(it) }
                )
            }
        }
    }

    // Dialogs
    if (uiState.showTemplateDialog) {
        FoodTemplateDialog(
            uiState = uiState,
            onNameChanged = viewModel::onTemplateNameChanged,
            onKcalChanged = viewModel::onTemplateKcalChanged,
            onProteinChanged = viewModel::onTemplateProteinChanged,
            onCarbsChanged = viewModel::onTemplateCarbsChanged,
            onFatChanged = viewModel::onTemplateFatChanged,
            onPortionSizeChanged = viewModel::onTemplatePortionSizeChanged,
            onPortionUnitChanged = viewModel::onTemplatePortionUnitChanged,
            onCategoryChanged = viewModel::onTemplateCategoryChanged,
            onSave = viewModel::saveTemplate,
            onDismiss = viewModel::dismissTemplateDialog
        )
    }

    if (uiState.showDeleteTemplateDialog) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.food_delete_template_title),
            text = stringResource(R.string.food_delete_template_text, uiState.deletingTemplate?.name ?: ""),
            onConfirm = viewModel::confirmDeleteTemplate,
            onDismiss = viewModel::dismissDeleteTemplateDialog
        )
    }

    if (uiState.showAddFromTemplateSheet) {
        AddFromTemplateSheet(
            uiState = uiState,
            onSearchQueryChanged = viewModel::onTemplateSearchQueryChanged,
            onTemplateSelected = viewModel::selectTemplateForEntry,
            onAmountChanged = viewModel::onTemplateAmountChanged,
            onConfirm = viewModel::confirmAddFromTemplate,
            onDismiss = viewModel::dismissAddFromTemplateSheet
        )
    }

    if (uiState.showManualEntryDialog) {
        FoodEntryDialog(
            uiState = uiState,
            onNameChanged = viewModel::onEntryNameChanged,
            onKcalChanged = viewModel::onEntryKcalChanged,
            onAmountChanged = viewModel::onEntryAmountChanged,
            onProteinChanged = viewModel::onEntryProteinChanged,
            onCarbsChanged = viewModel::onEntryCarbsChanged,
            onFatChanged = viewModel::onEntryFatChanged,
            onCategoryChanged = viewModel::onEntryCategoryChanged,
            onSave = viewModel::saveEntry,
            onDismiss = viewModel::dismissManualEntryDialog
        )
    }

    if (uiState.showDeleteEntryDialog) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.food_delete_entry_title),
            text = stringResource(R.string.food_delete_entry_text, uiState.deletingEntry?.name ?: ""),
            onConfirm = viewModel::confirmDeleteEntry,
            onDismiss = viewModel::dismissDeleteEntryDialog
        )
    }

    if (uiState.showCategoryManagement) {
        FoodCategoryManagementDialog(
            uiState = uiState,
            onAddCategory = viewModel::showAddCategoryDialog,
            onEditCategory = viewModel::showEditCategoryDialog,
            onDeleteCategory = viewModel::showDeleteCategoryConfirmation,
            onMoveCategoryUp = viewModel::moveCategoryUp,
            onMoveCategoryDown = viewModel::moveCategoryDown,
            onDismiss = viewModel::dismissCategoryManagement
        )
    }

    if (uiState.showAddCategoryDialog) {
        FoodCategoryDialog(
            uiState = uiState,
            onNameChanged = viewModel::onCategoryNameChanged,
            onSave = viewModel::saveCategory,
            onDismiss = viewModel::dismissCategoryDialog
        )
    }

    if (uiState.showDeleteCategoryDialog) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.food_delete_category_title),
            text = uiState.deleteCategoryError
                ?: stringResource(R.string.food_delete_category_text, uiState.deletingCategory?.name ?: ""),
            onConfirm = if (uiState.deleteCategoryError == null) viewModel::confirmDeleteCategory else null,
            onDismiss = viewModel::dismissDeleteCategoryDialog
        )
    }
}

@Composable
private fun FoodEntryList(
    entries: List<FoodEntry>,
    categories: List<FoodCategory>,
    onEdit: (FoodEntry) -> Unit,
    onDelete: (FoodEntry) -> Unit
) {
    if (entries.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.food_no_entries),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        val categoryMap = categories.associateBy { it.id }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(entries, key = { it.id }) { entry ->
                FoodEntryCard(
                    entry = entry,
                    categoryName = categoryMap[entry.categoryId]?.name ?: "",
                    onEdit = { onEdit(entry) },
                    onDelete = { onDelete(entry) }
                )
            }
        }
    }
}

@Composable
private fun FoodEntryCard(
    entry: FoodEntry,
    categoryName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    KcalTrackCard(
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${entry.kcal} kcal",
                style = MaterialTheme.typography.titleSmall
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.food_delete_entry),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun FoodTemplateList(
    templates: List<FoodTemplate>,
    categories: List<FoodCategory>,
    onEdit: (FoodTemplate) -> Unit,
    onDelete: (FoodTemplate) -> Unit
) {
    if (templates.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.food_no_templates),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        val categoryMap = categories.associateBy { it.id }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(templates, key = { it.id }) { template ->
                FoodTemplateCard(
                    template = template,
                    categoryName = categoryMap[template.categoryId]?.name ?: "",
                    onEdit = { onEdit(template) },
                    onDelete = { onDelete(template) }
                )
            }
        }
    }
}

@Composable
private fun FoodTemplateCard(
    template: FoodTemplate,
    categoryName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    KcalTrackCard(
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$categoryName · ${template.portionSize.formatAmount()} ${template.portionUnit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${template.kcal} kcal",
                style = MaterialTheme.typography.titleSmall
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.food_delete_template),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

internal fun Double.formatAmount(): String {
    return if (this == this.toLong().toDouble()) {
        this.toLong().toString()
    } else {
        this.toString()
    }
}
