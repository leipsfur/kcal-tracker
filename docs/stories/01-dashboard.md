# Dashboard

## US-0101: Tagesübersicht anzeigen

Als Nutzer möchte ich auf dem Dashboard meine heutige Kalorienbilanz sehen, damit ich auf einen Blick weiß, wie viel ich noch essen kann.

**Akzeptanzkriterien:**
- TDEE wird angezeigt (`bmrForDate(selektiertes Datum)` + Summe Aktivitäten)
- Aufgenommene kcal werden angezeigt (Summe Essen-Einträge)
- Verbleibende kcal werden angezeigt (TDEE - Aufnahme)
- Formel: `Übrig = bmrForDate(selektiertes Datum) + Σ Aktivität - Σ Aufnahme`
- Historische Tage verwenden den zum Datum gültigen Grundumsatz (keine rückwirkende Neuberechnung durch spätere Änderungen)
- Wenn keine Grundumsatz-Periode gespeichert ist, wird ein Hinweis angezeigt

## US-0102: Tageseinträge nach Kategorie gruppiert anzeigen

Als Nutzer möchte ich meine heutigen Essen-Einträge nach Kategorie gruppiert sehen, damit ich nachvollziehen kann, wann ich was gegessen habe.

**Akzeptanzkriterien:**
- Essen-Einträge des Tages werden nach Kategorie gruppiert (z. B. Frühstück, Mittagessen, Abendessen, Snack, Alkohol)
- Jede Kategorie zeigt eine Zwischensumme (kcal)
- Innerhalb einer Kategorie werden Einträge chronologisch sortiert
- Leere Kategorien werden nicht angezeigt
- Jeder Eintrag zeigt Name, Menge und kcal

## US-0103: Tageswechsel um Mitternacht

Als Nutzer möchte ich, dass ein neuer Tag um 00:00 Uhr beginnt, damit meine Tagesbilanz zur Mitternacht zurückgesetzt wird.

**Akzeptanzkriterien:**
- Einträge werden dem Tag zugeordnet basierend auf dem Datum (LocalDate)
- Das Dashboard zeigt immer den aktuellen Tag (heute)
- Nach Mitternacht zeigt das Dashboard den neuen, leeren Tag
- Einträge vom Vortag bleiben erhalten und sind über die Datumsnavigation zugänglich

## US-0104: Vergangene Tage ansehen und bearbeiten

Als Nutzer möchte ich vergangene Tage auf dem Dashboard ansehen und dort Einträge nachträglich hinzufügen, bearbeiten oder löschen können, damit ich vergessene Mahlzeiten oder Aktivitäten nachtragen kann.

**Akzeptanzkriterien:**
- Das Dashboard zeigt Navigations-Pfeile (vor/zurück) zum Datumswechsel
- Das aktuell angezeigte Datum wird prominent angezeigt
- "Heute"-Button springt zurück zum aktuellen Tag
- Beim App-Start wird immer der heutige Tag angezeigt
- Alle Aktionen (Essen/Aktivität hinzufügen, bearbeiten, löschen) sind auch für vergangene Tage verfügbar
- Einträge, die über einen vergangenen Tag hinzugefügt werden, erhalten das angezeigte Datum (nicht das aktuelle)
- Es können keine Einträge für zukünftige Tage erstellt werden

## US-0105: Uhrzeiten in der Tagesübersicht anzeigen

Als Nutzer möchte ich die Uhrzeiten meiner Essen-Einträge in der Tagesansicht sehen, damit ich meinen Tagesablauf besser nachvollziehen kann.

**Akzeptanzkriterien:**
- Essen-Einträge zeigen die Uhrzeit neben dem Eintragsnamen an (z. B. "08:30 — Haferflocken")
- Innerhalb einer Kategorie werden Einträge nach Uhrzeit sortiert (aufsteigend)

## US-0106: Grundumsatz vom Dashboard aus anpassen

Als Nutzer möchte ich den Grundumsatz direkt vom Dashboard aus anpassen können, damit ich beim Betrachten eines beliebigen Tages (auch vergangener Tage) den Grundumsatz schnell setzen oder korrigieren kann, ohne in die Einstellungen wechseln zu müssen.

**Akzeptanzkriterien:**
- Im Dashboard ist der angezeigte Grundumsatz tippbar (oder hat einen Bearbeiten-Button)
- Tipp öffnet einen Dialog zur Grundumsatz-Eingabe
- Der Dialog zeigt den aktuell gültigen BMR für den selektierten Tag als Vorbelegung
- Änderungen erzeugen/aktualisieren eine BMR-Periode ab dem selektierten Datum (gleiche Logik wie US-0501/US-0502)
- Tagesbilanz wird sofort aktualisiert
- Validierung wie bei US-0501 (Ganzzahl, Wert > 0, realistischer Bereich)
