package de.leipsfur.kcal_track.ui.settings

data class NutritionScanResult(
    val kcal: Int? = null,
    val proteinGrams: Double? = null,
    val carbsGrams: Double? = null,
    val fatGrams: Double? = null
)

sealed interface ScanState {
    data object Idle : ScanState
    data object Scanning : ScanState
    data class Result(val result: NutritionScanResult) : ScanState
    data class Error(val message: String) : ScanState
}
