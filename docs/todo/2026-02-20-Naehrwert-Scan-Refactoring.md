# Nährwert-Scan: Refactoring auf Zweistufige Pipeline (OCR + LLM)

**Datum:** 2026-02-20

## Kontext

Der Nährwert-Scan wurde von einem einstufigen Ansatz (Gemini Nano mit ImagePart) auf eine zweistufige Pipeline umgestellt:

```
Kamera → Bitmap → ML Kit OCR → Rohtext → Gemini Nano (nur TextPart) → JSON → NutritionScanResult
```

## Vorteile

- Breitere Gerätekompatibilität (ML Kit OCR läuft auf allen Geräten, Gemini Nano Vision war auf Pixel 9+ beschränkt)
- Bessere Testbarkeit (OCR und LLM-Parsing separat testbar)
- Zuverlässigere Ergebnisse (Text-zu-JSON statt Bild-zu-JSON)

## Erledigte Aufgaben

- [x] ML Kit Text Recognition Dependency hinzugefügt (bundled, ~4 MB)
- [x] `NutritionLabelScanner` Klasse erstellt (orchestriert OCR → LLM Pipeline)
- [x] `SettingsViewModel` vereinfacht (delegiert an `NutritionLabelScanner`)
- [x] DI-Verdrahtung in `KcalTrackApplication` und `MainActivity` aktualisiert
- [x] Unit-Tests für `parseNutritionResult` und `buildPrompt` geschrieben
- [x] US-0506 Dokumentation aktualisiert

## Geänderte Dateien

- `gradle/libs.versions.toml` — ML Kit Text Recognition Version + Library
- `app/build.gradle.kts` — Implementation-Dependency
- `app/src/main/java/.../ui/settings/NutritionLabelScanner.kt` — Neu
- `app/src/main/java/.../ui/settings/SettingsViewModel.kt` — Vereinfacht
- `app/src/main/java/.../KcalTrackApplication.kt` — DI für Scanner
- `app/src/main/java/.../MainActivity.kt` — Factory-Parameter
- `app/src/test/java/.../ui/settings/NutritionLabelScannerTest.kt` — Neu
- `docs/stories/05-einstellungen.md` — US-0506 aktualisiert
