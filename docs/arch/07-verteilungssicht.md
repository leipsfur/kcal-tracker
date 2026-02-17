# 7. Verteilungssicht

## Deployment

KcalTrack ist eine reine On-Device-Anwendung. Es gibt keine Server-Infrastruktur.

```
┌─────────────────────────────────────┐
│         Android-Gerät               │
│                                     │
│  ┌───────────────────────────────┐  │
│  │       KcalTrack APK           │  │
│  │                               │  │
│  │  ┌─────────┐  ┌───────────┐  │  │
│  │  │  App    │  │  Widget   │  │  │
│  │  │ (Main   │  │ (Glance   │  │  │
│  │  │ Activity)│  │ Provider) │  │  │
│  │  └────┬────┘  └─────┬─────┘  │  │
│  │       │              │        │  │
│  │       ▼              ▼        │  │
│  │  ┌───────────────────────┐    │  │
│  │  │     Room Database     │    │  │
│  │  │   (app-interner       │    │  │
│  │  │    SQLite Storage)    │    │  │
│  │  └───────────────────────┘    │  │
│  └───────────────────────────────┘  │
│                                     │
└─────────────────────────────────────┘
```

## Knoten und Artefakte

| Knoten | Artefakt | Beschreibung |
|--------|----------|-------------|
| Android-Gerät | KcalTrack APK | Einzelne installierbare App |
| App-interner Storage | Room DB (SQLite) | Alle Nutzerdaten, Vorlagen, Einstellungen |

## Kommunikation

- **Keine Netzwerkkommunikation** – die App benötigt keine Internetberechtigung
- App und Widget teilen sich die gleiche Room-Datenbank
- Widget-Updates erfolgen über den Android AppWidgetManager

## Anforderungen an die Umgebung

| Anforderung | Wert |
|------------|------|
| Android-Version | 10+ (SDK 29) |
| Speicher | Minimal (< 50 MB für DB bei normaler Nutzung) |
| Internet | Nicht erforderlich |
| Berechtigungen | Keine besonderen Berechtigungen nötig |
