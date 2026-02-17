# 9. Architekturentscheidungen

## ADR-01: Room als Datenbank

**Status:** Akzeptiert

**Kontext:** Die App benötigt eine lokale Datenbank für Einträge, Vorlagen, Kategorien und Einstellungen.

**Entscheidung:** Room (Jetpack) mit KSP-Annotation-Processing.

**Begründung:**
- Compile-Time-Verifikation von SQL-Queries
- Nativer Flow-Support für reaktive Daten
- De-facto Standard für Android-Apps
- TypeConverter-Support für LocalDate

**Alternativen verworfen:**
- SQLDelight: Guter SQL-First-Ansatz, aber weniger Android-Ökosystem-Integration
- Realm: Proprietär, komplexer für einfache Anwendungsfälle

## ADR-02: Jetpack Compose als UI-Framework

**Status:** Akzeptiert

**Kontext:** Die App benötigt ein UI-Framework für Android.

**Entscheidung:** Jetpack Compose mit Material 3.

**Begründung:**
- Deklaratives UI-Modell, weniger Boilerplate
- Native Material You / Dynamic Colors Unterstützung
- Bessere Testbarkeit als XML-Views
- Zukunftssichere Investition (Google-empfohlen)

## ADR-03: Kein DI-Framework

**Status:** Akzeptiert

**Kontext:** Dependency Injection wird benötigt für Repository- und ViewModel-Erstellung.

**Entscheidung:** Manuelle DI über Application-Klasse.

**Begründung:**
- App hat nur ein Modul und überschaubare Dependency-Graphen
- Kein Framework-Overhead (Hilt: Kapt/KSP, Code-Generierung, Annotations)
- Einfacher zu verstehen und zu debuggen
- Bei wachsender Komplexität kann später ein Framework eingeführt werden

**Konsequenz:** Database und Repositories werden in `KcalTrackApplication` instanziiert und über Properties bereitgestellt.

## ADR-04: Glance für Widget

**Status:** Akzeptiert

**Kontext:** Die App benötigt ein Homescreen-Widget mit Kalorien-Anzeige und Quick-Add.

**Entscheidung:** Jetpack Glance.

**Begründung:**
- Compose-ähnliche API für Widgets
- Konsistentes Entwicklungsmodell (Compose für App, Glance für Widget)
- Aktiv von Google gepflegt

**Risiken:**
- Glance hat Einschränkungen bei der UI-Komplexität
- Widget-Update-Frequenz ist vom System begrenzt

## ADR-05: Single Module

**Status:** Akzeptiert

**Kontext:** Multi-Module-Architektur vs. Single Module.

**Entscheidung:** Single Module (`:app`).

**Begründung:**
- Einfacherer Build-Prozess
- Kein Overhead für Modul-Grenzen und API-Definitionen
- Ausreichend für die aktuelle Projektgröße
- Paketstruktur bietet genug Separation

**Konsequenz:** Bei signifikantem Wachstum könnte eine Aufspaltung in `:data`, `:domain`, `:ui`-Module sinnvoll werden.
