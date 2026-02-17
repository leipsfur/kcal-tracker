# 4. Lösungsstrategie

## Architekturansatz

| Entscheidung | Lösung | Begründung |
|-------------|--------|-----------|
| App-Architektur | Single Activity + Jetpack Navigation | Standard für Compose-Apps, einfache Navigation |
| UI-Pattern | MVVM | Klare Trennung von UI und Logik, testbare ViewModels |
| Datenzugriff | Repository-Pattern | Abstrahiert DB-Zugriff, ermöglicht spätere Änderungen |
| Datenbank | Room | Bewährt, Compile-Time-Safety, Flow-Support |
| Reaktivität | Kotlin Flow + StateFlow | Automatische UI-Aktualisierung bei Datenänderungen |
| Widget | Glance (Jetpack) | Compose-basierte Widget-API, konsistentes Entwicklungsmodell |
| DI | Manuelle Injection | Kein Framework-Overhead, ausreichend für Einzelmodul-App |
| Design | Material 3 / Material You | Native Android-Optik, Dynamic Colors |

## Datenfluss

```
User Input → Composable → ViewModel → Repository → DAO → Room DB
                                                          ↓
User sees  ← Composable ← StateFlow ← Repository ← Flow<T>
```

1. Nutzer interagiert mit Composable-UI
2. Composable ruft ViewModel-Funktionen auf
3. ViewModel delegiert an Repository
4. Repository ruft DAO-Methoden auf
5. DAO schreibt/liest aus Room DB
6. Änderungen fließen über `Flow<T>` zurück zum Repository
7. Repository/ViewModel transformiert zu `StateFlow<UiState>`
8. Composable recomposed automatisch bei State-Änderungen

## Schlüsselentscheidungen

- **Kein Backend**: Alle Daten lokal → einfacher, schneller, offline
- **Kein DI-Framework**: Manuelle DI in Application-Klasse → weniger Magie, weniger Dependencies
- **Single Module**: Kein Multi-Module-Setup → einfacherer Build, ausreichend für Projektgröße
- **Dynamisches TDEE**: `Grundumsatz + Aktivitäten` statt fixem Ziel → flexibler für aktive Tage
- **Vorlagen-System**: Häufige Einträge als Vorlagen → schnellere Erfassung
