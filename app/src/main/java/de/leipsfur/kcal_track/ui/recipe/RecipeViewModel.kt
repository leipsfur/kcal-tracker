package de.leipsfur.kcal_track.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.Ingredient
import de.leipsfur.kcal_track.data.db.entity.Recipe
import de.leipsfur.kcal_track.data.db.entity.RecipeIngredient
import de.leipsfur.kcal_track.data.repository.FoodRepository
import de.leipsfur.kcal_track.data.repository.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class RecipeUiState(
    // Recipe list
    val recipes: List<Recipe> = emptyList(),
    val foodCategories: List<FoodCategory> = emptyList(),

    // Currently viewed/edited recipe
    val currentRecipe: Recipe? = null,
    val currentIngredients: List<RecipeIngredient> = emptyList(),
    val showRecipeDetail: Boolean = false,

    // Calculated values for current recipe
    val totalKcal: Int = 0,
    val totalProtein: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFat: Double = 0.0,

    // Create recipe dialog
    val showCreateRecipeDialog: Boolean = false,
    val newRecipeName: String = "",
    val createRecipeValidationError: String? = null,

    // Add/edit ingredient dialog
    val showIngredientDialog: Boolean = false,
    val editingIngredient: RecipeIngredient? = null,
    val ingredientName: String = "",
    val ingredientKcalPer100: String = "",
    val ingredientReferenceUnit: String = "g",
    val ingredientAmount: String = "",
    val ingredientProtein: String = "",
    val ingredientCarbs: String = "",
    val ingredientFat: String = "",
    val ingredientValidationError: String? = null,
    val ingredientSuggestions: List<Ingredient> = emptyList(),
    val selectedIngredientId: Long? = null,

    // Delete ingredient confirmation
    val showDeleteIngredientDialog: Boolean = false,
    val deletingIngredient: RecipeIngredient? = null,

    // Finish recipe dialog (enter portions and add as meal)
    val showFinishDialog: Boolean = false,
    val finishPortionsCooked: String = "",
    val finishPortionsEaten: String = "",
    val finishCategoryId: Long? = null,
    val finishValidationError: String? = null,

    // Delete recipe confirmation
    val showDeleteRecipeDialog: Boolean = false,
    val deletingRecipe: Recipe? = null,

    // Add portions from existing recipe dialog
    val showAddPortionsDialog: Boolean = false,
    val addPortionsRecipe: Recipe? = null,
    val addPortionsAmount: String = "1",
    val addPortionsCategoryId: Long? = null,
    val addPortionsValidationError: String? = null
)

