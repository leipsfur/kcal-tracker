package de.leipsfur.kcal_track.ui.activity

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.leipsfur.kcal_track.R
import de.leipsfur.kcal_track.data.db.entity.ActivityCategory
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.db.entity.ActivityTemplate

import de.leipsfur.kcal_track.ui.shared.KcalTrackCard
import de.leipsfur.kcal_track.ui.shared.KcalTrackHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    // Category management screen
    if (uiState.categoryManagementOpen) {
        CategoryManagementScreen(
            categories = uiState.categories,
            categoryDialog = uiState.categoryDialog,
            deleteCategoryConfirmation = uiState.deleteCategoryConfirmation,
            onBack = { viewModel.closeCategoryManagement() },
            onAdd = { viewModel.showCreateCategoryDialog() },
            onRename = { viewModel.showRenameCategoryDialog(it) },
            onDelete = { viewModel.showDeleteCategoryConfirmation(it) },
            onMoveUp = { viewModel.moveCategoryUp(it) },
            onMoveDown = { viewModel.moveCategoryDown(it) },
            onDismissCategoryDialog = { viewModel.dismissCategoryDialog() },
            onUpdateCategoryName = { viewModel.updateCategoryName(it) },
            onSaveCategory = { viewModel.saveCategory() },
            onDismissDeleteConfirmation = { viewModel.dismissDeleteCategoryConfirmation() },
            onConfirmDelete = { viewModel.confirmDeleteCategory() }
        )
        return
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            when (uiState.selectedTab) {
                ActivityTab.ENTRIES -> {
                    Column(horizontalAlignment = Alignment.End) {
                        FloatingActionButton(
                            onClick = { viewModel.showTemplateSelectionSheet() },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = stringResource(R.string.activity_from_template)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        FloatingActionButton(
                            onClick = { viewModel.showCreateEntryDialog() }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.activity_add_manual)
                            )
                        }
                    }
                }
                ActivityTab.TEMPLATES -> {
                    FloatingActionButton(
                        onClick = { viewModel.showCreateTemplateDialog() }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.activity_template_create)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = stringResource(R.string.nav_activity),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            TabRow(
                selectedTabIndex = if (uiState.selectedTab == ActivityTab.ENTRIES) 0 else 1
            ) {
                Tab(
                    selected = uiState.selectedTab == ActivityTab.ENTRIES,
                    onClick = { viewModel.selectTab(ActivityTab.ENTRIES) },
                    text = { Text(stringResource(R.string.activity_tab_entries)) }
                )
                Tab(
                    selected = uiState.selectedTab == ActivityTab.TEMPLATES,
                    onClick = { viewModel.selectTab(ActivityTab.TEMPLATES) },
                    text = { Text(stringResource(R.string.activity_tab_templates)) }
                )
            }

            when (uiState.selectedTab) {
                ActivityTab.ENTRIES -> {
                    EntryList(
                        entries = uiState.entries,
                        categories = uiState.categories,
                        onEdit = { viewModel.showEditEntryDialog(it) },
                        onDelete = { viewModel.showDeleteEntryConfirmation(it) }
                    )
                }
                ActivityTab.TEMPLATES -> {
                    TemplateList(
                        templates = uiState.templates,
                        categories = uiState.categories,
                        onEdit = { viewModel.showEditTemplateDialog(it) },
                        onDelete = { viewModel.showDeleteTemplateConfirmation(it) },
                        onManageCategories = { viewModel.openCategoryManagement() }
                    )
                }
            }
        }
    }

    // Template dialog
    uiState.templateDialog?.let { dialog ->
        TemplateDialog(
            state = dialog,
            categories = uiState.categories,
            onDismiss = { viewModel.dismissTemplateDialog() },
            onNameChange = { viewModel.updateTemplateName(it) },
            onKcalChange = { viewModel.updateTemplateKcal(it) },
            onCategoryChange = { viewModel.updateTemplateCategoryId(it) },
            onSave = { viewModel.saveTemplate() }
        )
    }

    // Entry dialog
    uiState.entryDialog?.let { dialog ->
        EntryDialog(
            state = dialog,
            categories = uiState.categories,
            onDismiss = { viewModel.dismissEntryDialog() },
            onNameChange = { viewModel.updateEntryName(it) },
            onKcalChange = { viewModel.updateEntryKcal(it) },
            onCategoryChange = { viewModel.updateEntryCategoryId(it) },
            onSave = { viewModel.saveEntry() }
        )
    }

    // Delete confirmation
    uiState.deleteConfirmation?.let { confirmation ->
        DeleteConfirmationDialog(
            state = confirmation,
            onDismiss = { viewModel.dismissDeleteConfirmation() },
            onConfirm = { viewModel.confirmDelete() }
        )
    }

    // Template selection bottom sheet
    if (uiState.templateSelectionSheet) {
        TemplateSelectionBottomSheet(
            templates = uiState.templates,
            categories = uiState.categories,
            onDismiss = { viewModel.dismissTemplateSelectionSheet() },
            onSelect = { template, kcalOverride ->
                viewModel.addEntryFromTemplate(template, kcalOverride)
            }
        )
    }
}

