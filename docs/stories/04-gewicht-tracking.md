# Gewichts-Tracking

## US-0401: Gewicht erfassen

Als Nutzer möchte ich mein aktuelles Gewicht erfassen, damit ich meinen Gewichtsverlauf dokumentieren kann.

**Akzeptanzkriterien:**
- Gewicht wird in Kilogramm eingegeben (eine Dezimalstelle)
- Das aktuelle Datum wird automatisch zugeordnet
- Nur ein Eintrag pro Tag erlaubt (bei erneutem Eintrag wird überschrieben)
- Validierung: Gewicht > 0 und realistischer Bereich (z. B. 20–500 kg)

## US-0402: Gewichtsverlauf als Liniendiagramm

Als Nutzer möchte ich meinen Gewichtsverlauf als Liniendiagramm sehen, damit ich Trends erkennen kann.

**Akzeptanzkriterien:**
- X-Achse: Datum
- Y-Achse: Gewicht in kg
- Datenpunkte sind verbunden (Linie)
- Skalierung passt sich automatisch an den Wertebereich an
- Bei weniger als 2 Datenpunkten wird ein Hinweis statt dem Diagramm angezeigt

## US-0403: Gewichtsverlauf als chronologische Liste

Als Nutzer möchte ich meinen Gewichtsverlauf als chronologische Liste sehen, damit ich die genauen Werte einsehen kann.

**Akzeptanzkriterien:**
- Liste zeigt Datum und Gewicht
- Neuester Eintrag steht oben
- Differenz zum vorherigen Eintrag wird angezeigt (z. B. +0.5 kg, -1.2 kg)

## US-0404: Gewichts-Eintrag löschen

Als Nutzer möchte ich einen Gewichts-Eintrag löschen, damit ich Fehleingaben korrigieren kann.

**Akzeptanzkriterien:**
- Eintrag kann aus der Liste gelöscht werden
- Diagramm und Liste werden sofort aktualisiert
- Bestätigungsdialog vor dem Löschen
