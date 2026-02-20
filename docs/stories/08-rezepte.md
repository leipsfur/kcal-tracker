# Rezepte

## US-0801: Rezept erstellen und Zutaten erfassen

Als Nutzer möchte ich ein neues Rezept starten und Zutaten mit Mengenangaben hinzufügen, damit ich beim Kochen die Kalorien und Nährstoffe meines Gerichts mitschneiden kann.

**Akzeptanzkriterien:**
- Im Rezepte-Screen gibt es einen Button zum Erstellen eines neuen Rezepts
- Beim Erstellen wird ein Name für das Rezept eingegeben (Pflichtfeld)
- In einer geöffneten Rezept-Session können Zutaten hinzugefügt werden
- Pro Zutat wird erfasst: Name (Pflicht), kcal pro 100g/ml (Pflicht), Referenzeinheit (g oder ml, Pflicht), verwendete Menge (Pflicht)
- Optional pro Zutat: Protein, Kohlenhydrate, Fett (jeweils pro 100g/ml)
- Die kcal und Makros des Gesamtgerichts werden live berechnet und angezeigt
- Formel: `Gesamt-kcal = Σ(zutat.kcalPro100 × zutat.menge / 100)`
- Die Rezept-Session wird in der Datenbank persistiert (überlebt App-Neustart)

## US-0802: Zutaten-Autocomplete aus Zutaten-Datenbank

Als Nutzer möchte ich beim Eintippen eines Zutatennamens Vorschläge aus einer Datenbank erhalten, damit ich Zutaten schneller erfassen kann und nicht jedes Mal die Nährwerte nachschlagen muss.

**Akzeptanzkriterien:**
- Beim Tippen in das Namensfeld werden passende Zutaten aus der `Ingredient`-Tabelle vorgeschlagen
- Vorschläge filtern nach Teilstring (case-insensitive)
- Bei Auswahl eines Vorschlags werden kcal pro 100g/ml, Referenzeinheit und Makros automatisch vorausgefüllt
- Vorausgefüllte Werte können manuell überschrieben werden
- Neue Zutaten (die nicht in der Datenbank existieren) werden automatisch in die `Ingredient`-Tabelle aufgenommen
- Wenn sich die kcal oder Makros einer bekannten Zutat ändern, werden die neuen Werte in die `Ingredient`-Tabelle zurückgespielt

## US-0803: Zutat im Rezept bearbeiten und löschen

Als Nutzer möchte ich Zutaten in einem Rezept nachträglich bearbeiten oder löschen können, damit ich Fehler korrigieren oder Zutaten entfernen kann.

**Akzeptanzkriterien:**
- Jede Zutat in der Rezept-Ansicht kann bearbeitet werden (alle Felder)
- Jede Zutat kann gelöscht werden
- Die Gesamtberechnung des Rezepts wird sofort aktualisiert
- Bestätigungsdialog vor dem Löschen einer Zutat

## US-0804: Rezept abschließen und als Mahlzeit eintragen

Als Nutzer möchte ich ein fertiges Rezept abschließen und als Essen-Eintrag in mein Tagesprotokoll übernehmen, damit die Kalorien und Nährstoffe korrekt getrackt werden.

**Akzeptanzkriterien:**
- Button "Fertig" oder "Als Mahlzeit eintragen" im Rezept
- Beim Abschließen wird abgefragt: Anzahl gekochter Portionen (Pflicht, > 0) und Anzahl gegessener Portionen (Pflicht, > 0, ≤ gekochte Portionen)
- Kategorie für den Essen-Eintrag wird ausgewählt (aus vorhandenen Essen-Kategorien)
- Berechnung pro gegessener Menge: `kcal = Gesamt-kcal / gekochtePortionen × gegessenePortionen`
- Gleiches für Protein, Kohlenhydrate, Fett
- Ein `FoodEntry` wird erstellt mit dem Rezeptnamen, berechneten Werten und dem aktuell im Dashboard gewählten Datum
- Das Rezept erhält den Status "abgeschlossen"

