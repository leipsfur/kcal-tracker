package de.leipsfur.kcal_track.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.leipsfur.kcal_track.data.db.entity.ActivityCategory
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.db.entity.ActivityTemplate
import de.leipsfur.kcal_track.data.repository.ActivityRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ActivityUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val entries: List<ActivityEntry> = emptyList(),
    val templates: List<ActivityTemplate> = emptyList(),
    val categories: List<ActivityCategory> = emptyList(),
    val selectedTab: ActivityTab = ActivityTab.ENTRIES,
    val templateDialog: TemplateDialogState? = null,
    val entryDialog: EntryDialogState? = null,
    val templateSelectionSheet: Boolean = false,
    val deleteConfirmation: DeleteConfirmationState? = null,
    val categoryManagementOpen: Boolean = false,
    val categoryDialog: CategoryDialogState? = null,
    val deleteCategoryConfirmation: DeleteCategoryConfirmationState? = null,
    val snackbarMessage: String? = null
)

enum class ActivityTab { ENTRIES, TEMPLATES }

data class TemplateDialogState(
    val editingTemplate: ActivityTemplate? = null,
    val name: String = "",
    val kcal: String = "",
    val categoryId: Long? = null,
    val nameError: String? = null,
    val kcalError: String? = null,
    val categoryError: String? = null
)

data class EntryDialogState(
    val editingEntry: ActivityEntry? = null,
    val name: String = "",
    val kcal: String = "",
    val categoryId: Long? = null,
    val nameError: String? = null,
    val kcalError: String? = null,
    val categoryError: String? = null
)

sealed class DeleteConfirmationState {
    data class Template(val template: ActivityTemplate) : DeleteConfirmationState()
    data class Entry(val entry: ActivityEntry) : DeleteConfirmationState()
}

data class CategoryDialogState(
    val editingCategory: ActivityCategory? = null,
    val name: String = "",
    val nameError: String? = null
)

