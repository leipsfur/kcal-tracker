# 8. Querschnittliche Konzepte

## Persistenz

- **Room** als einzige Persistenzschicht
- Alle Entities mit `@Entity`-Annotation, auto-generierte IDs (`@PrimaryKey(autoGenerate = true)`)
- DAOs liefern `Flow<T>` für reaktive Aktualisierung
- `UserSettings` als Singleton-Entity (feste ID = 1)
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
- Queries filtern nach `date = :today` mit `LocalDate.now()`
- Kein manuelles Datum-Setzen durch den Nutzer (Einträge werden immer "heute" zugeordnet)

## Portionsberechnung

- Vorlagen definieren Nährwerte pro Portionsgröße (z. B. 100g)
- Bei Erfassung wird die Menge als Multiplikator verwendet
- Berechnung: `kcal_eintrag = vorlage.kcal * (menge / vorlage.portionSize)`
- Gleiches Prinzip für Makros (Protein, Kohlenhydrate, Fett)
