# Implementierungs-Todos

## Phase 1: Projektstruktur

- [x] Room-Dependency in `libs.versions.toml` und `build.gradle.kts` hinzufügen
- [x] KSP-Plugin für Room hinzufügen
- [x] Navigation Compose Dependency hinzufügen
- [x] Glance (Widget) Dependency hinzufügen
- [x] Paketstruktur anlegen (`data/db`, `data/repository`, `ui/*`, `widget`)
- [x] `KcalTrackApplication`-Klasse erstellen und in `AndroidManifest.xml` registrieren
- [x] Theme-Setup prüfen (Dynamic Colors, Dark Mode Fallback)

## Phase 2: Datenschicht

- [x] Room Entities erstellen: `FoodTemplate`, `FoodEntry`, `ActivityTemplate`, `ActivityEntry`, `WeightEntry`, `FoodCategory`, `ActivityCategory`, `UserSettings` <!-- verified by ai-factory, iteration 2 -->
- [x] DAOs erstellen: `FoodTemplateDao`, `FoodEntryDao`, `ActivityTemplateDao`, `ActivityEntryDao`, `WeightEntryDao`, `FoodCategoryDao`, `ActivityCategoryDao`, `UserSettingsDao` <!-- verified by ai-factory, iteration 2 -->
- [x] `KcalTrackDatabase` erstellen (RoomDatabase) <!-- verified by ai-factory, iteration 2 -->
- [x] Initiale Kategorien per `RoomDatabase.Callback` einfügen (Essen: Frühstück, Mittagessen, Abendessen, Snack, Alkohol; Aktivität: Cardio, Krafttraining, Alltag) <!-- verified by ai-factory, iteration 2 -->
- [x] Repositories erstellen: `FoodRepository`, `ActivityRepository`, `WeightRepository`, `SettingsRepository` <!-- verified by ai-factory, iteration 2 -->
- [x] Manuelle DI: Database- und Repository-Instanzen in `KcalTrackApplication` bereitstellen <!-- verified by ai-factory, iteration 2 -->

## Phase 2b: Datenschicht-Ergänzungen (neue Stories)

- [x] `update()`-Methode zu `FoodEntryDao` hinzufügen (für US-0208: Eintrag bearbeiten) <!-- verified by ai-factory, iteration 3 -->
- [x] `update()`-Methode zu `ActivityEntryDao` hinzufügen (für US-0308: Eintrag bearbeiten) <!-- verified by ai-factory, iteration 3 -->
- [x] `updateEntry()`-Methode zu `FoodRepository` hinzufügen <!-- verified by ai-factory, iteration 3 -->
- [x] `updateEntry()`-Methode zu `ActivityRepository` hinzufügen <!-- verified by ai-factory, iteration 3 -->
- [x] Portionseinheiten als Konstanten-Liste definieren (g, ml, Stück, Scheibe, Portion, EL, TL) <!-- verified by ai-factory, iteration 3 -->

## Phase 3: Navigation

- [x] Bottom Navigation Bar implementieren (5 Tabs: Dashboard, Essen, Aktivität, Gewicht, Einstellungen) <!-- verified by ai-factory, iteration 1 -->
- [x] `NavHost` mit Routes für alle Screens einrichten <!-- verified by ai-factory, iteration 1 -->
- [x] Placeholder-Screens für alle 5 Tabs erstellen <!-- verified by ai-factory, iteration 1 -->
- [x] Icons und Labels für Navigation Tabs definieren <!-- verified by ai-factory, iteration 1 -->

## Phase 4: Einstellungen (US-0501, US-0502)

- [x] `SettingsViewModel` erstellen <!-- verified by ai-factory, iteration 4 -->
- [x] `SettingsScreen` implementieren: Grundumsatz-Eingabe mit Validierung <!-- verified by ai-factory, iteration 4 -->
- [x] Grundumsatz in `UserSettings`-Entity speichern/laden <!-- verified by ai-factory, iteration 4 -->

## Phase 5: Essen-Tracking (US-0201–US-0208)

- [x] `FoodViewModel` erstellen <!-- verified by ai-factory, iteration 1 -->
- [x] Essen-Screen: Tab/Bereich für Vorlagen-Verwaltung <!-- verified by ai-factory, iteration 1 -->
- [x] Lebensmittel-Vorlage erstellen (Dialog mit Portionseinheit-Dropdown und Validierung) <!-- verified by ai-factory, iteration 1 -->
- [x] Lebensmittel-Vorlage bearbeiten <!-- verified by ai-factory, iteration 1 -->
- [x] Lebensmittel-Vorlage löschen (mit Bestätigungsdialog) <!-- verified by ai-factory, iteration 1 -->
- [x] Essen erfassen über Vorlage (Bottom Sheet: Vorlagenauswahl, Mengenanpassung, kcal-Berechnung) <!-- verified by ai-factory, iteration 1 -->
- [x] Essen manuell erfassen (Dialog mit Validierung) <!-- verified by ai-factory, iteration 1 -->
- [x] Essen-Eintrag bearbeiten (Dialog, vorausgefüllt mit bestehenden Werten) <!-- verified by ai-factory, iteration 1 -->
- [x] Essen-Eintrag löschen (mit Bestätigungsdialog) <!-- verified by ai-factory, iteration 1 -->
- [x] Essen-Kategorien verwalten (Unter-Screen: hinzufügen, umbenennen, löschen, sortieren) <!-- verified by ai-factory, iteration 1 -->

