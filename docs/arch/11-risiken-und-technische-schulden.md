# 11. Risiken und Technische Schulden

## Risiken

### R-01: Widget-Update-Limits

**Risiko:** Android begrenzt die Häufigkeit von Widget-Updates. Bei häufigem Tracking könnten Updates verzögert angezeigt werden.

**Eintrittswahrscheinlichkeit:** Mittel

**Auswirkung:** Widget zeigt veraltete kcal-Werte an.

**Maßnahmen:**
- Explizites Widget-Update nach jedem Eintrag triggern (nicht nur auf Timer basieren)
- Nutzer über mögliche Verzögerungen informieren

### R-02: Quick-Add Overlay – OEM-Unterschiede

**Risiko:** Das Quick-Add Overlay (als Activity-Dialog oder System-Overlay) kann sich auf verschiedenen Android-OEMs unterschiedlich verhalten (Samsung, Xiaomi, Huawei haben eigene UI-Anpassungen).

**Eintrittswahrscheinlichkeit:** Mittel

**Auswirkung:** Overlay wird auf manchen Geräten anders dargestellt oder blockiert.

**Maßnahmen:**
- Standard-Activity mit Dialog-Theme verwenden statt System-Overlay
- Auf mehreren Geräten/Emulatoren testen

### R-03: Room Schema-Migration

**Risiko:** Bei Änderungen an Entities muss das Datenbankschema migriert werden. Fehlende oder fehlerhafte Migrationen führen zu Datenverlust oder App-Crashes.

**Eintrittswahrscheinlichkeit:** Hoch (bei Weiterentwicklung)

**Auswirkung:** Datenverlust oder App-Absturz nach Update.

**Maßnahmen:**
- Migrationen immer definieren, nie `fallbackToDestructiveMigration` verwenden
- Migrationen mit Instrumented Tests verifizieren
- Schema-Export aktivieren für Vergleich

### R-04: Kein Backup / Export

**Risiko:** Bei Geräteverlust oder Deinstallation sind alle Daten verloren.

**Eintrittswahrscheinlichkeit:** Niedrig (im täglichen Betrieb), Hoch (langfristig)

**Auswirkung:** Kompletter Datenverlust.

**Maßnahmen:**
- Backup/Export-Funktion für späteres Release geplant
- Android Auto-Backup ist als temporäre Maßnahme aktiv (Standard-Verhalten)

## Technische Schulden

### TS-01: Single Module

**Beschreibung:** Die gesamte App ist in einem Modul. Bei wachsender Codebasis wird die Build-Zeit steigen und die Modulgrenzen fehlen.

**Geplante Lösung:** Bei Bedarf in `:data`, `:domain`, `:ui`-Module aufsplitten.

### TS-02: Kein DI-Framework

**Beschreibung:** Manuelle DI in der Application-Klasse skaliert nicht unbegrenzt.

**Geplante Lösung:** Bei wachsender Komplexität Koin oder Hilt evaluieren.

### TS-03: Keine Internationalisierung

**Beschreibung:** App ist nur auf Deutsch. Strings sind in `strings.xml`, aber nur eine Sprache.

**Geplante Lösung:** Bei Bedarf englische Übersetzung hinzufügen.
