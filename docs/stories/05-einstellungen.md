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

## US-0504: Daten in Google Drive sichern

Als Nutzer möchte ich meine App-Daten in meinem verknüpften Google-Konto sichern können, damit ich meine Daten bei einem Gerätewechsel oder Datenverlust nicht verliere.

**Akzeptanzkriterien:**
- Im Einstellungen-Screen gibt es einen Bereich „Datensicherung"
- Button „Backup erstellen" startet den Sicherungsvorgang
- Bei erstmaliger Nutzung wird der Google-Anmeldedialog angezeigt (Google Identity Services)
- Die komplette Room-Datenbank wird als Datei in Google Drive App Data gespeichert (für den Nutzer nicht sichtbar im Drive)
- Während des Backups wird ein Ladeindikator angezeigt
- Nach erfolgreichem Backup wird Datum und Uhrzeit der letzten Sicherung angezeigt
- Fehlermeldung bei fehlender Internetverbindung oder abgebrochener Anmeldung
- Ein bestehendes Backup wird überschrieben (immer nur das aktuellste)

## US-0505: Daten aus Google Drive wiederherstellen

Als Nutzer möchte ich meine gesicherten Daten aus meinem Google-Konto wiederherstellen können, damit ich nach einer Neuinstallation oder auf einem neuen Gerät weiterarbeiten kann.

**Akzeptanzkriterien:**
- Button „Backup wiederherstellen" im Bereich „Datensicherung"
- Bei erstmaliger Nutzung wird der Google-Anmeldedialog angezeigt
- Bestätigungsdialog vor der Wiederherstellung: „Alle aktuellen Daten werden durch das Backup ersetzt. Fortfahren?"
- Das Datum des verfügbaren Backups wird im Bestätigungsdialog angezeigt
- Bei erfolgreicher Wiederherstellung wird die App neu gestartet (Datenbank-Verbindungen müssen neu aufgebaut werden)
- Fehlermeldung wenn kein Backup vorhanden ist
- Fehlermeldung bei fehlender Internetverbindung oder abgebrochener Anmeldung
- Ladeindikator während der Wiederherstellung
