package de.leipsfur.kcal_track.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import java.io.File
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.leipsfur.kcal_track.data.BackupManager
import de.leipsfur.kcal_track.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class SettingsUiState(
    val bmrInput: String = "",
    val savedBmr: Int? = null,
    val validationError: String? = null,
    val saveSuccess: Boolean = false,
    val isBackupInProgress: Boolean = false,
    val isRestoreInProgress: Boolean = false,
    val showRestoreConfirmDialog: Boolean = false,
    val pendingRestoreFile: File? = null,
    val backupError: String? = null,
    val backupShareIntent: Intent? = null
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val backupManager: BackupManager,
    private val onDataChanged: () -> Unit
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getBmrForDate(LocalDate.now()).collect { bmr ->
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

    fun createBackup() {
        _uiState.update { it.copy(isBackupInProgress = true, backupError = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = backupManager.createBackup()
                val intent = backupManager.createShareIntent(uri)
                _uiState.update {
                    it.copy(isBackupInProgress = false, backupShareIntent = intent)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isBackupInProgress = false,
                        backupError = "Backup fehlgeschlagen: ${e.message}"
                    )
                }
            }
        }
    }

    fun onBackupShared() {
        _uiState.update { it.copy(backupShareIntent = null) }
    }

    fun onRestoreFileSelected(uri: Uri) {
        _uiState.update { it.copy(backupError = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val stagingFile = backupManager.copyToStagingFile(uri)
                val error = backupManager.validateBackup(stagingFile)
                if (error != null) {
                    stagingFile.delete()
                    _uiState.update { it.copy(backupError = error) }
                } else {
                    _uiState.update {
                        it.copy(
                            showRestoreConfirmDialog = true,
                            pendingRestoreFile = stagingFile
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(backupError = "Backup-Datei konnte nicht gelesen werden: ${e.message}")
                }
            }
        }
    }

    fun confirmRestore() {
        val stagingFile = _uiState.value.pendingRestoreFile ?: return
        _uiState.update {
            it.copy(
                showRestoreConfirmDialog = false,
                isRestoreInProgress = true,
                backupError = null
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                backupManager.restoreBackup(stagingFile)
                backupManager.restartApp()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRestoreInProgress = false,
                        backupError = "Wiederherstellung fehlgeschlagen: ${e.message}"
                    )
                }
            }
        }
    }

    fun dismissRestoreConfirmDialog() {
        _uiState.value.pendingRestoreFile?.delete()
        _uiState.update {
            it.copy(showRestoreConfirmDialog = false, pendingRestoreFile = null)
        }
    }

    fun dismissBackupError() {
        _uiState.update { it.copy(backupError = null) }
    }

    class Factory(
        private val settingsRepository: SettingsRepository,
        private val backupManager: BackupManager,
        private val onDataChanged: () -> Unit
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository, backupManager, onDataChanged) as T
        }
    }
}
