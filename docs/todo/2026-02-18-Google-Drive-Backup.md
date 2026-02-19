# Backup/Restore via Share-Dialog & SAF (US-0504, US-0505)

Dokumentation: [docs/stories/05-einstellungen.md](../stories/05-einstellungen.md)

- [x] `FileProvider` einrichten (`file_provider_paths.xml`, `AndroidManifest.xml`)
- [x] `BackupManager` erstellen: WAL-Checkpoint, DB kopieren, FileProvider-URI, Share-Intent, Validierung, Restore
- [x] DI erweitern: `BackupManager` in `KcalTrackApplication`
- [x] `SettingsViewModel` erweitern: Backup/Restore-Aktionen, Ladezustand, Fehlermeldungen
- [x] `SettingsScreen` erweitern: Bereich „Datensicherung" mit Backup/Restore-Buttons, Share-Launcher, SAF-Filepicker, Bestätigungsdialog
- [x] `MainActivity` anpassen: `BackupManager` an `SettingsViewModel.Factory` übergeben
- [x] `strings.xml`: Neue Strings für Datensicherungs-UI
- [ ] Manuelle Tests: Backup erstellen + teilen, Restore aus Datei, Fehlerszenarien (ungültige Datei)
