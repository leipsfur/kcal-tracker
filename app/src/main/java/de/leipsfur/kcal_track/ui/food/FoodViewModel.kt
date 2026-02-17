package de.leipsfur.kcal_track.ui.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import de.leipsfur.kcal_track.data.db.entity.FoodTemplate
import de.leipsfur.kcal_track.data.repository.FoodRepository
import de.leipsfur.kcal_track.domain.model.PortionUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class FoodUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val entries: List<FoodEntry> = emptyList(),
    val templates: List<FoodTemplate> = emptyList(),
    val categories: List<FoodCategory> = emptyList(),
    val showTemplatesTab: Boolean = false,
    // Template dialog
    val showTemplateDialog: Boolean = false,
    val editingTemplate: FoodTemplate? = null,
    val templateName: String = "",
    val templateKcal: String = "",
    val templateProtein: String = "",
    val templateCarbs: String = "",
    val templateFat: String = "",
    val templatePortionSize: String = "",
    val templatePortionUnit: String = PortionUnit.GRAM,
    val templateCategoryId: Long? = null,
    val templateValidationError: String? = null,
    // Delete template confirmation
    val showDeleteTemplateDialog: Boolean = false,
    val deletingTemplate: FoodTemplate? = null,
    // Add from template (bottom sheet)
    val showAddFromTemplateSheet: Boolean = false,
    val templateSearchQuery: String = "",
    val selectedTemplate: FoodTemplate? = null,
    val templateAmount: String = "1",
    // Manual entry dialog
    val showManualEntryDialog: Boolean = false,
    val editingEntry: FoodEntry? = null,
    val entryName: String = "",
    val entryKcal: String = "",
    val entryAmount: String = "1",
    val entryProtein: String = "",
    val entryCarbs: String = "",
    val entryFat: String = "",
    val entryCategoryId: Long? = null,
    val entryValidationError: String? = null,
    // Delete entry confirmation
    val showDeleteEntryDialog: Boolean = false,
    val deletingEntry: FoodEntry? = null,
    // Category management
    val showCategoryManagement: Boolean = false,
    val showAddCategoryDialog: Boolean = false,
    val editingCategory: FoodCategory? = null,
    val categoryName: String = "",
    val categoryValidationError: String? = null,
    val showDeleteCategoryDialog: Boolean = false,
    val deletingCategory: FoodCategory? = null,
    val deleteCategoryError: String? = null
)

