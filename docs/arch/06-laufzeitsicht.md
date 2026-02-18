# 6. Laufzeitsicht

## Szenario 1: Essen über Vorlage erfassen

```
Nutzer          FoodScreen       FoodViewModel     FoodRepository    FoodEntryDao
  │                │                  │                  │                │
  │  Öffnet Essen  │                  │                  │                │
  │───────────────→│                  │                  │                │
  │                │  Vorlagen laden  │                  │                │
  │                │─────────────────→│  getTemplates()  │                │
  │                │                  │─────────────────→│                │
  │                │                  │  Flow<List<T>>   │                │
  │                │  UiState update  │←─────────────────│                │
  │  Vorlagenliste │←─────────────────│                  │                │
  │←───────────────│                  │                  │                │
  │                │                  │                  │                │
  │  Wählt Vorlage │                  │                  │                │
  │───────────────→│                  │                  │                │
  │                │                  │                  │                │
  │  Menge: 1.5    │                  │                  │                │
  │───────────────→│  addEntry(t,1.5) │                  │                │
  │                │─────────────────→│  insertEntry()   │                │
  │                │                  │─────────────────→│  insert(entry) │
  │                │                  │                  │───────────────→│
  │                │                  │                  │                │
  │  Bestätigung   │←─────────────────│                  │                │
  │←───────────────│                  │                  │                │
```

**Ablauf:**
1. Nutzer öffnet den Essen-Screen
2. ViewModel lädt Vorlagen über Repository (Flow)
3. Nutzer wählt eine Vorlage und passt die Menge an
4. ViewModel berechnet kcal proportional zur Menge
5. Entry wird über Repository → DAO in die DB geschrieben
6. Dashboard-ViewModel empfängt die Änderung über Flow und aktualisiert die Bilanz

## Szenario 2: Quick-Add über Widget

```
Nutzer        Widget(Glance)    QuickAddOverlay   FoodViewModel    FoodRepository
  │               │                   │                │                │
  │  Tippt Widget │                   │                │                │
  │──────────────→│                   │                │                │
  │               │  Intent starten   │                │                │
  │               │──────────────────→│                │                │
  │               │                   │  Vorlagen laden│                │
  │               │                   │───────────────→│                │
  │               │                   │  Vorlagenliste │                │
  │               │                   │←───────────────│                │
  │  Sieht Liste  │                   │                │                │
  │←──────────────│───────────────────│                │                │
  │               │                   │                │                │
  │  Wählt + Menge│                   │                │                │
  │──────────────→│──────────────────→│  addEntry()    │                │
  │               │                   │───────────────→│  insertEntry() │
  │               │                   │                │───────────────→│
  │               │                   │                │                │
  │               │  Widget Update    │                │                │
  │               │←──────────────────│                │                │
  │  Neuer Wert   │                   │                │                │
  │←──────────────│                   │                │                │
```

**Ablauf:**
1. Nutzer tippt auf das Widget
2. Widget startet Quick-Add Overlay (Activity/Dialog)
3. Overlay lädt Vorlagen und zeigt durchsuchbare Liste
4. Nutzer wählt Vorlage und bestätigt Menge
5. Eintrag wird in DB geschrieben
6. Widget wird aktualisiert mit neuem Rest-kcal-Wert

## Szenario 3: Tageswechsel

```
System          DashboardViewModel    FoodRepository    ActivityRepository
  │                    │                    │                    │
  │  00:00 Uhr         │                    │                    │
  │───────────────────→│                    │                    │
  │                    │  heute = LocalDate │                    │
  │                    │  .now() → neuer Tag│                    │
  │                    │                    │                    │
  │                    │  getEntries(heute)  │                    │
  │                    │───────────────────→│                    │
  │                    │  leere Liste       │                    │
  │                    │←───────────────────│                    │
  │                    │                    │                    │
  │                    │  getEntries(heute)  │                    │
  │                    │───────────────────→│───────────────────→│
  │                    │  leere Liste       │                    │
  │                    │←───────────────────│←───────────────────│
  │                    │                    │                    │
  │  Dashboard zeigt   │                    │                    │
  │  neuen leeren Tag  │                    │                    │
  │←───────────────────│                    │                    │
```

**Ablauf:**
1. Datum wechselt um Mitternacht
2. ViewModel verwendet `LocalDate.now()` als Filter
3. Queries liefern leere Listen für den neuen Tag
4. Dashboard zeigt TDEE = `bmrForDate(heute)` (keine Aktivitäten)
5. Daten des Vortags bleiben in der DB erhalten

## Szenario 4: Grundumsatz-Periode ändern ohne Rückwirkung

```
Nutzer        SettingsScreen     SettingsViewModel   SettingsRepository    BmrPeriodDao     DashboardViewModel
  │                │                    │                   │                   │                   │
  │ Neuer BMR +    │                    │                   │                   │                   │
  │ Startdatum     │                    │                   │                   │                   │
  │───────────────→│  savePeriod()      │                   │                   │                   │
  │                │───────────────────→│  upsertBmrPeriod()│                   │                   │
  │                │                    │──────────────────→│  upsertByDate()   │                   │
  │                │                    │                   │──────────────────→│                   │
  │                │                    │                   │                   │                   │
  │                │                    │                   │  getBmrForDate(d) │                   │
  │                │                    │                   │←──────────────────│                   │
  │                │                    │                   │                   │                   │
  │                │                    │                   │                   │  recompute(d)     │
  │                │                    │                   │───────────────────────────────────────→│
  │                │                    │                   │                   │                   │
  │  Historische   │                    │                   │                   │                   │
  │  Tage stabil   │←───────────────────│                   │                   │                   │
```

**Ablauf:**
1. Nutzer speichert einen neuen Grundumsatz mit Startdatum
2. Repository speichert die Periode als offenes Intervall ab `startDate`
3. Für ein Datum `d` wird immer `bmrForDate(d)` genutzt (größtes `startDate <= d`)
4. Für Tage vor der ersten Periode gilt die früheste bekannte Periode rückwirkend
5. Vergangene Tagesbilanzen bleiben bei späteren Änderungen fachlich stabil

## Szenario 5: Wischgeste wechselt Haupt-Tab

```
Nutzer          KcalTrackApp        HorizontalPager      Ziel-Screen
  │                  │                    │                    │
  │  Swipe links     │                    │                    │
  │─────────────────→│  Drag-Input        │                    │
  │                  │───────────────────→│  live offset       │
  │                  │                    │───────────────────→│
  │  hin-/herziehen  │                    │                    │
  │─────────────────→│───────────────────→│  ohne Route-Wechsel│
  │                  │                    │                    │
  │  Loslassen       │                    │                    │
  │─────────────────→│───────────────────→│  snap / rollback   │
  │                  │                    │                    │
  │  Neuer/alter Tab │                    │                    │
  │  sichtbar        │←─────────────────────────────────────────│
```

**Ablauf:**
1. Nutzer führt auf dem Haupt-Content eine horizontale Wischgeste aus
2. Ein `HorizontalPager` verschiebt die Seiten live in Finger-Richtung
3. Während des Drags kann der Nutzer zwischen alter und neuer Seite hin- und herziehen
4. Bei ausreichender Distanz/Velocity snappt der Pager auf den Ziel-Tab, sonst zurück auf den Start-Tab
5. Die virtuelle Pager-Indexlogik erlaubt zyklisches Durchwischen (Wrap-Around) über alle Haupt-Tabs
