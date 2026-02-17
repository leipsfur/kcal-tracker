# 10. Qualitätsanforderungen

## Qualitätsbaum

```
Qualität
├── Bedienbarkeit
│   ├── Schnelle Erfassung (< 3 Taps für Vorlage)
│   ├── Quick-Add ohne App-Öffnung
│   ├── Übersichtliches Dashboard
│   └── Nachträgliche Korrektur vergangener Tage
├── Zuverlässigkeit
│   ├── Kein Datenverlust
│   ├── Konsistente Berechnungen
│   └── Korrekte Tageswechsel-Logik
├── Performance
│   ├── Flüssiges Scrollen (60 fps)
│   ├── Schneller App-Start (< 2s)
│   └── Widget-Update ohne Verzögerung
├── Barrierefreiheit
│   ├── Screen-Reader-Kompatibilität (TalkBack)
│   ├── Ausreichende Farbkontraste (WCAG AA)
│   └── Mindest-Touch-Targets (48dp)
└── Offline-Fähigkeit
    └── Vollständige Funktionalität ohne Internet
```

## Qualitätsszenarien

### QS-01: Essen schnell erfassen

| Aspekt | Beschreibung |
|--------|-------------|
| Stimulus | Nutzer möchte Essen tracken |
| Reaktion | Über Vorlage in maximal 3 Taps erfasst (Tab → Vorlage → Bestätigen) |
| Metrik | ≤ 3 Interaktionen für Vorlage mit Standard-Portion |

### QS-02: Quick-Add über Widget

| Aspekt | Beschreibung |
|--------|-------------|
| Stimulus | Nutzer tippt Widget auf Homescreen |
| Reaktion | Overlay öffnet sich, Vorlage auswählbar, Eintrag wird erstellt |
| Metrik | Erfassung ohne App-Öffnung möglich |

### QS-03: Kein Datenverlust bei App-Beendigung

| Aspekt | Beschreibung |
|--------|-------------|
| Stimulus | Nutzer beendet App oder System killt Process |
| Reaktion | Alle gespeicherten Einträge bleiben erhalten |
| Metrik | 0 Datenverluste bei normalem Betrieb |

### QS-04: Dashboard-Performance bei vielen Einträgen

| Aspekt | Beschreibung |
|--------|-------------|
| Stimulus | Dashboard mit 50+ Einträgen pro Tag |
| Reaktion | Flüssiges Scrollen ohne Ruckler |
| Metrik | ≥ 60 fps bei LazyColumn mit 50 Items |

### QS-05: Korrekte Tagesbilanz

| Aspekt | Beschreibung |
|--------|-------------|
| Stimulus | Nutzer hat Grundumsatz (2000), Aktivität (500), Aufnahme (1800) |
| Reaktion | TDEE = 2500, Übrig = 700 |
| Metrik | Berechnung ist mathematisch korrekt, keine Rundungsfehler |

### QS-06: Offline-Betrieb

| Aspekt | Beschreibung |
|--------|-------------|
| Stimulus | Gerät hat kein Internet |
| Reaktion | Alle Funktionen arbeiten uneingeschränkt |
| Metrik | 100% Feature-Verfügbarkeit offline |

### QS-07: Nachträgliche Korrektur

| Aspekt | Beschreibung |
|--------|-------------|
| Stimulus | Nutzer hat gestern vergessen, eine Mahlzeit zu tracken |
| Reaktion | Über Datumspfeile zum Vortag navigieren und Eintrag hinzufügen |
| Metrik | Vergangene Tage sind vollständig bearbeitbar (hinzufügen, bearbeiten, löschen) |

### QS-08: Barrierefreiheit

| Aspekt | Beschreibung |
|--------|-------------|
| Stimulus | Nutzer mit Seheinschränkung verwendet TalkBack |
| Reaktion | Alle Elemente werden korrekt vorgelesen, Navigation ist möglich |
| Metrik | Alle interaktiven Elemente haben contentDescription, Touch-Targets ≥ 48dp |
