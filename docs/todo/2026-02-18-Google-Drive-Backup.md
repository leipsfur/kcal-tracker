# Google Drive Backup & Restore (US-0504, US-0505)

Dokumentation: [docs/stories/05-einstellungen.md](../stories/05-einstellungen.md)

- [ ] Google Identity Services Dependency hinzufügen (`libs.versions.toml`, `build.gradle.kts`)
- [ ] Google Drive API Dependency hinzufügen (`libs.versions.toml`, `build.gradle.kts`)
- [ ] Google Cloud Projekt konfigurieren (OAuth Client ID, Drive API aktivieren)
- [ ] `GoogleAuthManager` erstellen: Sign-In Flow, Token-Verwaltung
- [ ] `DriveBackupRepository` erstellen: Upload/Download der Datenbank-Datei in App Data Folder
- [ ] Room-Datenbank-Checkpoint (`wal_checkpoint(TRUNCATE)`) vor Backup durchführen
- [ ] `BackupManager` erstellen: Datenbank schließen, Datei kopieren, Upload orchestrieren
- [ ] `RestoreManager` erstellen: Download, Datenbank schließen, Datei ersetzen, App-Neustart auslösen
- [ ] `SettingsViewModel` erweitern: Backup/Restore-Aktionen, Ladezustand, Fehlermeldungen
- [ ] `SettingsScreen` erweitern: Bereich „Datensicherung" mit Backup/Restore-Buttons und Status-Anzeige
- [ ] Letztes Backup-Datum persistent speichern (z. B. in `SharedPreferences` oder `UserSettings`)
- [ ] Fehlerbehandlung: Kein Internet, Anmeldung abgebrochen, kein Backup vorhanden
- [ ] Bestätigungsdialog vor Wiederherstellung implementieren
- [ ] `strings.xml`: Neue Strings für Datensicherungs-UI und Fehlermeldungen
- [ ] Manuelle Tests: Backup erstellen, Restore auf gleichem/anderem Gerät, Fehlerszenarien
