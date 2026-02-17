# 2. Randbedingungen

## Technische Randbedingungen

| Randbedingung | Erläuterung |
|---------------|-------------|
| Kotlin 2.0.21 | Projektsprache, kein Java |
| Jetpack Compose | UI-Framework, keine XML-Layouts |
| Material 3 / Material You | Design-System mit Dynamic Colors |
| Room (KSP) | Lokale Datenbank, Compile-Time-Verifikation |
| Jetpack Navigation Compose | Screen-Navigation |
| Glance (Jetpack) | Homescreen-Widget |
| Min SDK 29 | Android 10 als Mindestversion |
| Target SDK 36 | Aktuelle Android-Version |
| AGP 9.0.1 | Android Gradle Plugin |
| Single Module | Kein Multi-Module-Setup im ersten Release |

## Organisatorische Randbedingungen

| Randbedingung | Erläuterung |
|---------------|-------------|
| Einzelprojekt | Ein Entwickler, kein Team |
| Offline-only | Kein Backend, kein Cloud-Service, keine API-Anbindung |
| Kein CI/CD | Lokaler Build, kein automatisiertes Deployment |
| Deutsch | UI-Sprache und Dokumentation auf Deutsch |

## Konventionen

| Konvention | Erläuterung |
|-----------|-------------|
| MVVM | Model-View-ViewModel als Architektur-Pattern |
| Repository-Pattern | Abstrahiert den Datenzugriff für ViewModels |
| Manuelle DI | Keine DI-Frameworks (Hilt, Koin), stattdessen manuelle Instanziierung in Application-Klasse |
| StateFlow | UI-State wird über StateFlow an Composables geliefert |
| Kotlin Flow | DAOs liefern Flow<T> für reaktive Datenaktualisierung |
| Version Catalog | Dependencies in `gradle/libs.versions.toml` |
