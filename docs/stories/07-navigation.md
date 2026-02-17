# Navigation & Design

## US-0701: Bottom Navigation

Als Nutzer möchte ich über eine Bottom Navigation Bar zwischen den Hauptbereichen der App wechseln, damit ich schnell auf alle Funktionen zugreifen kann.

**Akzeptanzkriterien:**
- 5 Tabs: Dashboard, Essen, Aktivität, Gewicht, Einstellungen
- Jeder Tab hat ein Icon und ein Label
- Aktiver Tab ist visuell hervorgehoben
- Beim App-Start wird das Dashboard angezeigt
- Navigation-State bleibt beim Tab-Wechsel erhalten (kein Neuladen)

## US-0702: Material You Design

Als Nutzer möchte ich, dass die App dem Material You Design-System folgt und sich farblich an mein Gerät anpasst, damit sie sich nativ anfühlt.

**Akzeptanzkriterien:**
- Dynamic Colors werden verwendet (ab Android 12)
- Auf älteren Geräten (SDK 29-31) wird ein Fallback-Farbschema verwendet
- Typografie folgt Material 3 Type Scale
- Komponenten nutzen Material 3 Compose-Komponenten (TopAppBar, FAB, Cards, etc.)
- Dark Mode wird unterstützt (System-Einstellung)
