# Navigation & Design

## US-0701: Bottom Navigation

Als Nutzer möchte ich über eine Bottom Navigation Bar zwischen den Hauptbereichen der App wechseln, damit ich schnell auf alle Funktionen zugreifen kann.

**Akzeptanzkriterien:**
- 5 Tabs: Dashboard, Essen, Aktivität, Gewicht, Einstellungen
- Jeder Tab hat ein Icon und ein Label
- Aktiver Tab ist visuell hervorgehoben
- Beim App-Start wird das Dashboard angezeigt
- Navigation-State bleibt beim Tab-Wechsel erhalten (kein Neuladen)

## US-0702: Material You Design

Als Nutzer möchte ich, dass die App dem Material You Design-System folgt und sich farblich an mein Gerät anpasst, damit sie sich nativ anfühlt.

**Akzeptanzkriterien:**
- Dynamic Colors werden verwendet (ab Android 12)
- Auf älteren Geräten (SDK 29-31) wird ein Fallback-Farbschema verwendet
- Typografie folgt Material 3 Type Scale
- Komponenten nutzen Material 3 Compose-Komponenten (TopAppBar, FAB, Cards, etc.)
- Dark Mode wird unterstützt (System-Einstellung)

## US-0703: Screen-interne Navigation

Als Nutzer möchte ich, dass Dialoge und Unterseiten innerhalb der Screens konsistent und vorhersehbar funktionieren, damit die Bedienung intuitiv bleibt.

**Akzeptanzkriterien:**
- Vorlage erstellen/bearbeiten: Dialog oder Bottom Sheet (kein eigener Screen)
- Essen/Aktivität erfassen über Vorlage: Bottom Sheet mit Vorlagenauswahl und Mengenanpassung
- Essen/Aktivität manuell erfassen: Dialog mit Eingabeformular
- Eintrag bearbeiten: Gleicher Dialog wie beim Erstellen, vorausgefüllt mit bestehenden Werten
- Kategorie verwalten: Eigener Unter-Screen (erreichbar über Essen-/Aktivitäts-Screen)
- Bestätigungsdialoge: AlertDialog für destruktive Aktionen (Löschen)
- System-Back-Button schließt Dialoge/Sheets, navigiert nicht zwischen Tabs

## US-0704: Barrierefreiheit

Als Nutzer möchte ich, dass die App grundlegende Barrierefreiheitsstandards einhält, damit sie auch mit Einschränkungen bedienbar ist.

**Akzeptanzkriterien:**
- Alle interaktiven Elemente haben eine Content Description (für Screen Reader / TalkBack)
- Mindest-Touch-Target von 48dp für alle tippbaren Elemente
- Farbkontraste erfüllen WCAG AA (Mindestkontrast 4.5:1 für Text)
- Informationen werden nicht ausschließlich über Farbe vermittelt (z. B. "Übrig" negativ: zusätzlich Minus-Zeichen, nicht nur Rot)
- Eingabefelder haben sichtbare Labels (nicht nur Placeholder-Text)

## US-0705: Wisch-Navigation zwischen Hauptbereichen

Als Nutzer möchte ich durch horizontales Wischen zwischen den Haupt-Tabs wechseln, damit ich schneller zwischen Dashboard, Essen, Aktivität, Gewicht und Einstellungen navigieren kann.

**Akzeptanzkriterien:**
- Wischen nach links wechselt zum nächsten Haupt-Tab
- Wischen nach rechts wechselt zum vorherigen Haupt-Tab
- Reihenfolge: Dashboard → Essen → Aktivität → Gewicht → Einstellungen
- An den Rändern wird zyklisch gewrappt (von Dashboard nach rechts zu Einstellungen, von Einstellungen nach links zu Dashboard)
- Die Slide-Animation läuft live mit der Fingerbewegung (interaktiv)
- Während eines Swipes kann zwischen zwei Tabs hin- und hergezogen werden ("Wackeln")
- Wird der Swipe nicht weit genug abgeschlossen oder zurückgezogen, landet der Nutzer wieder auf der Ausgangsseite
- Bottom Navigation und Wischgesten nutzen dieselbe Navigationslogik (State-Restore bleibt erhalten)
- Screen-interne Navigation (Dialoge, Sheets, Food-/Activity-Untertabs) bleibt funktional und konsistent