## Phase 6: Aktivitäts-Tracking (US-0301–US-0308)

- [x] `ActivityViewModel` erstellen <!-- verified by ai-factory, iteration 5 -->
- [x] Aktivitäts-Screen: Tab/Bereich für Vorlagen-Verwaltung <!-- verified by ai-factory, iteration 5 -->
- [x] Aktivitäts-Vorlage erstellen (Dialog mit Validierung) <!-- verified by ai-factory, iteration 5 -->
- [x] Aktivitäts-Vorlage bearbeiten <!-- verified by ai-factory, iteration 5 -->
- [x] Aktivitäts-Vorlage löschen (mit Bestätigungsdialog) <!-- verified by ai-factory, iteration 5 -->
- [x] Aktivität erfassen über Vorlage (Bottom Sheet: Vorlagenauswahl, kcal anpassbar) <!-- verified by ai-factory, iteration 5 -->
- [x] Aktivität manuell erfassen (Dialog mit Validierung) <!-- verified by ai-factory, iteration 5 -->
- [x] Aktivitäts-Eintrag bearbeiten (Dialog, vorausgefüllt mit bestehenden Werten) <!-- verified by ai-factory, iteration 5 -->
- [x] Aktivitäts-Eintrag löschen (mit Bestätigungsdialog) <!-- verified by ai-factory, iteration 5 -->
- [x] Aktivitäts-Kategorien verwalten (Unter-Screen: hinzufügen, umbenennen, löschen, sortieren) <!-- verified by ai-factory, iteration 5 -->

## Phase 7: Dashboard (US-0101–US-0104)

- [x] `DashboardViewModel` erstellen (mit selektiertem Datum, Standard: heute)
- [x] Datumsnavigation: Pfeile vor/zurück + "Heute"-Button
- [x] TDEE-Berechnung: Grundumsatz + Σ Aktivitäten des selektierten Tages
- [x] Verbleibende kcal berechnen: TDEE - Σ Aufnahme des selektierten Tages
- [x] Negativer Kalorienstand: Rote Darstellung + Minus-Zeichen
- [x] Dashboard-Screen: TDEE, Aufnahme und Übrig anzeigen
- [x] Tageseinträge nach Kategorie gruppiert anzeigen (mit Zwischensummen)
- [x] Hinweis anzeigen wenn kein Grundumsatz eingestellt
- [x] Validierung: Keine Navigation in die Zukunft

## Phase 8: Gewichts-Tracking (US-0401–US-0404)

- [x] `WeightViewModel` erstellen
- [x] Gewicht-erfassen-Formular (kg, eine Dezimalstelle, Validierung)
- [x] Ein Eintrag pro Tag (überschreiben bei erneutem Eintrag)
- [x] Chronologische Liste mit Differenz zum vorherigen Eintrag
- [x] Liniendiagramm (Compose Canvas oder Bibliothek evaluieren)
- [x] Gewichts-Eintrag löschen (mit Bestätigungsdialog)

## Phase 9: Widget (US-0601, US-0602)

- [ ] Glance Widget erstellen: Verbleibende kcal anzeigen
- [ ] Widget-Update bei Datenänderungen triggern
- [ ] Quick-Add Overlay: Activity/Dialog mit Vorlagen-Liste
- [ ] Quick-Add: Portionsmenge abfragen und Eintrag erstellen
- [ ] Widget-Design nach Material You / Glance-Richtlinien

## Phase 10: Barrierefreiheit (US-0704)

- [ ] Content Descriptions für alle Icons und interaktiven Elemente
- [ ] Mindest-Touch-Targets (48dp) prüfen und sicherstellen
- [ ] Farbkontraste nach WCAG AA validieren
- [ ] Informationen nicht ausschließlich über Farbe vermitteln
- [ ] Eingabefelder mit sichtbaren Labels (nicht nur Placeholder)
- [ ] TalkBack-Navigation testen

## Phase 11: Qualitätssicherung

- [ ] Unit Tests für ViewModels (Berechnungslogik, Datumsnavigation)
- [ ] Unit Tests für Repositories
- [ ] Instrumented Tests für Room DAOs (inkl. update-Methoden)
- [ ] UI Tests für kritische Flows (Essen erfassen, Aktivität erfassen, Eintrag bearbeiten)
- [ ] Edge Cases testen: Leere Datenbank, kein Grundumsatz, Tageswechsel, negative Übrig-Werte
- [ ] Barrierefreiheit testen: TalkBack, Touch-Targets, Kontraste
- [ ] Performance: Große Datenmengen in Listen, Widget-Update-Frequenz
- [ ] Lint-Warnungen beheben
