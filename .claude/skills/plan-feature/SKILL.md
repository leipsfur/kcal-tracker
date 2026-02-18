---
name: plan-feature
description: Dokumentiert ein neues Feature als User Stories in docs/stories/ und als Todo-Datei in docs/todo/. Verwende diesen Skill wenn ein neues Feature geplant und dokumentiert werden soll.
disable-model-invocation: true
user-invocable: true
argument-hint: <Feature-Beschreibung>
---

# Feature dokumentieren

Du dokumentierst ein neues Feature für das KcalTrack-Projekt. Das Feature: **$ARGUMENTS**

## Ablauf

### Schritt 1: Recherche

Lies die folgenden Dateien, um bestehende Nummern und Formate zu kennen:
- Alle Dateien in `docs/stories/*.md` — für bestehende US-Nummern und Story-Format
- Alle Dateien in `docs/todo/*.md` — für bestehende Phasen-Nummern

### Schritt 2: Vorschlag erarbeiten

Erarbeite einen Vorschlag und präsentiere ihn dem Nutzer **vor** dem Schreiben:

1. **Betroffene Story-Dateien** identifizieren (welche `docs/stories/XX-*.md`)
2. **Nächste US-Nummern** bestimmen (fortlaufend pro Datei; Prefix nach Datei: 01=Dashboard, 02=Essen, 03=Aktivität, 04=Gewicht, 05=Einstellungen, 06=Widget, 07=Navigation)
3. **User Stories** entwerfen mit Akzeptanzkriterien
4. **Todo-Datei** entwerfen mit Implementierungsschritten

Zeige dem Nutzer den vollständigen Entwurf und warte auf Bestätigung oder Änderungswünsche.

### Schritt 3: Dokumentation schreiben

Nach Bestätigung:

**User Stories** — An die jeweilige `docs/stories/XX-*.md` Datei anhängen:

```
## US-XXYY: Titel

Als Nutzer möchte ich ..., damit ...

**Akzeptanzkriterien:**
- Kriterium 1
- Kriterium 2
```

**Todo-Datei** — Neue Datei unter `docs/todo/` erstellen:
- Dateiname: `{YYYY-MM-DD}-{Zusammenfassung}.md` (heutiges Datum, Zusammenfassung mit Bindestrichen)
- Beispiel: `2026-02-18-Vorlagen-aus-Einträgen.md`

```
# Titel (US-Referenzen)

Dokumentation: [docs/stories/XX-name.md](../stories/XX-name.md)

- [ ] Konkreter Implementierungsschritt 1
- [ ] Konkreter Implementierungsschritt 2
```

Die Todo-Datei muss immer relative Links zu den betroffenen Story-Dateien enthalten, damit die Doku leicht auffindbar ist. Bei mehreren betroffenen Story-Dateien werden alle aufgelistet.

## Regeln

- Keine Code-Änderungen, nur Dokumentation
- Sprache: Deutsch
- Bestehende Formate und Konventionen exakt einhalten
- Immer erst Vorschlag zeigen, dann auf Bestätigung warten
