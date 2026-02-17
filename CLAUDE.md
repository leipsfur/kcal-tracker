# KcalTrack – Projektübersicht

Kalorienzähler-App für Android. Offline-first, kein Backend.

## Build & Run

**WSL-Umgebung** (JAVA_HOME und ANDROID_HOME müssen gesetzt werden):

```bash
export JAVA_HOME=/usr/lib/jvm/java-25
export ANDROID_HOME=/mnt/c/Users/darky/AppData/Local/Android/Sdk

./gradlew assembleDebug          # Debug-APK bauen
./gradlew test                   # Unit Tests
./gradlew connectedAndroidTest   # Instrumented Tests
./gradlew lint                   # Lint-Prüfung
```

**Hinweis**: Der Gradle-Build unter WSL funktioniert **nicht**, da die Android SDK Build-Tools Windows-Binaries sind (`.exe`). Auch `compileDebugKotlin` schlägt fehl, weil AGP die Build-Tools bereits bei der Task-Resolution prüft. **Builds müssen in Android Studio oder PowerShell ausgeführt werden.** Die ai-factory kann unter WSL keinen Build verifizieren — stattdessen manuelle Code-Review durchführen.

## Tech Stack

| Komponente | Technologie |
|-----------|-------------|
| Sprache | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 (Dynamic Colors) |
| Architektur | Single Activity, MVVM |
| Datenbank | Room (KSP) |
| Navigation | Jetpack Navigation Compose |
| Widget | Glance (Jetpack) |
| Async | Kotlin Coroutines + Flow |
| DI | Manuelle Dependency Injection (kein Framework) |
| Min SDK | 29 (Android 10) |
| Target SDK | 36 |
| Build | AGP 9.0.1, Version Catalog (`libs.versions.toml`) |

## Namespace

```
de.leipsfur.kcal_track
```

## Paketstruktur (geplant)

```
de.leipsfur.kcal_track/
├── data/
│   ├── db/              # Room Database, Entities, DAOs
│   └── repository/      # Repository-Implementierungen
├── domain/
│   └── model/           # Domain-Modelle (falls abweichend von Entities)
├── ui/
│   ├── dashboard/       # Dashboard-Screen + ViewModel
│   ├── food/            # Essen-Tracking Screen + ViewModel
│   ├── activity/        # Aktivitäts-Tracking Screen + ViewModel
│   ├── weight/          # Gewichts-Tracking Screen + ViewModel
│   ├── settings/        # Einstellungen Screen + ViewModel
│   ├── navigation/      # NavHost, Bottom Navigation
│   └── theme/           # Material You Theme, Colors
├── widget/              # Glance Widget + Quick-Add Overlay
└── KcalTrackApplication.kt
```

## Architektur-Pattern

**MVVM mit Repository-Pattern:**

```
Screen (Composable) → ViewModel → Repository → DAO → Room DB
```

- Screens beobachten UI-State via `StateFlow`
- ViewModels halten keinen Compose-State, nur `StateFlow`/`SharedFlow`
- Repositories abstrahieren Datenzugriff
- DAOs definieren SQL-Queries, geben `Flow<T>` zurück

## Domain-Formeln

```
TDEE = Grundumsatz + Σ(Aktivitäten des selektierten Tages)
Übrig = TDEE - Σ(Aufnahme des selektierten Tages)
```

- **Grundumsatz**: Manuell eingegeben (in kcal)
- **Aktivität**: Einzeleinträge mit kcal-Wert
- **Aufnahme**: Einzeleinträge mit kcal-Wert, optional mit Makros
- **Tageswechsel**: 00:00 Uhr (Mitternacht)
- **Selektiertes Datum**: Dashboard zeigt Standard "heute", navigierbar zu vergangenen Tagen
- **Negativer Stand**: `Übrig < 0` wird rot + mit Minus-Zeichen dargestellt

## Portionseinheiten

Vordefinierte Liste (kein Freitext): `g`, `ml`, `Stück`, `Scheibe`, `Portion`, `EL`, `TL`

## Room-Entities

| Entity | Felder |
|--------|--------|
| `FoodTemplate` | id, name, kcal, category, protein?, carbs?, fat?, portionSize, portionUnit |
| `FoodEntry` | id, date, templateId?, name, kcal, protein?, carbs?, fat?, amount, category |
| `ActivityTemplate` | id, name, kcal, category |
| `ActivityEntry` | id, date, templateId?, name, kcal, category |
| `WeightEntry` | id, date, weightKg |
| `FoodCategory` | id, name, sortOrder |
| `ActivityCategory` | id, name, sortOrder |
| `UserSettings` | id (singleton), bmr |

## Coding Conventions

- **Sprache**: Kotlin, kein Java
- **UI**: Nur Compose, keine XML-Layouts
- **Formatting**: Standard Kotlin Style (ktlint-kompatibel)
- **Naming**: camelCase für Variablen/Funktionen, PascalCase für Klassen/Composables
- **Composables**: Eigene Datei pro Screen, Präfix `Screen` für Top-Level-Screens (z. B. `DashboardScreen`)
- **ViewModels**: Suffix `ViewModel` (z. B. `DashboardViewModel`)
- **DAOs**: Suffix `Dao` (z. B. `FoodEntryDao`)
- **State**: `data class XyzUiState(...)` pro Screen
- **Strings**: In `strings.xml` (Deutsch als Standardsprache)
- **Keine Abkürzungen** in öffentlichen APIs außer `kcal`, `bmr`, `id`

## Dokumentation

- User Stories: `docs/stories/`
- Architektur (arc42): `docs/arch/`
- Implementierungs-Todos: `docs/todo/todo.md`
