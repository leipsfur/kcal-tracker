# 12. Glossar

| Begriff | Definition |
|---------|-----------|
| **Grundumsatz (BMR)** | Basal Metabolic Rate – die Kalorienmenge, die der Körper in Ruhe pro Tag verbraucht. Wird in dieser App manuell vom Nutzer eingegeben. |
| **TDEE** | Total Daily Energy Expenditure – der gesamte Tagesenergieverbrauch. Berechnet als: `TDEE = Grundumsatz + Σ Aktivitäten`. |
| **Übrig** | Verbleibende Kalorien für den Tag. Berechnet als: `Übrig = TDEE - Σ Aufnahme`. |
| **Vorlage (Template)** | Ein gespeichertes Lebensmittel oder eine Aktivität mit vordefinierten Werten (Name, kcal, Kategorie). Dient zur schnellen Erfassung wiederkehrender Einträge. |
| **Eintrag (Entry)** | Ein einzelner Datensatz für Essen, Aktivität oder Gewicht an einem bestimmten Tag. |
| **Kategorie** | Gruppierung für Einträge und Vorlagen. Essen: z. B. Frühstück, Mittagessen. Aktivität: z. B. Cardio, Krafttraining. Dynamisch pflegbar. |
| **Portionsgröße** | Die Referenzmenge einer Lebensmittel-Vorlage (z. B. 100g, 1 Stück, 250ml). Nährwerte beziehen sich auf diese Menge. |
| **Portionseinheit** | Die Einheit der Portionsgröße (z. B. g, ml, Stück). |
| **Menge** | Der Multiplikator bei der Erfassung. Beispiel: Vorlage hat 200 kcal pro 100g, Nutzer isst 150g → Menge = 150, kcal = 200 × (150/100) = 300. |
| **Quick-Add** | Schnellerfassung über das Homescreen-Widget. Zeigt Vorlagen zur Auswahl, kein Freitext. |
| **Makros** | Makronährstoffe: Protein, Kohlenhydrate, Fett. Optional bei Lebensmittel-Vorlagen und -Einträgen. |
| **Dashboard** | Hauptscreen der App. Zeigt Tagesbilanz (TDEE, Aufnahme, Übrig) und Einträge nach Kategorie gruppiert. |
| **Dynamic Colors** | Material You Feature (ab Android 12), bei dem die App-Farben aus dem Wallpaper des Nutzers abgeleitet werden. |
| **Glance** | Jetpack-Bibliothek für Android App Widgets mit einer Compose-ähnlichen API. |
| **Room** | Jetpack-Bibliothek als Abstraktionsschicht über SQLite. Bietet Compile-Time-Verifikation und Flow-Support. |
| **DAO** | Data Access Object – Interface das die SQL-Queries für eine Entity definiert. |
| **Flow** | Kotlin Coroutines Flow – asynchroner Datenstrom, der bei Änderungen automatisch neue Werte emittiert. |
| **StateFlow** | Spezieller Flow der immer einen aktuellen Wert hat. Wird für UI-State in ViewModels verwendet. |