## US-0805: Rezeptliste anzeigen

Als Nutzer möchte ich eine Liste meiner Rezepte sehen, damit ich frühere Gerichte wiederfinden kann.

**Akzeptanzkriterien:**
- Der Rezepte-Screen zeigt eine chronologische Liste aller Rezepte (neuestes oben)
- Jeder Listeneintrag zeigt: Rezeptname, Erstellungsdatum, Gesamt-kcal pro Portion
- Rezepte mit Status "in Bearbeitung" werden visuell hervorgehoben (z.B. mit Badge)
- Tipp auf ein Rezept öffnet die Rezept-Detailansicht

## US-0806: Früheres Rezept bearbeiten

Als Nutzer möchte ich ein früheres Rezept öffnen und bearbeiten, damit ich Zutaten korrigieren oder ergänzen kann.

**Akzeptanzkriterien:**
- Jedes Rezept (auch abgeschlossene) kann geöffnet und bearbeitet werden
- Alle Zutaten können hinzugefügt, bearbeitet oder gelöscht werden
- Änderungen an einem Rezept wirken sich nicht auf bereits erstellte Essen-Einträge aus
- Portionsanzahl kann geändert werden

## US-0807: Portionen aus früherem Rezept nachträglich erfassen

Als Nutzer möchte ich aus einem früheren Rezept nachträglich Portionen als Essen-Eintrag erfassen, damit ich z.B. Reste an einem anderen Tag tracken kann.

**Akzeptanzkriterien:**
- Bei jedem Rezept gibt es einen Button "Portionen eintragen"
- Anzahl der zu essenden Portionen wird abgefragt (Standard: 1)
- Kategorie wird ausgewählt
- Berechnung basiert auf dem aktuellen Stand des Rezepts (kcal, Makros, Portionsanzahl)
- Ein neuer `FoodEntry` wird für das aktuell im Dashboard gewählte Datum erstellt

## US-0808: Rezept als Basis kopieren

Als Nutzer möchte ich ein bestehendes Rezept als Basis für ein neues Rezept kopieren, damit ich ähnliche Gerichte schneller erfassen kann.

**Akzeptanzkriterien:**
- Bei jedem Rezept gibt es einen Button "Als Basis kopieren"
- Es wird ein neues Rezept erstellt mit allen Zutaten des Originals
- Der Name wird mit "(Kopie)" ergänzt und ist editierbar
- Das neue Rezept ist unabhängig vom Original (Änderungen betreffen nur die Kopie)
- Das neue Rezept hat den Status "in Bearbeitung"

## US-0809: Rezept löschen

Als Nutzer möchte ich ein Rezept löschen, damit meine Rezeptliste übersichtlich bleibt.

**Akzeptanzkriterien:**
- Rezept kann gelöscht werden
- Bereits erstellte Essen-Einträge bleiben erhalten
- Bestätigungsdialog vor dem Löschen

## US-0810: Vorausgefüllte Zutaten-Datenbank

Als Nutzer möchte ich, dass die App beim Installieren bereits eine umfangreiche Zutaten-Datenbank mitbringt, damit ich beim Kochen möglichst wenig manuell eingeben muss.

**Akzeptanzkriterien:**
- Die `Ingredient`-Tabelle wird beim ersten App-Start mit ca. 500+ gängigen Zutaten befüllt
- Jede Zutat enthält: Name, kcal pro 100g/ml, Referenzeinheit (g oder ml), Protein, Kohlenhydrate, Fett (soweit verfügbar)
- Kategorien abgedeckt: Gemüse, Obst, Fleisch, Fisch, Milchprodukte, Getreide/Mehl, Hülsenfrüchte, Nüsse/Samen, Öle/Fette, Gewürze, Zucker/Süßungsmittel, Getränke, Fertigprodukte, Backzutaten
- Werte basieren auf gängigen Nährwerttabellen (Durchschnittswerte)
- Befüllung erfolgt per `RoomDatabase.Callback` oder Pre-populated Database
