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
| `SettingsScreen` | `ui/settings/` | Grundumsatz eingeben |

### ViewModel Layer

| ViewModel | Verantwortung |
|-----------|--------------|
| `DashboardViewModel` | TDEE-Berechnung, Tageseinträge laden, Kategorien gruppieren |
| `FoodViewModel` | Vorlagen-CRUD, Einträge erfassen/löschen, Kategorien verwalten |
| `ActivityViewModel` | Vorlagen-CRUD, Einträge erfassen/löschen, Kategorien verwalten |
| `WeightViewModel` | Gewicht erfassen/löschen, Verlaufsdaten laden |
| `SettingsViewModel` | Grundumsatz laden/speichern |

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
| `UserSettings` | Benutzereinstellungen (Grundumsatz) |

### Data Layer – DAOs

| DAO | Wichtige Queries |
|-----|-----------------|
| `FoodTemplateDao` | CRUD, nach Kategorie gruppiert, Suche nach Name |
| `FoodEntryDao` | Einträge nach Datum, Summe kcal nach Datum, nach Kategorie gruppiert |
| `ActivityTemplateDao` | CRUD, nach Kategorie gruppiert, Suche nach Name |
| `ActivityEntryDao` | Einträge nach Datum, Summe kcal nach Datum |
| `WeightEntryDao` | CRUD, alle Einträge sortiert nach Datum, Eintrag nach Datum |
| `FoodCategoryDao` | CRUD, sortiert nach sortOrder |
| `ActivityCategoryDao` | CRUD, sortiert nach sortOrder |
| `UserSettingsDao` | Get/Update Singleton |

### Data Layer – Repositories

| Repository | Verantwortung |
|-----------|--------------|
| `FoodRepository` | Vorlagen + Einträge + Kategorien für Essen |
| `ActivityRepository` | Vorlagen + Einträge + Kategorien für Aktivitäten |
| `WeightRepository` | Gewichtseinträge |
| `SettingsRepository` | Benutzereinstellungen (Grundumsatz) |
