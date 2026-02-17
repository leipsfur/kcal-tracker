# Aktivitäts-Tracking

## US-0301: Aktivitäts-Vorlage erstellen

Als Nutzer möchte ich Aktivitäten als Vorlagen speichern, damit ich häufig ausgeführte Aktivitäten schnell erfassen kann.

**Akzeptanzkriterien:**
- Name und kcal sind Pflichtfelder
- Kategorie ist Pflichtfeld (Auswahl aus vorhandenen Aktivitäts-Kategorien)
- Vorlage wird in der Datenbank gespeichert
- Validierung: Name nicht leer, kcal > 0

## US-0302: Aktivitäts-Vorlage bearbeiten

Als Nutzer möchte ich bestehende Aktivitäts-Vorlagen bearbeiten, damit ich Fehler korrigieren oder Werte anpassen kann.

**Akzeptanzkriterien:**
- Alle Felder der Vorlage können bearbeitet werden
- Änderungen wirken sich nicht auf bereits erfasste Einträge aus
- Validierung wie bei der Erstellung

## US-0303: Aktivitäts-Vorlage löschen

Als Nutzer möchte ich Aktivitäts-Vorlagen löschen, damit meine Vorlagenliste übersichtlich bleibt.

**Akzeptanzkriterien:**
- Vorlage kann gelöscht werden
- Bereits erfasste Einträge bleiben erhalten
- Bestätigungsdialog vor dem Löschen

## US-0304: Aktivität über Vorlage erfassen

Als Nutzer möchte ich eine Aktivität über eine Vorlage erfassen, damit ich häufig ausgeführte Aktivitäten schnell tracken kann.

**Akzeptanzkriterien:**
- Nutzer wählt eine Vorlage aus einer Liste
- Vorlagen sind nach Kategorie gruppiert und durchsuchbar
- kcal-Wert ist vorausgefüllt, aber anpassbar
- Eintrag wird dem heutigen Tag zugeordnet
- Kategorie wird von der Vorlage übernommen

## US-0305: Aktivität manuell erfassen

Als Nutzer möchte ich eine Aktivität auch ohne Vorlage manuell erfassen, damit ich auch spontane Aktivitäten tracken kann.

**Akzeptanzkriterien:**
- Name, kcal und Kategorie sind Pflichtfelder
- Eintrag wird dem heutigen Tag zugeordnet
- Validierung: Name nicht leer, kcal > 0

## US-0306: Aktivitäts-Eintrag löschen

Als Nutzer möchte ich einen Aktivitäts-Eintrag löschen, damit ich Fehleingaben korrigieren kann.

**Akzeptanzkriterien:**
- Eintrag kann aus der Tagesansicht gelöscht werden
- Tagesbilanz wird sofort aktualisiert
- Bestätigungsdialog vor dem Löschen

## US-0307: Aktivitäts-Kategorien verwalten

Als Nutzer möchte ich eigene Aktivitäts-Kategorien anlegen und verwalten, damit ich meine Aktivitäten sinnvoll gruppieren kann.

**Akzeptanzkriterien:**
- Initiale Kategorien: Cardio, Krafttraining, Alltag
- Neue Kategorien können hinzugefügt werden
- Bestehende Kategorien können umbenannt werden
- Kategorien können gelöscht werden (nur wenn keine Einträge/Vorlagen damit verknüpft sind)
- Sortierreihenfolge ist anpassbar
