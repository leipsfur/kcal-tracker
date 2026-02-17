# 8. Querschnittliche Konzepte

## Persistenz

- **Room** als einzige Persistenzschicht
- Alle Datenobjekte sind als Room-Entities modelliert; je nach Fall mit auto-generierter ID oder fachlichem Schlüssel (z. B. `BmrPeriod.startDate`)
- DAOs liefern `Flow<T>` für reaktive Aktualisierung
- Grundumsatz wird als Zeitreihe in `BmrPeriod(startDate, bmr)` gespeichert
- `UserSettings` bleibt Singleton-Entity (feste ID = 1) für sonstige Einstellungen
- Schema-Migrationen bei Änderungen definieren (keine `fallbackToDestructiveMigration`)
- Initiale Kategorien werden über `RoomDatabase.Callback.onCreate()` eingefügt

## UI-Konsistenz

- **Material 3** Komponenten durchgängig verwenden
- **Dynamic Colors** (Material You) ab Android 12, Fallback-Theme für SDK 29–31
- **Dark Mode** folgt System-Einstellung
- Einheitliches Layout-Pattern: TopAppBar + Content + optional FAB
- Bestätigungsdialoge für destruktive Aktionen (Löschen)
- Eingabevalidierung mit visueller Rückmeldung (Error-State auf Textfeldern)
- Leere Zustände mit Hinweistext statt leerer Fläche

## Fehlerbehandlung

- **Validierung** vor dem Speichern: kcal > 0, Name nicht leer, Gewicht im realistischen Bereich
- **Keine Netzwerkfehler** (offline-only App)
- **Datenbankfehler**: Room wirft Compile-Time-Fehler bei Query-Problemen; Runtime-Fehler sollten durch korrektes Schema vermieden werden
- **Kein Crash bei fehlenden Einstellungen**: Dashboard zeigt Hinweis wenn Grundumsatz nicht gesetzt

## Testbarkeit

- **ViewModels** sind unabhängig von Android-Framework testbar (Unit Tests)
- **Repositories** können gemockt werden für ViewModel-Tests
- **DAOs** testbar über In-Memory Room Database (Instrumented Tests)
- **Composables** testbar über Compose Testing Framework
- Separation of Concerns durch MVVM + Repository ermöglicht isolierte Tests

## Datumslogik

- **Tageswechsel um 00:00 Uhr** (Mitternacht, Systemzeitzone)
- `LocalDate` als Datumstyp für Einträge
- Room TypeConverter für `LocalDate` ↔ `Long` (Epoch Day)
- Dashboard hat ein **selektiertes Datum** (Standard: `LocalDate.now()`)
- Queries filtern nach `date = :selectedDate`
- Nutzer kann über Datumspfeile zu vergangenen Tagen navigieren
- Einträge für vergangene Tage können nachträglich hinzugefügt, bearbeitet und gelöscht werden
- **Keine Einträge für zukünftige Tage** erlaubt (Validierung im ViewModel)

## Grundumsatz-Periodenlogik

- Grundumsatz wird als offene Intervalle modelliert: gültig ab `startDate` bis zur nächsten Periode
- Berechnungsregel: `bmrForDate(d) = bmr` der Periode mit größtem `startDate <= d`
- Fallback für frühe Tage: wenn `d` vor der ersten Periode liegt, gilt die früheste bekannte Periode rückwirkend
- Alle Bilanzberechnungen (Dashboard, Widget, Auswertungen) verwenden dieselbe Regel
- Eine spätere Änderung erzeugt eine neue Periode und verändert vergangene Tagesbilanzen nicht rückwirkend

## Portionsberechnung

- Vorlagen definieren Nährwerte pro Portionsgröße (z. B. 100g)
- Bei Erfassung wird die Menge als Multiplikator verwendet
- Berechnung: `kcal_eintrag = vorlage.kcal * (menge / vorlage.portionSize)`
- Gleiches Prinzip für Makros (Protein, Kohlenhydrate, Fett)

## Portionseinheiten

- Vordefinierte Liste gängiger Einheiten: **g**, **ml**, **Stück**, **Scheibe**, **Portion**, **EL** (Esslöffel), **TL** (Teelöffel)
- Wird in `FoodTemplate.portionUnit` als String gespeichert
- Auswahl über Dropdown bei Vorlagenerstellung (kein Freitext)
- Liste ist im Code als Konstante definiert (nicht in DB), Erweiterung durch App-Update

## Negativer Kalorienstand

- Wenn `Übrig < 0` (Aufnahme übersteigt TDEE), wird der Wert **rot** dargestellt
- Zusätzlich zum Farbwechsel wird ein **Minus-Zeichen** angezeigt (Barrierefreiheit: Farbe allein reicht nicht)
- Kein Blocking oder Warndialog – der Nutzer wird informiert, nicht bevormundet
- Im Widget wird bei negativem Stand ebenfalls der Minus-Wert angezeigt

## Barrierefreiheit

- Alle Icons und interaktiven Elemente erhalten eine `contentDescription`
- Mindest-Touch-Target: 48dp (Material 3 Standard)
- Farbkontraste nach WCAG AA (4.5:1 für normalen Text, 3:1 für großen Text)
- Informationen werden nie ausschließlich über Farbe vermittelt
- Eingabefelder verwenden sichtbare Labels (nicht nur Placeholder/Hint)
- TalkBack-kompatible Navigation durch semantische Compose-Modifier