class FoodViewModel(
    private val foodRepository: FoodRepository,
    private val dateFlow: StateFlow<LocalDate>,
    private val onDateChangedCallback: (LocalDate) -> Unit
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodUiState())
    val uiState: StateFlow<FoodUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Observe dateFlow and update UI state
            launch {
                dateFlow.collect { date ->
                    _uiState.update { it.copy(selectedDate = date) }
                }
            }

            // Reactive data loading
            val entriesFlow = dateFlow.flatMapLatest { date ->
                foodRepository.getEntriesByDate(date)
            }

            combine(
                entriesFlow,
                foodRepository.getAllTemplates(),
                foodRepository.getAllCategories()
            ) { entries, templates, categories ->
                Triple(entries, templates, categories)
            }.collect { (entries, templates, categories) ->
                _uiState.update {
                    it.copy(
                        entries = entries,
                        templates = templates,
                        categories = categories,
                        templateCategoryId = it.templateCategoryId ?: categories.firstOrNull()?.id,
                        entryCategoryId = it.entryCategoryId ?: categories.firstOrNull()?.id
                    )
                }
            }
        }
    }

    fun onDateChanged(date: LocalDate) {
        onDateChangedCallback(date)
    }

    fun toggleTemplatesTab() {
        _uiState.update { it.copy(showTemplatesTab = !it.showTemplatesTab) }
    }

    // --- Template CRUD ---

    fun showCreateTemplateDialog() {
        _uiState.update {
            it.copy(
                showTemplateDialog = true,
                editingTemplate = null,
                templateName = "",
                templateKcal = "",
                templateProtein = "",
                templateCarbs = "",
                templateFat = "",
                templatePortionSize = "",
                templatePortionUnit = PortionUnit.GRAM,
                templateCategoryId = it.categories.firstOrNull()?.id,
                templateValidationError = null
            )
        }
    }

    fun showEditTemplateDialog(template: FoodTemplate) {
        _uiState.update {
            it.copy(
                showTemplateDialog = true,
                editingTemplate = template,
                templateName = template.name,
                templateKcal = template.kcal.toString(),
                templateProtein = template.protein?.toString() ?: "",
                templateCarbs = template.carbs?.toString() ?: "",
                templateFat = template.fat?.toString() ?: "",
                templatePortionSize = template.portionSize.toString(),
                templatePortionUnit = template.portionUnit,
                templateCategoryId = template.categoryId,
                templateValidationError = null
            )
        }
    }

    fun dismissTemplateDialog() {
        _uiState.update { it.copy(showTemplateDialog = false) }
    }

    fun onTemplateNameChanged(value: String) {
        _uiState.update { it.copy(templateName = value, templateValidationError = null) }
    }

    fun onTemplateKcalChanged(value: String) {
        _uiState.update { it.copy(templateKcal = value, templateValidationError = null) }
    }

    fun onTemplateProteinChanged(value: String) {
        _uiState.update { it.copy(templateProtein = value) }
    }

    fun onTemplateCarbsChanged(value: String) {
        _uiState.update { it.copy(templateCarbs = value) }
    }

    fun onTemplateFatChanged(value: String) {
        _uiState.update { it.copy(templateFat = value) }
    }

    fun onTemplatePortionSizeChanged(value: String) {
        _uiState.update { it.copy(templatePortionSize = value, templateValidationError = null) }
    }

    fun onTemplatePortionUnitChanged(value: String) {
        _uiState.update { it.copy(templatePortionUnit = value) }
    }

    fun onTemplateCategoryChanged(categoryId: Long) {
        _uiState.update { it.copy(templateCategoryId = categoryId) }
    }

    fun saveTemplate() {
        val state = _uiState.value
        val name = state.templateName.trim()
        val kcal = state.templateKcal.trim().toIntOrNull()
        val portionSize = state.templatePortionSize.trim().toDoubleOrNull()

        when {
            name.isEmpty() -> {
                _uiState.update { it.copy(templateValidationError = "Name darf nicht leer sein") }
                return
            }
            kcal == null || kcal <= 0 -> {
                _uiState.update { it.copy(templateValidationError = "kcal muss größer als 0 sein") }
                return
            }
            portionSize == null || portionSize <= 0 -> {
                _uiState.update { it.copy(templateValidationError = "Portionsgröße muss größer als 0 sein") }
                return
            }
            state.templateCategoryId == null -> {
                _uiState.update { it.copy(templateValidationError = "Bitte eine Kategorie wählen") }
                return
            }
        }

        val protein = state.templateProtein.trim().toDoubleOrNull()
        val carbs = state.templateCarbs.trim().toDoubleOrNull()
        val fat = state.templateFat.trim().toDoubleOrNull()

        viewModelScope.launch {
            val editing = state.editingTemplate
            if (editing != null) {
                foodRepository.updateTemplate(
                    editing.copy(
                        name = name,
                        kcal = kcal!!,
                        categoryId = state.templateCategoryId!!,
                        protein = protein,
                        carbs = carbs,
                        fat = fat,
                        portionSize = portionSize!!,
                        portionUnit = state.templatePortionUnit
                    )
                )
            } else {
                foodRepository.insertTemplate(
                    FoodTemplate(
                        name = name,
                        kcal = kcal!!,
                        categoryId = state.templateCategoryId!!,
                        protein = protein,
                        carbs = carbs,
                        fat = fat,
                        portionSize = portionSize!!,
                        portionUnit = state.templatePortionUnit
                    )
                )
            }
            _uiState.update { it.copy(showTemplateDialog = false) }
        }
    }

    fun showDeleteTemplateConfirmation(template: FoodTemplate) {
        _uiState.update {
            it.copy(showDeleteTemplateDialog = true, deletingTemplate = template)
        }
    }

    fun dismissDeleteTemplateDialog() {
        _uiState.update { it.copy(showDeleteTemplateDialog = false, deletingTemplate = null) }
    }

    fun confirmDeleteTemplate() {
        val template = _uiState.value.deletingTemplate ?: return
        viewModelScope.launch {
            foodRepository.deleteTemplate(template)
            _uiState.update { it.copy(showDeleteTemplateDialog = false, deletingTemplate = null) }
        }
    }

    // --- Add from template ---

    fun showAddFromTemplateSheet() {
        _uiState.update {
            it.copy(
                showAddFromTemplateSheet = true,
                templateSearchQuery = "",
                selectedTemplate = null,
                templateAmount = "1"
            )
        }
    }

    fun dismissAddFromTemplateSheet() {
        _uiState.update { it.copy(showAddFromTemplateSheet = false) }
    }

    fun onTemplateSearchQueryChanged(query: String) {
        _uiState.update { it.copy(templateSearchQuery = query) }
    }

    fun selectTemplateForEntry(template: FoodTemplate) {
        _uiState.update {
            it.copy(selectedTemplate = template, templateAmount = "1")
        }
    }

    fun onTemplateAmountChanged(amount: String) {
        _uiState.update { it.copy(templateAmount = amount) }
    }

    fun confirmAddFromTemplate() {
        val state = _uiState.value
        val template = state.selectedTemplate ?: return
        val amount = state.templateAmount.trim().toDoubleOrNull() ?: return
        if (amount <= 0) return

        val factor = amount
        val kcal = (template.kcal * factor).toInt()
        val protein = template.protein?.let { it * factor }
        val carbs = template.carbs?.let { it * factor }
        val fat = template.fat?.let { it * factor }

        viewModelScope.launch {
            foodRepository.insertEntry(
                FoodEntry(
                    date = state.selectedDate,
                    templateId = template.id,
                    name = template.name,
                    kcal = kcal,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    amount = amount * template.portionSize,
                    categoryId = template.categoryId
                )
            )
            _uiState.update { it.copy(showAddFromTemplateSheet = false) }
        }
    }

    // --- Manual entry ---

    fun showManualEntryDialog() {
        _uiState.update {
            it.copy(
                showManualEntryDialog = true,
                editingEntry = null,
                entryName = "",
                entryKcal = "",
                entryAmount = "1",
                entryProtein = "",
                entryCarbs = "",
                entryFat = "",
                entryCategoryId = it.categories.firstOrNull()?.id,
                entryValidationError = null
            )
        }
    }

    fun showEditEntryDialog(entry: FoodEntry) {
        _uiState.update {
            it.copy(
                showManualEntryDialog = true,
                editingEntry = entry,
                entryName = entry.name,
                entryKcal = entry.kcal.toString(),
                entryAmount = entry.amount.toString(),
                entryProtein = entry.protein?.toString() ?: "",
                entryCarbs = entry.carbs?.toString() ?: "",
                entryFat = entry.fat?.toString() ?: "",
                entryCategoryId = entry.categoryId,
                entryValidationError = null
            )
        }
    }

    fun dismissManualEntryDialog() {
        _uiState.update { it.copy(showManualEntryDialog = false) }
    }

    fun onEntryNameChanged(value: String) {
        _uiState.update { it.copy(entryName = value, entryValidationError = null) }
    }

    fun onEntryKcalChanged(value: String) {
        _uiState.update { it.copy(entryKcal = value, entryValidationError = null) }
    }

    fun onEntryAmountChanged(value: String) {
        _uiState.update { it.copy(entryAmount = value) }
    }

    fun onEntryProteinChanged(value: String) {
        _uiState.update { it.copy(entryProtein = value) }
    }

    fun onEntryCarbsChanged(value: String) {
        _uiState.update { it.copy(entryCarbs = value) }
    }

    fun onEntryFatChanged(value: String) {
        _uiState.update { it.copy(entryFat = value) }
    }

    fun onEntryCategoryChanged(categoryId: Long) {
        _uiState.update { it.copy(entryCategoryId = categoryId) }
    }

    fun saveEntry() {
        val state = _uiState.value
        val name = state.entryName.trim()
        val kcal = state.entryKcal.trim().toIntOrNull()
        val amount = state.entryAmount.trim().toDoubleOrNull() ?: 1.0

        when {
            name.isEmpty() -> {
                _uiState.update { it.copy(entryValidationError = "Name darf nicht leer sein") }
                return
            }
            kcal == null || kcal <= 0 -> {
                _uiState.update { it.copy(entryValidationError = "kcal muss größer als 0 sein") }
                return
            }
            state.entryCategoryId == null -> {
                _uiState.update { it.copy(entryValidationError = "Bitte eine Kategorie wählen") }
                return
            }
        }

        val protein = state.entryProtein.trim().toDoubleOrNull()
        val carbs = state.entryCarbs.trim().toDoubleOrNull()
        val fat = state.entryFat.trim().toDoubleOrNull()

        viewModelScope.launch {
            val editing = state.editingEntry
            if (editing != null) {
                foodRepository.updateEntry(
                    editing.copy(
                        name = name,
                        kcal = kcal!!,
                        amount = amount,
                        protein = protein,
                        carbs = carbs,
                        fat = fat,
                        categoryId = state.entryCategoryId!!
                    )
                )
            } else {
                foodRepository.insertEntry(
                    FoodEntry(
                        date = state.selectedDate,
                        name = name,
                        kcal = kcal!!,
                        amount = amount,
                        protein = protein,
                        carbs = carbs,
                        fat = fat,
                        categoryId = state.entryCategoryId!!
                    )
                )
            }
            _uiState.update { it.copy(showManualEntryDialog = false) }
        }
    }

    // --- Delete entry ---

    fun showDeleteEntryConfirmation(entry: FoodEntry) {
        _uiState.update { it.copy(showDeleteEntryDialog = true, deletingEntry = entry) }
    }

    fun dismissDeleteEntryDialog() {
        _uiState.update { it.copy(showDeleteEntryDialog = false, deletingEntry = null) }
    }

    fun confirmDeleteEntry() {
        val entry = _uiState.value.deletingEntry ?: return
        viewModelScope.launch {
            foodRepository.deleteEntry(entry)
            _uiState.update { it.copy(showDeleteEntryDialog = false, deletingEntry = null) }
        }
    }

    // --- Category management ---

    fun showCategoryManagement() {
        _uiState.update { it.copy(showCategoryManagement = true) }
    }

    fun dismissCategoryManagement() {
        _uiState.update { it.copy(showCategoryManagement = false) }
    }

    fun showAddCategoryDialog() {
        _uiState.update {
            it.copy(
                showAddCategoryDialog = true,
                editingCategory = null,
                categoryName = "",
                categoryValidationError = null
            )
        }
    }

    fun showEditCategoryDialog(category: FoodCategory) {
        _uiState.update {
            it.copy(
                showAddCategoryDialog = true,
                editingCategory = category,
                categoryName = category.name,
                categoryValidationError = null
            )
        }
    }

    fun dismissCategoryDialog() {
        _uiState.update { it.copy(showAddCategoryDialog = false) }
    }

    fun onCategoryNameChanged(value: String) {
        _uiState.update { it.copy(categoryName = value, categoryValidationError = null) }
    }

    fun saveCategory() {
        val state = _uiState.value
        val name = state.categoryName.trim()

        if (name.isEmpty()) {
            _uiState.update { it.copy(categoryValidationError = "Name darf nicht leer sein") }
            return
        }

        viewModelScope.launch {
            val editing = state.editingCategory
            if (editing != null) {
                foodRepository.updateCategory(editing.copy(name = name))
            } else {
                val maxOrder = foodRepository.getMaxCategorySortOrder() ?: -1
                foodRepository.insertCategory(
                    FoodCategory(name = name, sortOrder = maxOrder + 1)
                )
            }
            _uiState.update { it.copy(showAddCategoryDialog = false) }
        }
    }

    fun showDeleteCategoryConfirmation(category: FoodCategory) {
        _uiState.update {
            it.copy(
                showDeleteCategoryDialog = true,
                deletingCategory = category,
                deleteCategoryError = null
            )
        }
    }

    fun dismissDeleteCategoryDialog() {
        _uiState.update {
            it.copy(showDeleteCategoryDialog = false, deletingCategory = null, deleteCategoryError = null)
        }
    }

    fun confirmDeleteCategory() {
        val category = _uiState.value.deletingCategory ?: return
        viewModelScope.launch {
            val usageCount = foodRepository.getCategoryUsageCount(category.id)
            if (usageCount > 0) {
                _uiState.update {
                    it.copy(deleteCategoryError = "Kategorie wird noch von $usageCount Einträgen/Vorlagen verwendet")
                }
            } else {
                foodRepository.deleteCategory(category)
                _uiState.update {
                    it.copy(showDeleteCategoryDialog = false, deletingCategory = null)
                }
            }
        }
    }

    fun moveCategoryUp(category: FoodCategory) {
        val categories = _uiState.value.categories.sortedBy { it.sortOrder }
        val index = categories.indexOfFirst { it.id == category.id }
        if (index <= 0) return

        val other = categories[index - 1]
        viewModelScope.launch {
            foodRepository.updateCategory(category.copy(sortOrder = other.sortOrder))
            foodRepository.updateCategory(other.copy(sortOrder = category.sortOrder))
        }
    }

    fun moveCategoryDown(category: FoodCategory) {
        val categories = _uiState.value.categories.sortedBy { it.sortOrder }
        val index = categories.indexOfFirst { it.id == category.id }
        if (index < 0 || index >= categories.size - 1) return

        val other = categories[index + 1]
        viewModelScope.launch {
            foodRepository.updateCategory(category.copy(sortOrder = other.sortOrder))
            foodRepository.updateCategory(other.copy(sortOrder = category.sortOrder))
        }
    }

    class Factory(
        private val foodRepository: FoodRepository,
        private val dateFlow: StateFlow<LocalDate>,
        private val onDateChangedCallback: (LocalDate) -> Unit
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FoodViewModel(foodRepository, dateFlow, onDateChangedCallback) as T
        }
    }
}
