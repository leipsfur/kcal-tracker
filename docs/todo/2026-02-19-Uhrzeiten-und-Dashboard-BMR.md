# Uhrzeiten für Essen-Einträge & Dashboard-BMR (US-0210, US-0105, US-0106)

Dokumentation: [docs/stories/02-essen-tracking.md](../stories/02-essen-tracking.md), [docs/stories/01-dashboard.md](../stories/01-dashboard.md)

- [ ] Room-Migration: `FoodEntry` um Spalte `time` (`TEXT NOT NULL DEFAULT '12:00'`) erweitern
- [ ] `FoodEntryDao`: Sortierung nach Uhrzeit innerhalb Kategorie ergänzen
- [ ] `FoodRepository`: Uhrzeit-Feld in Insert-/Update-Methoden berücksichtigen
- [ ] `FoodViewModel`: Uhrzeitfeld in UI-State, aktuelle Uhrzeit als Default
- [ ] `FoodScreen`: Material 3 TimePicker im Erfassungs- und Bearbeitungsdialog
- [ ] `DashboardViewModel`: Einträge innerhalb Kategorie nach Uhrzeit sortieren
- [ ] `DashboardScreen`: Uhrzeit neben Eintragsnamen anzeigen
- [ ] `DashboardViewModel`: Funktion zum Erstellen/Aktualisieren einer BMR-Periode für selektiertes Datum
- [ ] `DashboardScreen`: Tippbarer Grundumsatz-Wert / Bearbeiten-Button + BMR-Dialog
- [ ] `strings.xml`: Neue Strings für Uhrzeit-Label und BMR-Dialog