data class DeleteCategoryConfirmationState(
    val category: ActivityCategory,
    val usageCount: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityViewModel(
    private val activityRepository: ActivityRepository,
    private val dateFlow: StateFlow<LocalDate>,
    private val onDateChangedCallback: (LocalDate) -> Unit,
    private val onDataChanged: () -> Unit
) : ViewModel() {

    private val _uiExtras = MutableStateFlow(UiExtras())

    private data class UiExtras(
        val selectedTab: ActivityTab = ActivityTab.ENTRIES,
        val templateDialog: TemplateDialogState? = null,
        val entryDialog: EntryDialogState? = null,
        val templateSelectionSheet: Boolean = false,
        val deleteConfirmation: DeleteConfirmationState? = null,
        val categoryManagementOpen: Boolean = false,
        val categoryDialog: CategoryDialogState? = null,
        val deleteCategoryConfirmation: DeleteCategoryConfirmationState? = null,
        val snackbarMessage: String? = null
    )

    val uiState: StateFlow<ActivityUiState> = combine(
        dateFlow.flatMapLatest { date -> activityRepository.getEntriesByDate(date) },
        activityRepository.getAllTemplates(),
        activityRepository.getAllCategories(),
        dateFlow,
        _uiExtras
    ) { entries, templates, categories, selectedDate, extras ->
        ActivityUiState(
            selectedDate = selectedDate,
            entries = entries,
            templates = templates,
            categories = categories,
            selectedTab = extras.selectedTab,
            templateDialog = extras.templateDialog,
            entryDialog = extras.entryDialog,
            templateSelectionSheet = extras.templateSelectionSheet,
            deleteConfirmation = extras.deleteConfirmation,
            categoryManagementOpen = extras.categoryManagementOpen,
            categoryDialog = extras.categoryDialog,
            deleteCategoryConfirmation = extras.deleteCategoryConfirmation,
            snackbarMessage = extras.snackbarMessage
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActivityUiState())

    fun onDateChanged(date: LocalDate) {
        onDateChangedCallback(date)
    }

    // Tab selection
    fun selectTab(tab: ActivityTab) {
        _uiExtras.update { it.copy(selectedTab = tab) }
    }

    // Template dialog
    fun showCreateTemplateDialog() {
        _uiExtras.update { it.copy(templateDialog = TemplateDialogState()) }
    }

    fun showEditTemplateDialog(template: ActivityTemplate) {
        _uiExtras.update {
            it.copy(
                templateDialog = TemplateDialogState(
                    editingTemplate = template,
                    name = template.name,
                    kcal = template.kcal.toString(),
                    categoryId = template.categoryId
                )
            )
        }
    }

    fun showCreateTemplateFromEntry(entry: ActivityEntry) {
        _uiExtras.update {
            it.copy(
                templateDialog = TemplateDialogState(
                    editingTemplate = null,
                    name = entry.name,
                    kcal = entry.kcal.toString(),
                    categoryId = entry.categoryId
                )
            )
        }
    }

    fun dismissTemplateDialog() {
        _uiExtras.update { it.copy(templateDialog = null) }
    }

    fun updateTemplateName(name: String) {
        _uiExtras.update { extras ->
            extras.copy(
                templateDialog = extras.templateDialog?.copy(name = name, nameError = null)
            )
        }
    }

    fun updateTemplateKcal(kcal: String) {
        _uiExtras.update { extras ->
            extras.copy(
                templateDialog = extras.templateDialog?.copy(kcal = kcal, kcalError = null)
            )
        }
    }

    fun updateTemplateCategoryId(categoryId: Long) {
        _uiExtras.update { extras ->
            extras.copy(
                templateDialog = extras.templateDialog?.copy(
                    categoryId = categoryId,
                    categoryError = null
                )
            )
        }
    }

    fun saveTemplate() {
        val dialog = _uiExtras.value.templateDialog ?: return
        val name = dialog.name.trim()
        val kcalValue = dialog.kcal.trim().toIntOrNull()
        val categoryId = dialog.categoryId

        var hasError = false
        var nameError: String? = null
        var kcalError: String? = null
        var categoryError: String? = null

        if (name.isEmpty()) {
            nameError = "Name darf nicht leer sein"
            hasError = true
        }
        if (kcalValue == null || kcalValue <= 0) {
            kcalError = "kcal muss größer als 0 sein"
            hasError = true
        }
        if (categoryId == null) {
            categoryError = "Bitte eine Kategorie wählen"
            hasError = true
        }

        if (hasError) {
            _uiExtras.update { extras ->
                extras.copy(
                    templateDialog = extras.templateDialog?.copy(
                        nameError = nameError,
                        kcalError = kcalError,
                        categoryError = categoryError
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            val template = ActivityTemplate(
                id = dialog.editingTemplate?.id ?: 0,
                name = name,
                kcal = kcalValue!!,
                categoryId = categoryId!!
            )
            if (dialog.editingTemplate != null) {
                activityRepository.updateTemplate(template)
            } else {
                activityRepository.insertTemplate(template)
            }
            onDataChanged()
            _uiExtras.update { it.copy(templateDialog = null) }
        }
    }

    // Delete confirmation
    fun showDeleteTemplateConfirmation(template: ActivityTemplate) {
        _uiExtras.update {
            it.copy(deleteConfirmation = DeleteConfirmationState.Template(template))
        }
    }

    fun showDeleteEntryConfirmation(entry: ActivityEntry) {
        _uiExtras.update {
            it.copy(deleteConfirmation = DeleteConfirmationState.Entry(entry))
        }
    }

    fun dismissDeleteConfirmation() {
        _uiExtras.update { it.copy(deleteConfirmation = null) }
    }

    fun confirmDelete() {
        val confirmation = _uiExtras.value.deleteConfirmation ?: return
        viewModelScope.launch {
            when (confirmation) {
                is DeleteConfirmationState.Template -> {
                    activityRepository.deleteTemplate(confirmation.template)
                }
                is DeleteConfirmationState.Entry -> {
                    activityRepository.deleteEntry(confirmation.entry)
                }
            }
            onDataChanged()
            _uiExtras.update { it.copy(deleteConfirmation = null) }
        }
    }

    // Template selection bottom sheet
    fun showTemplateSelectionSheet() {
        _uiExtras.update { it.copy(templateSelectionSheet = true) }
    }

    fun dismissTemplateSelectionSheet() {
        _uiExtras.update { it.copy(templateSelectionSheet = false) }
    }

    fun addEntryFromTemplate(template: ActivityTemplate, kcalOverride: Int? = null) {
        viewModelScope.launch {
            val entry = ActivityEntry(
                date = dateFlow.value,
                templateId = template.id,
                name = template.name,
                kcal = kcalOverride ?: template.kcal,
                categoryId = template.categoryId
            )
            activityRepository.insertEntry(entry)
            onDataChanged()
            _uiExtras.update { it.copy(templateSelectionSheet = false) }
        }
    }

    // Manual entry dialog
    fun showCreateEntryDialog() {
        _uiExtras.update { it.copy(entryDialog = EntryDialogState()) }
    }

    fun showEditEntryDialog(entry: ActivityEntry) {
        _uiExtras.update {
            it.copy(
                entryDialog = EntryDialogState(
                    editingEntry = entry,
                    name = entry.name,
                    kcal = entry.kcal.toString(),
                    categoryId = entry.categoryId
                )
            )
        }
    }

    fun dismissEntryDialog() {
        _uiExtras.update { it.copy(entryDialog = null) }
    }

    fun updateEntryName(name: String) {
        _uiExtras.update { extras ->
            extras.copy(
                entryDialog = extras.entryDialog?.copy(name = name, nameError = null)
            )
        }
    }

    fun updateEntryKcal(kcal: String) {
        _uiExtras.update { extras ->
            extras.copy(
                entryDialog = extras.entryDialog?.copy(kcal = kcal, kcalError = null)
            )
        }
    }

    fun updateEntryCategoryId(categoryId: Long) {
        _uiExtras.update { extras ->
            extras.copy(
                entryDialog = extras.entryDialog?.copy(
                    categoryId = categoryId,
                    categoryError = null
                )
            )
        }
    }

    fun saveEntry() {
        val dialog = _uiExtras.value.entryDialog ?: return
        val name = dialog.name.trim()
        val kcalValue = dialog.kcal.trim().toIntOrNull()
        val categoryId = dialog.categoryId

        var hasError = false
        var nameError: String? = null
        var kcalError: String? = null
        var categoryError: String? = null

        if (name.isEmpty()) {
            nameError = "Name darf nicht leer sein"
            hasError = true
        }
        if (kcalValue == null || kcalValue <= 0) {
            kcalError = "kcal muss größer als 0 sein"
            hasError = true
        }
        if (categoryId == null) {
            categoryError = "Bitte eine Kategorie wählen"
            hasError = true
        }

        if (hasError) {
            _uiExtras.update { extras ->
                extras.copy(
                    entryDialog = extras.entryDialog?.copy(
                        nameError = nameError,
                        kcalError = kcalError,
                        categoryError = categoryError
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            val entry = ActivityEntry(
                id = dialog.editingEntry?.id ?: 0,
                date = dialog.editingEntry?.date ?: dateFlow.value,
                templateId = dialog.editingEntry?.templateId,
                name = name,
                kcal = kcalValue!!,
                categoryId = categoryId!!
            )
            if (dialog.editingEntry != null) {
                activityRepository.updateEntry(entry)
            } else {
                activityRepository.insertEntry(entry)
            }
            onDataChanged()
            _uiExtras.update { it.copy(entryDialog = null) }
        }
    }

    // Category management
    fun openCategoryManagement() {
        _uiExtras.update { it.copy(categoryManagementOpen = true) }
    }

    fun closeCategoryManagement() {
        _uiExtras.update { it.copy(categoryManagementOpen = false) }
    }

    fun showCreateCategoryDialog() {
        _uiExtras.update { it.copy(categoryDialog = CategoryDialogState()) }
    }

    fun showRenameCategoryDialog(category: ActivityCategory) {
        _uiExtras.update {
            it.copy(
                categoryDialog = CategoryDialogState(
                    editingCategory = category,
                    name = category.name
                )
            )
        }
    }

    fun dismissCategoryDialog() {
        _uiExtras.update { it.copy(categoryDialog = null) }
    }

    fun updateCategoryName(name: String) {
        _uiExtras.update { extras ->
            extras.copy(
                categoryDialog = extras.categoryDialog?.copy(name = name, nameError = null)
            )
        }
    }

    fun saveCategory() {
        val dialog = _uiExtras.value.categoryDialog ?: return
        val name = dialog.name.trim()

        if (name.isEmpty()) {
            _uiExtras.update { extras ->
                extras.copy(
                    categoryDialog = extras.categoryDialog?.copy(
                        nameError = "Name darf nicht leer sein"
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            if (dialog.editingCategory != null) {
                activityRepository.updateCategory(
                    dialog.editingCategory.copy(name = name)
                )
            } else {
                val maxSortOrder = activityRepository.getMaxCategorySortOrder() ?: 0
                activityRepository.insertCategory(
                    ActivityCategory(name = name, sortOrder = maxSortOrder + 1)
                )
            }
            onDataChanged()
            _uiExtras.update { it.copy(categoryDialog = null) }
        }
    }

    fun showDeleteCategoryConfirmation(category: ActivityCategory) {
        viewModelScope.launch {
            val usageCount = activityRepository.getCategoryUsageCount(category.id)
            _uiExtras.update {
                it.copy(
                    deleteCategoryConfirmation = DeleteCategoryConfirmationState(
                        category = category,
                        usageCount = usageCount
                    )
                )
            }
        }
    }

    fun dismissDeleteCategoryConfirmation() {
        _uiExtras.update { it.copy(deleteCategoryConfirmation = null) }
    }

    fun confirmDeleteCategory() {
        val confirmation = _uiExtras.value.deleteCategoryConfirmation ?: return
        if (confirmation.usageCount > 0) {
            _uiExtras.update {
                it.copy(
                    deleteCategoryConfirmation = null,
                    snackbarMessage = "Kategorie wird noch verwendet und kann nicht gelöscht werden"
                )
            }
            return
        }
        viewModelScope.launch {
            activityRepository.deleteCategory(confirmation.category)
            onDataChanged()
            _uiExtras.update { it.copy(deleteCategoryConfirmation = null) }
        }
    }

    fun moveCategoryUp(category: ActivityCategory) {
        val categories = uiState.value.categories
        val index = categories.indexOfFirst { it.id == category.id }
        if (index <= 0) return
        val other = categories[index - 1]
        viewModelScope.launch {
            activityRepository.updateCategory(category.copy(sortOrder = other.sortOrder))
            activityRepository.updateCategory(other.copy(sortOrder = category.sortOrder))
            onDataChanged()
        }
    }

    fun moveCategoryDown(category: ActivityCategory) {
        val categories = uiState.value.categories
        val index = categories.indexOfFirst { it.id == category.id }
        if (index < 0 || index >= categories.size - 1) return
        val other = categories[index + 1]
        viewModelScope.launch {
            activityRepository.updateCategory(category.copy(sortOrder = other.sortOrder))
            activityRepository.updateCategory(other.copy(sortOrder = category.sortOrder))
            onDataChanged()
        }
    }

    fun dismissSnackbar() {
        _uiExtras.update { it.copy(snackbarMessage = null) }
    }

    class Factory(
        private val activityRepository: ActivityRepository,
        private val dateFlow: StateFlow<LocalDate>,
        private val onDateChangedCallback: (LocalDate) -> Unit,
        private val onDataChanged: () -> Unit
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ActivityViewModel(activityRepository, dateFlow, onDateChangedCallback, onDataChanged) as T
        }
    }
}
