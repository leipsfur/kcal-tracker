# Dashboard

## US-0101: Tagesübersicht anzeigen

Als Nutzer möchte ich auf dem Dashboard meine heutige Kalorienbilanz sehen, damit ich auf einen Blick weiß, wie viel ich noch essen kann.

**Akzeptanzkriterien:**
- TDEE wird angezeigt (Grundumsatz + Summe Aktivitäten)
- Aufgenommene kcal werden angezeigt (Summe Essen-Einträge)
- Verbleibende kcal werden angezeigt (TDEE - Aufnahme)
- Formel: `Übrig = Grundumsatz + Σ Aktivität - Σ Aufnahme`
- Wenn kein Grundumsatz eingestellt ist, wird ein Hinweis angezeigt

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
