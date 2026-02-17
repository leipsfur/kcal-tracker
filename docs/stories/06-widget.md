# Homescreen-Widget

## US-0601: Widget mit verbleibenden Kalorien

Als Nutzer möchte ich ein Homescreen-Widget sehen, das mir die verbleibenden Kalorien für heute anzeigt, damit ich ohne App-Öffnung meinen Status kenne.

**Akzeptanzkriterien:**
- Widget zeigt verbleibende kcal an: `Übrig = Grundumsatz + Σ Aktivität - Σ Aufnahme`
- Widget aktualisiert sich bei Änderungen in der App
- Widget zeigt einen Hinweis, wenn kein Grundumsatz eingestellt ist
- Design folgt Material You / Glance-Richtlinien
- Widget ist resizeable

## US-0602: Quick-Add Overlay über Widget

Als Nutzer möchte ich über das Widget schnell Essen aus meinen Vorlagen hinzufügen, damit ich ohne die App komplett zu öffnen tracken kann.

**Akzeptanzkriterien:**
- Tipp auf das Widget öffnet ein Quick-Add Overlay (kein Freitext, nur Vorlagen)
- Vorlagen werden in einer durchsuchbaren Liste angezeigt
- Bei Auswahl einer Vorlage wird die Portionsmenge abgefragt (Standard: 1)
- Eintrag wird dem heutigen Tag zugeordnet
- Widget-Anzeige aktualisiert sich nach dem Hinzufügen
- Overlay schließt sich nach erfolgreichem Hinzufügen
