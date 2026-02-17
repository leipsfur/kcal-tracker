package de.leipsfur.kcal_track.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.db.entity.ActivityCategory
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import de.leipsfur.kcal_track.data.repository.ActivityRepository
import de.leipsfur.kcal_track.data.repository.FoodRepository
import de.leipsfur.kcal_track.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DashboardUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val bmr: Int? = null,
    val foodEntries: List<FoodEntry> = emptyList(),
    val activityEntries: List<ActivityEntry> = emptyList(),
    val foodCategories: List<FoodCategory> = emptyList(),
    val activityCategories: List<ActivityCategory> = emptyList()
) {
    val totalFoodKcal: Int get() = foodEntries.sumOf { it.kcal }
    val totalActivityKcal: Int get() = activityEntries.sumOf { it.kcal }
    val tdee: Int get() = (bmr ?: 0) + totalActivityKcal
    val remainingKcal: Int get() = tdee - totalFoodKcal
    
    // Helper to group entries by category, sorted by category sortOrder
    val groupedFoodEntries: Map<FoodCategory, List<FoodEntry>>
        get() {
            val categoryMap = foodCategories.associateBy { it.id }
            return foodEntries
                .groupBy { categoryMap[it.categoryId] }
                .filterKeys { it != null }
                .mapKeys { it.key!! }
                .toSortedMap(compareBy { it.sortOrder })
        }

    val groupedActivityEntries: Map<ActivityCategory, List<ActivityEntry>>
        get() {
            val categoryMap = activityCategories.associateBy { it.id }
            return activityEntries
                .groupBy { categoryMap[it.categoryId] }
                .filterKeys { it != null }
                .mapKeys { it.key!! }
                .toSortedMap(compareBy { it.sortOrder })
        }
}

class DashboardViewModel(
    private val foodRepository: FoodRepository,
    private val activityRepository: ActivityRepository,
    private val settingsRepository: SettingsRepository,
    private val dateFlow: StateFlow<LocalDate>,
    private val onDateChangedCallback: (LocalDate) -> Unit
) : ViewModel() {

    // Helper data class for combining multiple flows
    private data class DashboardData(
        val foodEntries: List<FoodEntry>,
        val activityEntries: List<ActivityEntry>,
        val foodCategories: List<FoodCategory>,
        val activityCategories: List<ActivityCategory>
    )

    // Derive flows based on selectedDate from dateFlow
    private val _foodEntries = dateFlow.flatMapLatest { date ->
        foodRepository.getEntriesByDate(date)
    }

    private val _activityEntries = dateFlow.flatMapLatest { date ->
        activityRepository.getEntriesByDate(date)
    }
    
    private val _foodCategories = foodRepository.getAllCategories()
    private val _activityCategories = activityRepository.getAllCategories()
    private val _bmrForSelectedDate = dateFlow.flatMapLatest { date ->
        settingsRepository.getBmrForDate(date)
    }

    // Combine data flows first
    private val _dataFlow = combine(
        _foodEntries,
        _activityEntries,
        _foodCategories,
        _activityCategories
    ) { foodEntries, activityEntries, foodCategories, activityCategories ->
        DashboardData(foodEntries, activityEntries, foodCategories, activityCategories)
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        dateFlow,
        _bmrForSelectedDate,
        _dataFlow
    ) { date, bmr, data ->
        DashboardUiState(
            selectedDate = date,
            bmr = bmr,
            foodEntries = data.foodEntries,
            activityEntries = data.activityEntries,
            foodCategories = data.foodCategories,
            activityCategories = data.activityCategories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun onDateChanged(date: LocalDate) {
        onDateChangedCallback(date)
    }

    fun updateFoodEntry(entry: FoodEntry) {
        viewModelScope.launch {
            foodRepository.updateEntry(entry)
        }
    }

    fun deleteFoodEntry(entry: FoodEntry) {
        viewModelScope.launch {
            foodRepository.deleteEntry(entry)
        }
    }
    
    fun updateActivityEntry(entry: ActivityEntry) {
        viewModelScope.launch {
            activityRepository.updateEntry(entry)
        }
    }
    
    fun deleteActivityEntry(entry: ActivityEntry) {
        viewModelScope.launch {
            activityRepository.deleteEntry(entry)
        }
    }

    class Factory(
        private val foodRepository: FoodRepository,
        private val activityRepository: ActivityRepository,
        private val settingsRepository: SettingsRepository,
        private val dateFlow: StateFlow<LocalDate>,
        private val onDateChangedCallback: (LocalDate) -> Unit
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(
                    foodRepository,
                    activityRepository,
                    settingsRepository,
                    dateFlow,
                    onDateChangedCallback
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
