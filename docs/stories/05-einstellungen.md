# Einstellungen

## US-0501: Grundumsatz eingeben

Als Nutzer möchte ich meinen Grundumsatz (BMR) mit einem Startdatum eingeben, damit die App mein tägliches Kalorienziel datumsabhängig berechnen kann.

**Akzeptanzkriterien:**
- Eingabefeld für den Grundumsatz in kcal
- Startdatum für die neue Periode ist auswählbar (Standard: heute)
- Wert wird persistent als BMR-Periode gespeichert
- Validierung: Ganzzahl, Wert > 0, realistischer Bereich (z. B. 500–5000 kcal)
- Initialer Zustand: Kein Wert gesetzt, Hinweis auf dem Dashboard

## US-0502: Grundumsatz einsehen und ändern

Als Nutzer möchte ich meine gespeicherten Grundumsatz-Perioden sehen und ändern können, damit ich Anpassungen ohne Verlust historischer Konsistenz vornehmen kann.

**Akzeptanzkriterien:**
- Die aktuell gültige BMR-Periode wird im Einstellungen-Screen angezeigt
- Neue Werte erzeugen eine neue Periode ab gewähltem Startdatum (offenes Intervall bis zur nächsten Periode)
- Bei gleichem Startdatum wird die bestehende Periode aktualisiert (kein Duplikat)
- Bei Startdatum = heute wirkt sich die Änderung sofort auf die heutige Dashboard-Berechnung aus

## US-0503: Historische BMR-Konsistenz

Als Nutzer möchte ich den Grundumsatz für eine Periode festlegen, damit vergangene kcal-Differenzen in Dashboard, Widget und Auswertungen stabil bleiben.

**Akzeptanzkriterien:**
- Für jedes Datum wird `bmrForDate(d)` verwendet: größte Periode mit `startDate <= d`
- Für Tage vor der ersten gespeicherten Periode gilt die früheste Periode rückwirkend
- Neue Perioden ändern keine bereits abgeschlossenen Tage mit abweichendem Datum
- Dashboard, Widget und Auswertungen nutzen dieselbe datumsabhängige BMR-Regel

## US-0504: Daten per Share-Dialog sichern

Als Nutzer möchte ich meine App-Daten als Backup-Datei über den Android-Share-Dialog teilen können (z. B. per Google Drive, E-Mail), damit ich meine Daten bei einem Gerätewechsel oder Datenverlust nicht verliere.

**Akzeptanzkriterien:**
- Im Einstellungen-Screen gibt es einen Bereich „Datensicherung"
- Button „Backup erstellen" erstellt eine Kopie der Room-Datenbank
- Nach Erstellung öffnet sich der Android-Share-Dialog zum Teilen der Backup-Datei
- Während des Backups wird ein Ladeindikator angezeigt
- Fehlermeldung bei fehlgeschlagenem Backup

## US-0505: Daten aus Datei wiederherstellen

Als Nutzer möchte ich meine gesicherten Daten aus einer Backup-Datei wiederherstellen können, damit ich nach einer Neuinstallation oder auf einem neuen Gerät weiterarbeiten kann.

**Akzeptanzkriterien:**
- Button „Backup wiederherstellen" öffnet den System-Filepicker (SAF)
- Die ausgewählte Datei wird auf gültiges SQLite-Format validiert
- Bestätigungsdialog vor der Wiederherstellung: „Alle aktuellen Daten werden durch das Backup ersetzt. Fortfahren?"
- Bei erfolgreicher Wiederherstellung wird die App neu gestartet (Datenbank-Verbindungen müssen neu aufgebaut werden)
- Fehlermeldung bei ungültiger Backup-Datei
- Ladeindikator während der Wiederherstellung

## US-0506: Nährwerttabelle scannen (Test-Button)

Als Nutzer möchte ich über einen Test-Button in den Einstellungen ein Foto einer Nährwerttabelle aufnehmen können, damit die On-Device-KI (Gemini Nano) die Nährwerte erkennen kann.

**Akzeptanzkriterien:**
- In den Einstellungen gibt es einen Button „Nährwerttabelle scannen"
- Nach Tippen öffnet sich die Kamera (via CameraX oder `ACTION_IMAGE_CAPTURE`)
- Das aufgenommene Bild wird an Gemini Nano (Google AI Edge SDK) zur Analyse übergeben
- Die Verarbeitung erfolgt vollständig on-device (kein Netzwerkzugriff nötig)
- Während der Erkennung wird ein Ladeindikator angezeigt
- Bei fehlendem Gemini-Nano-Modell wird eine verständliche Fehlermeldung angezeigt

## US-0507: Scan-Ergebnis anzeigen

Als Nutzer möchte ich nach dem Scannen die erkannten Nährwerte in einer Ergebnisansicht sehen, damit ich die Qualität der Erkennung überprüfen kann.

**Akzeptanzkriterien:**
- Nach erfolgreicher Erkennung wird eine Ergebnisansicht angezeigt
- Folgende Werte werden angezeigt (soweit erkannt): Brennwert (kcal), Protein (g), Kohlenhydrate (g), Fett (g)
- Nicht erkannte Werte werden als „–" dargestellt
- Die Ergebnisansicht enthält einen „Schließen"-Button
- Bei Erkennungsfehlern wird eine Fehlermeldung statt der Werte angezeigt
