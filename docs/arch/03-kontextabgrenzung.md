# 3. Kontextabgrenzung

## Fachlicher Kontext

```
┌──────────┐         ┌─────────────────────┐
│          │ Eingabe  │                     │
│  Nutzer  │────────→│    KcalTrack App     │
│          │←────────│                     │
│          │ Anzeige  │                     │
└──────────┘         └─────────────────────┘
```

| Kommunikationspartner | Input | Output |
|----------------------|-------|--------|
| Nutzer | Essen-Einträge, Aktivitäts-Einträge, Gewicht, Grundumsatz, Vorlagen, Kategorien | Tagesbilanz (TDEE, Übrig), Gewichtsverlauf, gruppierte Tageseinträge, Widget-Anzeige |

Es gibt **keine externen Systeme**. Die App ist vollständig eigenständig.

## Technischer Kontext

```
┌───────────────────────────────────────────────────┐
│                  Android Device                    │
│                                                   │
│  ┌─────────────┐     ┌──────────────────────┐    │
│  │  Homescreen  │     │    KcalTrack App      │    │
│  │   Widget     │────→│                      │    │
│  │  (Glance)    │←────│  ┌────────────────┐  │    │
│  └─────────────┘     │  │   Room DB       │  │    │
│                      │  │  (SQLite)       │  │    │
│                      │  └────────────────┘  │    │
│                      └──────────────────────┘    │
└───────────────────────────────────────────────────┘
```

| Kanal | Protokoll | Beschreibung |
|-------|-----------|-------------|
| App ↔ Room DB | SQLite (lokal) | Persistierung aller Daten auf dem Gerät |
| Widget ↔ App | Glance / AppWidgetProvider | Widget liest Daten aus der gleichen Room DB |
| Widget → Quick-Add | Activity Intent | Overlay/Dialog für schnelles Erfassen |

**Keine Netzwerkkommunikation.** Alle Daten bleiben auf dem Gerät.
