# 1. Einführung und Ziele

## Aufgabenstellung

KcalTrack ist eine Android-App zum täglichen Tracking von Kalorienaufnahme, körperlicher Aktivität und Körpergewicht. Die App berechnet ein dynamisches Tagesziel (TDEE) und zeigt dem Nutzer an, wie viele Kalorien noch verfügbar sind.

**Kernfunktionen:**
- Essen-Tracking mit Vorlagen und manueller Eingabe
- Aktivitäts-Tracking mit Vorlagen und manueller Eingabe
- Gewichtsverlauf (Graph + Liste)
- Dashboard mit Tagesbilanz (TDEE, Aufnahme, Verbleibend)
- Homescreen-Widget mit Quick-Add Overlay
- Dynamisch pflegbare Kategorien für Essen und Aktivitäten

## Qualitätsziele

| Priorität | Qualitätsziel | Beschreibung |
|-----------|--------------|--------------|
| 1 | Bedienbarkeit | Essen und Aktivitäten müssen in wenigen Taps erfassbar sein. Quick-Add über Widget ohne App-Öffnung. |
| 2 | Zuverlässigkeit | Daten dürfen niemals verloren gehen. Room als bewährte lokale Datenbank. |
| 3 | Performance | Listen und Dashboard müssen auch bei vielen Einträgen flüssig scrollen. Widget-Updates dürfen die App nicht blockieren. |
| 4 | Offline-Fähigkeit | Die App funktioniert vollständig ohne Internetverbindung. Kein Backend, kein Cloud-Sync. |

## Stakeholder

| Rolle | Beschreibung | Erwartung |
|-------|-------------|-----------|
| Nutzer | Einzelperson, die Kalorien tracken möchte | Einfache, schnelle Erfassung; übersichtliches Dashboard; zuverlässige Datenhaltung |
| Entwickler | Einzelentwickler (Projektinhaber) | Wartbarer Code, klare Architektur, testbare Komponenten |