class RecipeViewModel(
    private val recipeRepository: RecipeRepository,
    private val foodRepository: FoodRepository,
    private val dateFlow: StateFlow<LocalDate>,
    private val onDataChanged: () -> Unit
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    private var ingredientCollectionJob: Job? = null
    private var ingredientSearchJob: Job? = null

    init {
        viewModelScope.launch {
            launch {
                recipeRepository.getAllRecipes().collect { recipes ->
                    _uiState.update { it.copy(recipes = recipes) }
                }
            }
            launch {
                foodRepository.getAllCategories().collect { categories ->
                    _uiState.update {
                        it.copy(
                            foodCategories = categories,
                            finishCategoryId = it.finishCategoryId ?: categories.firstOrNull()?.id,
                            addPortionsCategoryId = it.addPortionsCategoryId ?: categories.firstOrNull()?.id
                        )
                    }
                }
            }
        }
    }

    // --- Recipe List Actions ---

    fun showCreateRecipeDialog() {
        _uiState.update {
            it.copy(
                showCreateRecipeDialog = true,
                newRecipeName = "",
                createRecipeValidationError = null
            )
        }
    }

    fun dismissCreateRecipeDialog() {
        _uiState.update { it.copy(showCreateRecipeDialog = false) }
    }

    fun onNewRecipeNameChanged(value: String) {
        _uiState.update { it.copy(newRecipeName = value, createRecipeValidationError = null) }
    }

    fun createRecipe() {
        val name = _uiState.value.newRecipeName.trim()
        if (name.isEmpty()) {
            _uiState.update { it.copy(createRecipeValidationError = "Name darf nicht leer sein") }
            return
        }

        viewModelScope.launch {
            val recipe = Recipe(
                name = name,
                createdDate = dateFlow.value,
                status = "in_progress"
            )
            val id = recipeRepository.insertRecipe(recipe)
            val createdRecipe = recipeRepository.getRecipeById(id) ?: recipe.copy(id = id)
            _uiState.update { it.copy(showCreateRecipeDialog = false) }
            openRecipeDetail(createdRecipe)
        }
    }

    fun openRecipeDetail(recipe: Recipe) {
        ingredientCollectionJob?.cancel()
        _uiState.update {
            it.copy(
                currentRecipe = recipe,
                showRecipeDetail = true,
                currentIngredients = emptyList(),
                totalKcal = 0,
                totalProtein = 0.0,
                totalCarbs = 0.0,
                totalFat = 0.0
            )
        }
        ingredientCollectionJob = viewModelScope.launch {
            recipeRepository.getIngredientsByRecipeId(recipe.id).collect { ingredients ->
                _uiState.update { it.copy(currentIngredients = ingredients) }
                recalculateTotals(ingredients)
            }
        }
    }

    fun closeRecipeDetail() {
        ingredientCollectionJob?.cancel()
        ingredientCollectionJob = null
        _uiState.update {
            it.copy(
                showRecipeDetail = false,
                currentRecipe = null,
                currentIngredients = emptyList(),
                totalKcal = 0,
                totalProtein = 0.0,
                totalCarbs = 0.0,
                totalFat = 0.0
            )
        }
    }

    fun showDeleteRecipeConfirmation(recipe: Recipe) {
        _uiState.update {
            it.copy(showDeleteRecipeDialog = true, deletingRecipe = recipe)
        }
    }

    fun dismissDeleteRecipeDialog() {
        _uiState.update { it.copy(showDeleteRecipeDialog = false, deletingRecipe = null) }
    }

    fun confirmDeleteRecipe() {
        val recipe = _uiState.value.deletingRecipe ?: return
        viewModelScope.launch {
            recipeRepository.deleteRecipe(recipe)
            onDataChanged()
            val wasCurrentDetail = _uiState.value.currentRecipe?.id == recipe.id
            _uiState.update { it.copy(showDeleteRecipeDialog = false, deletingRecipe = null) }
            if (wasCurrentDetail) {
                closeRecipeDetail()
            }
        }
    }

    fun copyRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val newRecipeId = recipeRepository.copyRecipe(recipe.id)
            val copiedRecipe = recipeRepository.getRecipeById(newRecipeId)
            if (copiedRecipe != null) {
                openRecipeDetail(copiedRecipe)
            }
        }
    }

    // --- Ingredient Actions ---

    fun showAddIngredientDialog() {
        _uiState.update {
            it.copy(
                showIngredientDialog = true,
                editingIngredient = null,
                ingredientName = "",
                ingredientKcalPer100 = "",
                ingredientReferenceUnit = "g",
                ingredientAmount = "",
                ingredientProtein = "",
                ingredientCarbs = "",
                ingredientFat = "",
                ingredientValidationError = null,
                ingredientSuggestions = emptyList(),
                selectedIngredientId = null
            )
        }
    }

    fun showEditIngredientDialog(ingredient: RecipeIngredient) {
        _uiState.update {
            it.copy(
                showIngredientDialog = true,
                editingIngredient = ingredient,
                ingredientName = ingredient.name,
                ingredientKcalPer100 = ingredient.kcalPer100.toString(),
                ingredientReferenceUnit = ingredient.referenceUnit,
                ingredientAmount = ingredient.amount.toString(),
                ingredientProtein = ingredient.protein?.toString() ?: "",
                ingredientCarbs = ingredient.carbs?.toString() ?: "",
                ingredientFat = ingredient.fat?.toString() ?: "",
                ingredientValidationError = null,
                ingredientSuggestions = emptyList(),
                selectedIngredientId = ingredient.ingredientId
            )
        }
    }

    fun dismissIngredientDialog() {
        ingredientSearchJob?.cancel()
        ingredientSearchJob = null
        _uiState.update { it.copy(showIngredientDialog = false) }
    }

    fun onIngredientNameChanged(value: String) {
        _uiState.update { it.copy(ingredientName = value, ingredientValidationError = null) }

        ingredientSearchJob?.cancel()
        if (value.trim().length >= 2) {
            ingredientSearchJob = viewModelScope.launch {
                recipeRepository.searchIngredients(value.trim()).collect { suggestions ->
                    _uiState.update { it.copy(ingredientSuggestions = suggestions) }
                }
            }
        } else {
            _uiState.update { it.copy(ingredientSuggestions = emptyList()) }
        }
    }

    fun selectIngredientSuggestion(ingredient: Ingredient) {
        _uiState.update {
            it.copy(
                ingredientName = ingredient.name,
                ingredientKcalPer100 = ingredient.kcalPer100.toString(),
                ingredientReferenceUnit = ingredient.referenceUnit,
                ingredientProtein = ingredient.protein?.toString() ?: "",
                ingredientCarbs = ingredient.carbs?.toString() ?: "",
                ingredientFat = ingredient.fat?.toString() ?: "",
                selectedIngredientId = ingredient.id,
                ingredientSuggestions = emptyList()
            )
        }
    }

    fun onIngredientKcalPer100Changed(value: String) {
        _uiState.update { it.copy(ingredientKcalPer100 = value, ingredientValidationError = null) }
    }

    fun onIngredientReferenceUnitChanged(value: String) {
        _uiState.update { it.copy(ingredientReferenceUnit = value) }
    }

    fun onIngredientAmountChanged(value: String) {
        _uiState.update { it.copy(ingredientAmount = value, ingredientValidationError = null) }
    }

    fun onIngredientProteinChanged(value: String) {
        _uiState.update { it.copy(ingredientProtein = value) }
    }

    fun onIngredientCarbsChanged(value: String) {
        _uiState.update { it.copy(ingredientCarbs = value) }
    }

    fun onIngredientFatChanged(value: String) {
        _uiState.update { it.copy(ingredientFat = value) }
    }

    fun saveIngredient() {
        val state = _uiState.value
        val currentRecipe = state.currentRecipe ?: return
        val name = state.ingredientName.trim()
        val kcalPer100 = state.ingredientKcalPer100.trim().toDoubleOrNull()
        val amount = state.ingredientAmount.trim().toDoubleOrNull()

        when {
            name.isEmpty() -> {
                _uiState.update { it.copy(ingredientValidationError = "Name darf nicht leer sein") }
                return
            }
            kcalPer100 == null || kcalPer100 <= 0 -> {
                _uiState.update { it.copy(ingredientValidationError = "kcal pro 100 muss gr\u00f6\u00dfer als 0 sein") }
                return
            }
            amount == null || amount <= 0 -> {
                _uiState.update { it.copy(ingredientValidationError = "Menge muss gr\u00f6\u00dfer als 0 sein") }
                return
            }
        }

        val protein = state.ingredientProtein.trim().toDoubleOrNull()
        val carbs = state.ingredientCarbs.trim().toDoubleOrNull()
        val fat = state.ingredientFat.trim().toDoubleOrNull()

        viewModelScope.launch {
            val editing = state.editingIngredient
            if (editing != null) {
                recipeRepository.updateRecipeIngredient(
                    editing.copy(
                        name = name,
                        kcalPer100 = kcalPer100!!,
                        referenceUnit = state.ingredientReferenceUnit,
                        amount = amount!!,
                        protein = protein,
                        carbs = carbs,
                        fat = fat,
                        ingredientId = state.selectedIngredientId
                    )
                )
            } else {
                recipeRepository.insertRecipeIngredient(
                    RecipeIngredient(
                        recipeId = currentRecipe.id,
                        ingredientId = state.selectedIngredientId,
                        name = name,
                        kcalPer100 = kcalPer100!!,
                        referenceUnit = state.ingredientReferenceUnit,
                        amount = amount!!,
                        protein = protein,
                        carbs = carbs,
                        fat = fat
                    )
                )
            }

            // Upsert ingredient in lookup table
            recipeRepository.upsertIngredient(
                name = name,
                kcalPer100 = kcalPer100!!,
                referenceUnit = state.ingredientReferenceUnit,
                protein = protein,
                carbs = carbs,
                fat = fat
            )

            _uiState.update { it.copy(showIngredientDialog = false) }
        }
    }

    fun showDeleteIngredientConfirmation(ingredient: RecipeIngredient) {
        _uiState.update {
            it.copy(showDeleteIngredientDialog = true, deletingIngredient = ingredient)
        }
    }

    fun dismissDeleteIngredientDialog() {
        _uiState.update { it.copy(showDeleteIngredientDialog = false, deletingIngredient = null) }
    }

    fun confirmDeleteIngredient() {
        val ingredient = _uiState.value.deletingIngredient ?: return
        viewModelScope.launch {
            recipeRepository.deleteRecipeIngredient(ingredient)
            _uiState.update { it.copy(showDeleteIngredientDialog = false, deletingIngredient = null) }
        }
    }

    // --- Finish Recipe (add as meal) ---

    fun showFinishDialog() {
        val currentRecipe = _uiState.value.currentRecipe ?: return
        _uiState.update {
            it.copy(
                showFinishDialog = true,
                finishPortionsCooked = currentRecipe.totalPortions?.toString() ?: "",
                finishPortionsEaten = "",
                finishCategoryId = it.finishCategoryId ?: it.foodCategories.firstOrNull()?.id,
                finishValidationError = null
            )
        }
    }

    fun dismissFinishDialog() {
        _uiState.update { it.copy(showFinishDialog = false) }
    }

    fun onFinishPortionsCookedChanged(value: String) {
        _uiState.update { it.copy(finishPortionsCooked = value, finishValidationError = null) }

        // Update the recipe's totalPortions in the DB
        val currentRecipe = _uiState.value.currentRecipe ?: return
        val portions = value.trim().toDoubleOrNull() ?: return
        if (portions > 0) {
            viewModelScope.launch {
                val updatedRecipe = currentRecipe.copy(totalPortions = portions)
                recipeRepository.updateRecipe(updatedRecipe)
                _uiState.update { it.copy(currentRecipe = updatedRecipe) }
            }
        }
    }

    fun onFinishPortionsEatenChanged(value: String) {
        _uiState.update { it.copy(finishPortionsEaten = value, finishValidationError = null) }
    }

    fun onFinishCategoryChanged(categoryId: Long) {
        _uiState.update { it.copy(finishCategoryId = categoryId) }
    }

    fun confirmFinishRecipe() {
        val state = _uiState.value
        val currentRecipe = state.currentRecipe ?: return
        val portionsCooked = state.finishPortionsCooked.trim().toDoubleOrNull()
        val portionsEaten = state.finishPortionsEaten.trim().toDoubleOrNull()
        val categoryId = state.finishCategoryId

        when {
            portionsCooked == null || portionsCooked <= 0 -> {
                _uiState.update { it.copy(finishValidationError = "Gekochte Portionen m\u00fcssen gr\u00f6\u00dfer als 0 sein") }
                return
            }
            portionsEaten == null || portionsEaten <= 0 -> {
                _uiState.update { it.copy(finishValidationError = "Gegessene Portionen m\u00fcssen gr\u00f6\u00dfer als 0 sein") }
                return
            }
            portionsEaten > portionsCooked -> {
                _uiState.update { it.copy(finishValidationError = "Gegessene Portionen d\u00fcrfen nicht gr\u00f6\u00dfer als gekochte sein") }
                return
            }
            categoryId == null -> {
                _uiState.update { it.copy(finishValidationError = "Bitte eine Kategorie w\u00e4hlen") }
                return
            }
        }

        viewModelScope.launch {
            // Update recipe with totalPortions and status
            val updatedRecipe = currentRecipe.copy(
                totalPortions = portionsCooked,
                status = "completed"
            )
            recipeRepository.updateRecipe(updatedRecipe)
            _uiState.update { it.copy(currentRecipe = updatedRecipe) }

            // Add as food entry
            val ingredients = state.currentIngredients
            val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            recipeRepository.addRecipeAsFoodEntry(
                recipe = updatedRecipe,
                ingredients = ingredients,
                eatenPortions = portionsEaten!!,
                categoryId = categoryId!!,
                date = dateFlow.value,
                time = time
            )

            onDataChanged()
            _uiState.update { it.copy(showFinishDialog = false) }
        }
    }

    // --- Add Portions from Previous Recipe ---

    fun showAddPortionsDialog(recipe: Recipe) {
        _uiState.update {
            it.copy(
                showAddPortionsDialog = true,
                addPortionsRecipe = recipe,
                addPortionsAmount = "1",
                addPortionsCategoryId = it.addPortionsCategoryId ?: it.foodCategories.firstOrNull()?.id,
                addPortionsValidationError = null
            )
        }
    }

    fun dismissAddPortionsDialog() {
        _uiState.update { it.copy(showAddPortionsDialog = false, addPortionsRecipe = null) }
    }

    fun onAddPortionsAmountChanged(value: String) {
        _uiState.update { it.copy(addPortionsAmount = value, addPortionsValidationError = null) }
    }

    fun onAddPortionsCategoryChanged(categoryId: Long) {
        _uiState.update { it.copy(addPortionsCategoryId = categoryId) }
    }

    fun confirmAddPortions() {
        val state = _uiState.value
        val recipe = state.addPortionsRecipe ?: return
        val amount = state.addPortionsAmount.trim().toDoubleOrNull()
        val categoryId = state.addPortionsCategoryId

        when {
            amount == null || amount <= 0 -> {
                _uiState.update { it.copy(addPortionsValidationError = "Portionen m\u00fcssen gr\u00f6\u00dfer als 0 sein") }
                return
            }
            recipe.totalPortions == null -> {
                _uiState.update { it.copy(addPortionsValidationError = "Rezept hat keine Portionsangabe") }
                return
            }
            categoryId == null -> {
                _uiState.update { it.copy(addPortionsValidationError = "Bitte eine Kategorie w\u00e4hlen") }
                return
            }
        }

        viewModelScope.launch {
            val ingredients = recipeRepository.getIngredientsByRecipeIdOnce(recipe.id)
            val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

            recipeRepository.addRecipeAsFoodEntry(
                recipe = recipe,
                ingredients = ingredients,
                eatenPortions = amount!!,
                categoryId = categoryId!!,
                date = dateFlow.value,
                time = time
            )

            onDataChanged()
            _uiState.update { it.copy(showAddPortionsDialog = false, addPortionsRecipe = null) }
        }
    }

    // --- Helper ---

    private fun recalculateTotals(ingredients: List<RecipeIngredient>) {
        val totalKcal = ingredients.sumOf { (it.kcalPer100 * it.amount / 100).toInt() }
        val totalProtein = ingredients.sumOf { (it.protein ?: 0.0) * it.amount / 100 }
        val totalCarbs = ingredients.sumOf { (it.carbs ?: 0.0) * it.amount / 100 }
        val totalFat = ingredients.sumOf { (it.fat ?: 0.0) * it.amount / 100 }
        _uiState.update {
            it.copy(
                totalKcal = totalKcal,
                totalProtein = totalProtein,
                totalCarbs = totalCarbs,
                totalFat = totalFat
            )
        }
    }

    class Factory(
        private val recipeRepository: RecipeRepository,
        private val foodRepository: FoodRepository,
        private val dateFlow: StateFlow<LocalDate>,
        private val onDataChanged: () -> Unit
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipeViewModel(recipeRepository, foodRepository, dateFlow, onDataChanged) as T
        }
    }
}
