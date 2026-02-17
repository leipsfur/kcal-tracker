package de.leipsfur.kcal_track.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.leipsfur.kcal_track.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val bmrInput: String = "",
    val savedBmr: Int? = null,
    val validationError: String? = null,
    val saveSuccess: Boolean = false
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val onDataChanged: () -> Unit
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                val bmr = settings?.bmr
                _uiState.update { state ->
                    state.copy(
                        savedBmr = bmr,
                        bmrInput = if (state.bmrInput.isEmpty() && bmr != null) {
                            bmr.toString()
                        } else {
                            state.bmrInput
                        }
                    )
                }
            }
        }
    }

    fun onBmrInputChanged(input: String) {
        _uiState.update {
            it.copy(
                bmrInput = input,
                validationError = null,
                saveSuccess = false
            )
        }
    }

    fun saveBmr() {
        val input = _uiState.value.bmrInput.trim()
        val value = input.toIntOrNull()

        when {
            input.isEmpty() -> {
                _uiState.update { it.copy(validationError = "Bitte einen Wert eingeben") }
            }
            value == null -> {
                _uiState.update { it.copy(validationError = "Bitte eine ganze Zahl eingeben") }
            }
            value < 500 || value > 5000 -> {
                _uiState.update { it.copy(validationError = "Wert muss zwischen 500 und 5000 kcal liegen") }
            }
            else -> {
                viewModelScope.launch {
                    settingsRepository.updateBmr(value)
                    onDataChanged()
                    _uiState.update {
                        it.copy(
                            validationError = null,
                            saveSuccess = true
                        )
                    }
                }
            }
        }
    }

    fun dismissSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    class Factory(
        private val settingsRepository: SettingsRepository,
        private val onDataChanged: () -> Unit
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository, onDataChanged) as T
        }
    }
}