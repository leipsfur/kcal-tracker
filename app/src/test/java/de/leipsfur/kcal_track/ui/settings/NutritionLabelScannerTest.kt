package de.leipsfur.kcal_track.ui.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NutritionLabelScannerTest {

    @Test
    fun `parseNutritionResult with valid JSON returns correct values`() {
        val json = """{"kcal": 250, "protein": 12.5, "carbs": 30.0, "fat": 8.3}"""
        val result = NutritionLabelScanner.parseNutritionResult(json)

        assertEquals(250, result.kcal)
        assertEquals(12.5, result.proteinGrams!!, 0.01)
        assertEquals(30.0, result.carbsGrams!!, 0.01)
        assertEquals(8.3, result.fatGrams!!, 0.01)
    }

    @Test
    fun `parseNutritionResult with null values returns nulls`() {
        val json = """{"kcal": 100, "protein": null, "carbs": null, "fat": 5.0}"""
        val result = NutritionLabelScanner.parseNutritionResult(json)

        assertEquals(100, result.kcal)
        assertNull(result.proteinGrams)
        assertNull(result.carbsGrams)
        assertEquals(5.0, result.fatGrams!!, 0.01)
    }

    @Test
    fun `parseNutritionResult converts kJ to kcal when value exceeds threshold`() {
        val json = """{"kcal": 1234, "protein": 10.0, "carbs": 20.0, "fat": 5.0}"""
        val result = NutritionLabelScanner.parseNutritionResult(json)

        // 1234 > 900 → 1234 / 4.184 = 294
        assertEquals(294, result.kcal)
    }

    @Test
    fun `parseNutritionResult does not convert kcal below threshold`() {
        val json = """{"kcal": 500, "protein": 10.0, "carbs": 20.0, "fat": 5.0}"""
        val result = NutritionLabelScanner.parseNutritionResult(json)

        assertEquals(500, result.kcal)
    }

    @Test
    fun `parseNutritionResult with invalid input returns empty result`() {
        val result = NutritionLabelScanner.parseNutritionResult("not json at all")

        assertNull(result.kcal)
        assertNull(result.proteinGrams)
        assertNull(result.carbsGrams)
        assertNull(result.fatGrams)
    }

    @Test
    fun `parseNutritionResult extracts JSON from surrounding text`() {
        val text = """Here are the results: {"kcal": 200, "protein": 8.0, "carbs": 25.0, "fat": 10.0} done."""
        val result = NutritionLabelScanner.parseNutritionResult(text)

        assertEquals(200, result.kcal)
        assertEquals(8.0, result.proteinGrams!!, 0.01)
        assertEquals(25.0, result.carbsGrams!!, 0.01)
        assertEquals(10.0, result.fatGrams!!, 0.01)
    }

    @Test
    fun `parseNutritionResult with all null kcal returns null`() {
        val json = """{"kcal": null, "protein": null, "carbs": null, "fat": null}"""
        val result = NutritionLabelScanner.parseNutritionResult(json)

        assertNull(result.kcal)
        assertNull(result.proteinGrams)
        assertNull(result.carbsGrams)
        assertNull(result.fatGrams)
    }

    @Test
    fun `buildPrompt contains OCR text`() {
        val ocrText = "Brennwert 250 kcal\nEiweiß 12g"
        val prompt = NutritionLabelScanner.buildPrompt(ocrText)

        assertTrue(prompt.contains(ocrText))
    }

    @Test
    fun `buildPrompt contains kJ instructions`() {
        val prompt = NutritionLabelScanner.buildPrompt("test")

        assertTrue(prompt.contains("kJ"))
        assertTrue(prompt.contains("kcal"))
        assertTrue(prompt.contains("4.184"))
    }

    @Test
    fun `buildPrompt contains JSON format instruction`() {
        val prompt = NutritionLabelScanner.buildPrompt("test")

        assertTrue(prompt.contains("JSON"))
        assertTrue(prompt.contains("\"kcal\""))
        assertTrue(prompt.contains("\"protein\""))
        assertTrue(prompt.contains("\"carbs\""))
        assertTrue(prompt.contains("\"fat\""))
    }

    @Test
    fun `parseNutritionResult with missing fields returns partial result`() {
        val json = """{"kcal": 150}"""
        val result = NutritionLabelScanner.parseNutritionResult(json)

        assertEquals(150, result.kcal)
        assertNull(result.proteinGrams)
        assertNull(result.carbsGrams)
        assertNull(result.fatGrams)
    }
}
