# Essen-Tracking

## US-0201: Lebensmittel-Vorlage erstellen

Als Nutzer möchte ich Lebensmittel als Vorlagen speichern, damit ich häufig gegessene Lebensmittel schnell erfassen kann.

**Akzeptanzkriterien:**
- Name und kcal sind Pflichtfelder
- Kategorie ist Pflichtfeld (Auswahl aus vorhandenen Essen-Kategorien)
- Portionsgröße und Portionseinheit sind Pflichtfelder (z. B. 100g, 1 Stück, 250ml)
- Protein, Kohlenhydrate und Fett sind optionale Felder
- Vorlage wird in der Datenbank gespeichert
- Validierung: Name nicht leer, kcal > 0, Portionsgröße > 0

## US-0202: Lebensmittel-Vorlage bearbeiten

Als Nutzer möchte ich bestehende Lebensmittel-Vorlagen bearbeiten, damit ich Fehler korrigieren oder Werte anpassen kann.

**Akzeptanzkriterien:**
- Alle Felder der Vorlage können bearbeitet werden
- Änderungen wirken sich nicht auf bereits erfasste Einträge aus
- Validierung wie bei der Erstellung

## US-0203: Lebensmittel-Vorlage löschen

Als Nutzer möchte ich Lebensmittel-Vorlagen löschen, damit meine Vorlagenliste übersichtlich bleibt.

**Akzeptanzkriterien:**
- Vorlage kann gelöscht werden
- Bereits erfasste Einträge bleiben erhalten (kein kaskadierendes Löschen)
- Bestätigungsdialog vor dem Löschen

## US-0204: Essen über Vorlage erfassen

Als Nutzer möchte ich Essen über eine Vorlage erfassen, damit ich häufig gegessene Lebensmittel schnell tracken kann.

**Akzeptanzkriterien:**
- Nutzer wählt eine Vorlage aus einer Liste
- Vorlagen sind nach Kategorie gruppiert und durchsuchbar
- Portionsmenge ist vorausgefüllt (Standard: 1 Portion), aber anpassbar
- kcal und Makros werden proportional zur Menge berechnet
- Eintrag wird dem aktuell im Dashboard gewählten Tag zugeordnet (Standard: heute)
- Kategorie wird von der Vorlage übernommen

## US-0205: Essen manuell erfassen

Als Nutzer möchte ich Essen auch ohne Vorlage manuell erfassen, damit ich auch Lebensmittel tracken kann, die ich nicht als Vorlage gespeichert habe.

**Akzeptanzkriterien:**
- Name, kcal und Kategorie sind Pflichtfelder
- Makros (Protein, Kohlenhydrate, Fett) sind optional
- Eintrag wird dem aktuell im Dashboard gewählten Tag zugeordnet (Standard: heute)
- Validierung: Name nicht leer, kcal > 0

## US-0208: Essen-Eintrag bearbeiten

Als Nutzer möchte ich einen bestehenden Essen-Eintrag bearbeiten, damit ich Fehleingaben korrigieren kann, ohne den Eintrag löschen und neu erstellen zu müssen.

**Akzeptanzkriterien:**
- Alle Felder des Eintrags können bearbeitet werden (Name, kcal, Menge, Kategorie, Makros)
- Das Datum des Eintrags kann nicht geändert werden
- Tagesbilanz wird sofort aktualisiert
- Validierung wie bei der Erstellung (Name nicht leer, kcal > 0)

## US-0206: Essen-Eintrag löschen

Als Nutzer möchte ich einen Essen-Eintrag löschen, damit ich Fehleingaben korrigieren kann.

**Akzeptanzkriterien:**
- Eintrag kann aus der Tagesansicht gelöscht werden
- Tagesbilanz wird sofort aktualisiert
- Bestätigungsdialog vor dem Löschen

## US-0207: Essen-Kategorien verwalten

Als Nutzer möchte ich eigene Essen-Kategorien anlegen und verwalten, damit ich meine Mahlzeiten so gruppieren kann, wie es zu meinem Tagesablauf passt.

**Akzeptanzkriterien:**
- Initiale Kategorien: Frühstück, Mittagessen, Abendessen, Snack, Alkohol
- Neue Kategorien können hinzugefügt werden
- Bestehende Kategorien können umbenannt werden
- Kategorien können gelöscht werden (nur wenn keine Einträge/Vorlagen damit verknüpft sind)
- Sortierreihenfolge ist anpassbar

## US-0209: Vorlage aus Essen-Eintrag erstellen

Als Nutzer möchte ich aus einem getrackten Essen-Eintrag eine Vorlage erzeugen, damit ich ein Lebensmittel, das ich bereits manuell erfasst habe, künftig schneller tracken kann.

**Akzeptanzkriterien:**
- Bei jedem Essen-Eintrag (mit und ohne verknüpfte Vorlage) ist ein Button „Vorlage erstellen" sichtbar
- Der Vorlagen-Dialog öffnet sich vorausgefüllt mit den Daten des Eintrags (Name, kcal, Protein, Kohlenhydrate, Fett, Portionsgröße, Portionseinheit, Kategorie)
- Die bestehende Validierung für Vorlagen greift (Name nicht leer, kcal > 0, Portionsgröße > 0)
- Der bestehende Eintrag wird nicht mit der neu erstellten Vorlage verknüpft
