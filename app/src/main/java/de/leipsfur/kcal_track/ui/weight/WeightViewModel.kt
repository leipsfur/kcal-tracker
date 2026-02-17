package de.leipsfur.kcal_track.ui.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.leipsfur.kcal_track.data.db.entity.WeightEntry
import de.leipsfur.kcal_track.data.repository.WeightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class WeightUiState(
    val entries: List<WeightEntryWithDiff> = emptyList(),
    val isLoading: Boolean = false
)

data class WeightEntryWithDiff(
    val entry: WeightEntry,
    val diff: Double? = null
)

class WeightViewModel(
    private val repository: WeightRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<WeightUiState> = repository.getAll()
        .combine(_isLoading) { entries, isLoading ->
            val entriesWithDiff = entries.mapIndexed { index, weightEntry ->
                val prevEntry = entries.getOrNull(index + 1)
                val diff = prevEntry?.let { weightEntry.weightKg - it.weightKg }
                WeightEntryWithDiff(weightEntry, diff)
            }
            WeightUiState(entriesWithDiff, isLoading)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WeightUiState(isLoading = true)
        )

    fun addWeight(weightKg: Double, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            val roundedWeight = kotlin.math.round(weightKg * 10) / 10.0
            repository.insert(WeightEntry(date = date, weightKg = roundedWeight))
        }
    }

    fun deleteEntry(entry: WeightEntry) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }

    class Factory(private val repository: WeightRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WeightViewModel(repository) as T
        }
    }
}
