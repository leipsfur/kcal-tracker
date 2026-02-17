# 5. Bausteinsicht

## Ebene 1: Schichten

```
┌─────────────────────────────────────────┐
│              UI Layer                    │
│  (Screens, Composables, Navigation)     │
├─────────────────────────────────────────┤
│           ViewModel Layer               │
│  (ViewModels, UiState, Business Logic)  │
├─────────────────────────────────────────┤
│          Repository Layer               │
│  (Repositories, Datenabstraktion)       │
├─────────────────────────────────────────┤
│            Data Layer                   │
│  (Room DB, Entities, DAOs)              │
└─────────────────────────────────────────┘
```

| Schicht | Verantwortung | Abhängigkeiten |
|---------|--------------|----------------|
| UI Layer | Darstellung, Nutzereingaben, Navigation | ViewModel Layer |
| ViewModel Layer | UI-State-Management, Geschäftslogik, Berechnungen | Repository Layer |
| Repository Layer | Datenzugriff abstrahieren, Daten transformieren | Data Layer |
| Data Layer | Persistierung, SQL-Queries, Schema-Definition | Room/SQLite |

## Ebene 2: Bausteine im Detail

### UI Layer – Screens

| Screen | Datei | Beschreibung |
|--------|-------|-------------|
| `DashboardScreen` | `ui/dashboard/` | Tagesbilanz, gruppierte Einträge |
| `FoodScreen` | `ui/food/` | Essen erfassen, Vorlagen verwalten |
| `ActivityScreen` | `ui/activity/` | Aktivitäten erfassen, Vorlagen verwalten |
| `WeightScreen` | `ui/weight/` | Gewicht erfassen, Diagramm, Liste |
| `SettingsScreen` | `ui/settings/` | Grundumsatz-Perioden erfassen und ändern |

### ViewModel Layer

| ViewModel | Verantwortung |
|-----------|--------------|
| `DashboardViewModel` | Datumsnavigation, TDEE-Berechnung, Tageseinträge laden, Kategorien gruppieren |
| `FoodViewModel` | Vorlagen-CRUD, Einträge erfassen/bearbeiten/löschen, Kategorien verwalten |
| `ActivityViewModel` | Vorlagen-CRUD, Einträge erfassen/bearbeiten/löschen, Kategorien verwalten |
| `WeightViewModel` | Gewicht erfassen/löschen, Verlaufsdaten laden |
| `SettingsViewModel` | Grundumsatz-Perioden laden/speichern, Startdatum-Validierung |

### Data Layer – Entities

| Entity | Beschreibung |
|--------|-------------|
| `FoodTemplate` | Lebensmittel-Vorlage (Name, kcal, Kategorie, Makros, Portion) |
| `FoodEntry` | Tageseintrag Essen (Datum, Name, kcal, Menge, Kategorie) |
| `ActivityTemplate` | Aktivitäts-Vorlage (Name, kcal, Kategorie) |
| `ActivityEntry` | Tageseintrag Aktivität (Datum, Name, kcal, Kategorie) |
| `WeightEntry` | Gewichtseintrag (Datum, Gewicht in kg) |
| `FoodCategory` | Essen-Kategorie (Name, Sortierreihenfolge) |
| `ActivityCategory` | Aktivitäts-Kategorie (Name, Sortierreihenfolge) |
| `BmrPeriod` | Grundumsatz-Periode (`startDate`, `bmr`) |
| `UserSettings` | Benutzereinstellungen (app-weite Defaults, ohne BMR-Historie) |

### Data Layer – DAOs

| DAO | Wichtige Queries |
|-----|-----------------|
| `FoodTemplateDao` | CRUD, nach Kategorie gruppiert, Suche nach Name |
| `FoodEntryDao` | CRUD, Einträge nach Datum, Summe kcal nach Datum, nach Kategorie gruppiert |
| `ActivityTemplateDao` | CRUD, nach Kategorie gruppiert, Suche nach Name |
| `ActivityEntryDao` | CRUD, Einträge nach Datum, Summe kcal nach Datum |
| `WeightEntryDao` | CRUD, alle Einträge sortiert nach Datum, Eintrag nach Datum |
| `FoodCategoryDao` | CRUD, sortiert nach sortOrder |
| `ActivityCategoryDao` | CRUD, sortiert nach sortOrder |
| `BmrPeriodDao` | Upsert Periode nach `startDate`, gültige Periode für Datum (`startDate <= :date`), früheste Periode |
| `UserSettingsDao` | Get/Update Singleton für sonstige Einstellungen |

### Data Layer – Repositories

| Repository | Verantwortung |
|-----------|--------------|
| `FoodRepository` | Vorlagen + Einträge + Kategorien für Essen |
| `ActivityRepository` | Vorlagen + Einträge + Kategorien für Aktivitäten |
| `WeightRepository` | Gewichtseinträge |
| `SettingsRepository` | Benutzereinstellungen + BMR-Perioden (`upsertBmrPeriod`, `getBmrForDate`) |
