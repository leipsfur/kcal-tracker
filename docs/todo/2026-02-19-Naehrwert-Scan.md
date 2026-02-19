# Nährwerttabellen-Scan via Gemini Nano (US-0506, US-0507)

Dokumentation: [docs/stories/05-einstellungen.md](../stories/05-einstellungen.md)

- [x] Google AI Edge SDK Dependency in `libs.versions.toml` und `build.gradle.kts` hinzufügen
- [x] Kamera-Permission (`CAMERA`) in `AndroidManifest.xml` deklarieren
- [x] Runtime-Permission-Handling für Kamera implementieren
- [x] Kamera-Intent oder CameraX-Integration für Bildaufnahme
- [x] Gemini Nano Client initialisieren und Modell-Verfügbarkeit prüfen
- [x] Prompt für Nährwerttabellen-Erkennung erstellen (strukturierte Ausgabe: kcal, Protein, Kohlenhydrate, Fett)
- [x] Ergebnis-Parsing der Gemini-Nano-Antwort in ein Datenmodell
- [x] Test-Button „Nährwerttabelle scannen" im SettingsScreen hinzufügen
- [x] Ergebnisansicht (Composable) für erkannte Nährwerte erstellen
- [x] Fehlerbehandlung: Modell nicht verfügbar, Erkennung fehlgeschlagen
- [x] Strings in `strings.xml` ergänzen
- [ ] Manuelle Tests auf Pixel 10 mit echten Nährwerttabellen