@Composable
private fun EntryList(
    entries: List<ActivityEntry>,
    categories: List<ActivityCategory>,
    onEdit: (ActivityEntry) -> Unit,
    onDelete: (ActivityEntry) -> Unit
) {
    if (entries.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.activity_no_entries),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        val categoryMap = categories.associateBy { it.id }
        val groupedEntries = entries.groupBy { it.categoryId }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groupedEntries.forEach { (categoryId, categoryEntries) ->
                val categoryName = categoryMap[categoryId]?.name
                val categoryTotal = categoryEntries.sumOf { it.kcal }

                item(key = "header_$categoryId") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        KcalTrackHeader(text = categoryName ?: stringResource(R.string.activity_unknown_category))
                        KcalTrackHeader(text = stringResource(R.string.activity_kcal_value, categoryTotal))
                    }
                }

                items(categoryEntries, key = { it.id }) { entry ->
                    EntryCard(entry = entry, onEdit = onEdit, onDelete = onDelete)
                }
            }

            item(key = "total") {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.activity_total),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.activity_kcal_value, entries.sumOf { it.kcal }),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun EntryCard(
    entry: ActivityEntry,
    onEdit: (ActivityEntry) -> Unit,
    onDelete: (ActivityEntry) -> Unit
) {
    KcalTrackCard {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.activity_kcal_value, entry.kcal),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = { onEdit(entry) }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.activity_edit)
                    )
                }
                IconButton(onClick = { onDelete(entry) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.activity_delete)
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateList(
    templates: List<ActivityTemplate>,
    categories: List<ActivityCategory>,
    onEdit: (ActivityTemplate) -> Unit,
    onDelete: (ActivityTemplate) -> Unit,
    onManageCategories: () -> Unit
) {
    val categoryMap = categories.associateBy { it.id }
    val groupedTemplates = templates.groupBy { it.categoryId }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "manage_categories") {
            TextButton(
                onClick = onManageCategories,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(stringResource(R.string.activity_manage_categories))
            }
        }

        if (templates.isEmpty()) {
            item(key = "empty") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.activity_no_templates),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            groupedTemplates.forEach { (categoryId, categoryTemplates) ->
                val categoryName = categoryMap[categoryId]?.name

                item(key = "template_header_$categoryId") {
                    KcalTrackHeader(text = categoryName ?: stringResource(R.string.activity_unknown_category))
                }

                items(categoryTemplates, key = { "template_${it.id}" }) { template ->
                    TemplateCard(
                        template = template,
                        onEdit = onEdit,
                        onDelete = onDelete
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: ActivityTemplate,
    onEdit: (ActivityTemplate) -> Unit,
    onDelete: (ActivityTemplate) -> Unit
) {
    KcalTrackCard {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.activity_kcal_value, template.kcal),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = { onEdit(template) }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.activity_edit)
                    )
                }
                IconButton(onClick = { onDelete(template) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.activity_delete)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplateDialog(
    state: TemplateDialogState,
    categories: List<ActivityCategory>,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onKcalChange: (String) -> Unit,
    onCategoryChange: (Long) -> Unit,
    onSave: () -> Unit
) {
    val isEditing = state.editingTemplate != null
    val title = if (isEditing) {
        stringResource(R.string.activity_template_edit)
    } else {
        stringResource(R.string.activity_template_create)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.activity_name)) },
                    isError = state.nameError != null,
                    supportingText = state.nameError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.kcal,
                    onValueChange = onKcalChange,
                    label = { Text(stringResource(R.string.activity_kcal_label)) },
                    isError = state.kcalError != null,
                    supportingText = state.kcalError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                CategoryDropdown(
                    categories = categories,
                    selectedCategoryId = state.categoryId,
                    onCategorySelected = onCategoryChange,
                    isError = state.categoryError != null,
                    errorText = state.categoryError
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(stringResource(R.string.activity_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.activity_cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryDialog(
    state: EntryDialogState,
    categories: List<ActivityCategory>,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onKcalChange: (String) -> Unit,
    onCategoryChange: (Long) -> Unit,
    onSave: () -> Unit
) {
    val isEditing = state.editingEntry != null
    val title = if (isEditing) {
        stringResource(R.string.activity_entry_edit)
    } else {
        stringResource(R.string.activity_entry_create)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.activity_name)) },
                    isError = state.nameError != null,
                    supportingText = state.nameError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.kcal,
                    onValueChange = onKcalChange,
                    label = { Text(stringResource(R.string.activity_kcal_label)) },
                    isError = state.kcalError != null,
                    supportingText = state.kcalError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                CategoryDropdown(
                    categories = categories,
                    selectedCategoryId = state.categoryId,
                    onCategorySelected = onCategoryChange,
                    isError = state.categoryError != null,
                    errorText = state.categoryError
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(stringResource(R.string.activity_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.activity_cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    categories: List<ActivityCategory>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit,
    isError: Boolean,
    errorText: String?
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
            label = { Text(stringResource(R.string.activity_category)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            supportingText = errorText?.let { { Text(it) } },
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

@Composable
private fun DeleteConfirmationDialog(
    state: DeleteConfirmationState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val message = when (state) {
        is DeleteConfirmationState.Template -> {
            stringResource(R.string.activity_delete_template_confirm, state.template.name)
        }
        is DeleteConfirmationState.Entry -> {
            stringResource(R.string.activity_delete_entry_confirm, state.entry.name)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.activity_delete_title)) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    stringResource(R.string.activity_delete),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.activity_cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplateSelectionBottomSheet(
    templates: List<ActivityTemplate>,
    categories: List<ActivityCategory>,
    onDismiss: () -> Unit,
    onSelect: (ActivityTemplate, Int?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val categoryMap = categories.associateBy { it.id }
    val groupedTemplates = templates.groupBy { it.categoryId }
    var selectedTemplate by remember { mutableStateOf<ActivityTemplate?>(null) }
    var kcalOverride by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.activity_select_template),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (selectedTemplate != null) {
                val template = selectedTemplate!!
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = kcalOverride.ifEmpty { template.kcal.toString() },
                    onValueChange = { kcalOverride = it },
                    label = { Text(stringResource(R.string.activity_kcal_label)) },
                    suffix = { Text(stringResource(R.string.settings_bmr_unit)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        selectedTemplate = null
                        kcalOverride = ""
                    }) {
                        Text(stringResource(R.string.activity_back))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        val override = kcalOverride.toIntOrNull()
                        onSelect(
                            template,
                            if (override != null && override != template.kcal) override else null
                        )
                    }) {
                        Text(stringResource(R.string.activity_add))
                    }
                }
            } else if (templates.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.activity_no_templates),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    groupedTemplates.forEach { (categoryId, categoryTemplates) ->
                        val categoryName = categoryMap[categoryId]?.name ?: ""

                        item(key = "sheet_header_$categoryId") {
                            KcalTrackHeader(text = categoryName)
                        }

                        items(categoryTemplates, key = { "sheet_${it.id}" }) { template ->
                            KcalTrackCard(
                                onClick = {
                                    selectedTemplate = template
                                    kcalOverride = ""
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = template.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = stringResource(R.string.activity_kcal_value, template.kcal),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryManagementScreen(
    categories: List<ActivityCategory>,
    categoryDialog: CategoryDialogState?,
    deleteCategoryConfirmation: DeleteCategoryConfirmationState?,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onRename: (ActivityCategory) -> Unit,
    onDelete: (ActivityCategory) -> Unit,
    onMoveUp: (ActivityCategory) -> Unit,
    onMoveDown: (ActivityCategory) -> Unit,
    onDismissCategoryDialog: () -> Unit,
    onUpdateCategoryName: (String) -> Unit,
    onSaveCategory: () -> Unit,
    onDismissDeleteConfirmation: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.activity_back))
            }
            Text(
                text = stringResource(R.string.activity_categories_title),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = onAdd) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.activity_category_add)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories, key = { it.id }) { category ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        )
                        Row {
                            IconButton(onClick = { onMoveUp(category) }) {
                                Icon(
                                    Icons.Default.ArrowDropUp,
                                    contentDescription = stringResource(R.string.activity_category_move_up)
                                )
                            }
                            IconButton(onClick = { onMoveDown(category) }) {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = stringResource(R.string.activity_category_move_down)
                                )
                            }
                            IconButton(onClick = { onRename(category) }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.activity_category_rename)
                                )
                            }
                            IconButton(onClick = { onDelete(category) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.activity_delete)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Category add/rename dialog
    categoryDialog?.let { dialog ->
        val isEditing = dialog.editingCategory != null
        AlertDialog(
            onDismissRequest = onDismissCategoryDialog,
            title = {
                Text(
                    if (isEditing) stringResource(R.string.activity_category_rename)
                    else stringResource(R.string.activity_category_add)
                )
            },
            text = {
                OutlinedTextField(
                    value = dialog.name,
                    onValueChange = onUpdateCategoryName,
                    label = { Text(stringResource(R.string.activity_name)) },
                    isError = dialog.nameError != null,
                    supportingText = dialog.nameError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = onSaveCategory) {
                    Text(stringResource(R.string.activity_save))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissCategoryDialog) {
                    Text(stringResource(R.string.activity_cancel))
                }
            }
        )
    }

    // Category delete confirmation
    deleteCategoryConfirmation?.let { confirmation ->
        AlertDialog(
            onDismissRequest = onDismissDeleteConfirmation,
            title = { Text(stringResource(R.string.activity_delete_title)) },
            text = {
                if (confirmation.usageCount > 0) {
                    Text(
                        stringResource(
                            R.string.activity_category_in_use,
                            confirmation.category.name,
                            confirmation.usageCount
                        )
                    )
                } else {
                    Text(
                        stringResource(
                            R.string.activity_delete_category_confirm,
                            confirmation.category.name
                        )
                    )
                }
            },
            confirmButton = {
                if (confirmation.usageCount == 0) {
                    TextButton(onClick = onConfirmDelete) {
                        Text(
                            stringResource(R.string.activity_delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteConfirmation) {
                    Text(
                        if (confirmation.usageCount > 0) stringResource(R.string.activity_ok)
                        else stringResource(R.string.activity_cancel)
                    )
                }
            }
        )
    }
}
