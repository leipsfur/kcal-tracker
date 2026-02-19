package de.leipsfur.kcal_track.ui.settings

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import java.io.File
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.ImagePart
import com.google.mlkit.genai.prompt.TextPart
import com.google.mlkit.genai.prompt.generateContentRequest
import de.leipsfur.kcal_track.data.BackupManager
import de.leipsfur.kcal_track.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
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
    val backupShareIntent: Intent? = null,
    val scanState: ScanState = ScanState.Idle
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

    fun onImageCaptured(bitmap: Bitmap) {
        _uiState.update { it.copy(scanState = ScanState.Scanning) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val generativeModel = Generation.getClient()
                val status = generativeModel.checkStatus()
                if (status == FeatureStatus.UNAVAILABLE) {
                    _uiState.update {
                        it.copy(scanState = ScanState.Error("Gemini Nano ist auf diesem Gerät nicht verfügbar."))
                    }
                    return@launch
                }
                if (status == FeatureStatus.DOWNLOADABLE || status == FeatureStatus.DOWNLOADING) {
                    generativeModel.download().collect { downloadStatus ->
                        when (downloadStatus) {
                            is DownloadStatus.DownloadFailed ->
                                throw Exception("Modell-Download fehlgeschlagen")
                            DownloadStatus.DownloadCompleted -> return@collect
                            else -> { /* wait */ }
                        }
                    }
                }
                val response = generativeModel.generateContent(
                    generateContentRequest(
                        ImagePart(bitmap),
                        TextPart(NUTRITION_SCAN_PROMPT)
                    ) {
                        temperature = 0.2f
                        topK = 10
                        maxOutputTokens = 256
                    }
                )
                val resultText = response.candidates?.firstOrNull()?.text ?: ""
                val result = parseNutritionResult(resultText)
                _uiState.update { it.copy(scanState = ScanState.Result(result)) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(scanState = ScanState.Error(e.message ?: "Unbekannter Fehler"))
                }
            }
        }
    }

    fun dismissScanResult() {
        _uiState.update { it.copy(scanState = ScanState.Idle) }
    }

    private fun parseNutritionResult(text: String): NutritionScanResult {
        return try {
            val jsonMatch = Regex("\\{[^}]+\\}").find(text)?.value ?: text
            val obj = JSONObject(jsonMatch)
            var kcal = if (obj.has("kcal") && !obj.isNull("kcal")) obj.getInt("kcal") else null
            // Heuristik: Werte > 900 sind fast sicher kJ statt kcal → umrechnen
            if (kcal != null && kcal > KJ_TO_KCAL_THRESHOLD) {
                kcal = (kcal / KJ_TO_KCAL_FACTOR).toInt()
            }
            NutritionScanResult(
                kcal = kcal,
                proteinGrams = if (obj.has("protein") && !obj.isNull("protein")) obj.getDouble("protein") else null,
                carbsGrams = if (obj.has("carbs") && !obj.isNull("carbs")) obj.getDouble("carbs") else null,
                fatGrams = if (obj.has("fat") && !obj.isNull("fat")) obj.getDouble("fat") else null
            )
        } catch (_: Exception) {
            NutritionScanResult()
        }
    }

    companion object {
        private const val NUTRITION_SCAN_PROMPT =
            "Read the nutrition label in this image. " +
            "IMPORTANT: European labels show energy as both kJ and kcal (e.g. '1234 kJ / 295 kcal'). " +
            "Always return the kcal value, NOT the kJ value. " +
            "kcal is always the smaller number (roughly kJ divided by 4.184). " +
            "Extract: calories in kcal, protein in g, carbohydrates in g, fat in g. " +
            "Respond only with JSON: {\"kcal\": 123, \"protein\": 4.5, \"carbs\": 6.7, \"fat\": 8.9}. " +
            "Use null for unreadable values."

        private const val KJ_TO_KCAL_THRESHOLD = 900
        private const val KJ_TO_KCAL_FACTOR = 4.184
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
