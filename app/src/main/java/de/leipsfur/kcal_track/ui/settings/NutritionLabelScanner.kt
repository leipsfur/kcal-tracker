package de.leipsfur.kcal_track.ui.settings

import android.graphics.Bitmap
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerativeModel
import com.google.mlkit.genai.prompt.TextPart
import com.google.mlkit.genai.prompt.generateContentRequest
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class NutritionLabelScanner(
    private val textRecognizer: TextRecognizer,
    private val generativeModel: GenerativeModel
) {
    suspend fun scan(bitmap: Bitmap): NutritionScanResult {
        val ocrText = recognizeText(bitmap)
        if (ocrText.isBlank()) {
            throw Exception("Kein Text erkannt")
        }

        val status = generativeModel.checkStatus()
        if (status == FeatureStatus.UNAVAILABLE) {
            throw Exception("Gemini Nano ist auf diesem Gerät nicht verfügbar.")
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
                TextPart(buildPrompt(ocrText))
            ) {
                temperature = 0.2f
                topK = 10
                maxOutputTokens = 256
            }
        )
        val resultText = response.candidates?.firstOrNull()?.text ?: ""
        return parseNutritionResult(resultText)
    }

    private suspend fun recognizeText(bitmap: Bitmap): String {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        return suspendCancellableCoroutine { continuation ->
            textRecognizer.process(inputImage)
                .addOnSuccessListener { result ->
                    continuation.resume(result.text)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    companion object {
        internal const val KJ_TO_KCAL_THRESHOLD = 900
        internal const val KJ_TO_KCAL_FACTOR = 4.184

        internal fun buildPrompt(ocrText: String): String =
            "Parse the following OCR text from a nutrition label.\n" +
            "IMPORTANT: European labels show energy as both kJ and kcal (e.g. '1234 kJ / 295 kcal').\n" +
            "Always return the kcal value, NOT the kJ value.\n" +
            "kcal is always the smaller number (roughly kJ divided by 4.184).\n" +
            "If only kJ is present, divide by 4.184 to get kcal.\n" +
            "Extract: calories in kcal, protein in g, carbohydrates in g, fat in g.\n" +
            "Respond only with JSON: {\"kcal\": 123, \"protein\": 4.5, \"carbs\": 6.7, \"fat\": 8.9}.\n" +
            "Use null for unreadable values.\n\n" +
            "OCR text:\n" +
            ocrText

        internal fun parseNutritionResult(text: String): NutritionScanResult {
            return try {
                val jsonMatch = Regex("\\{[^}]+\\}").find(text)?.value ?: text
                val obj = JSONObject(jsonMatch)
                var kcal = if (obj.has("kcal") && !obj.isNull("kcal")) obj.getInt("kcal") else null
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
    }
}
