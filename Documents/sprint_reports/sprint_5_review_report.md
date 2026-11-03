# Sprint 5 Review Report

*Sprint: 5 — UI Localization Planning and Implementation*

*Team: Kseniia Shlenskaia, Veikka Liukkonen, Luara Moreira Da Silva, Unna Postila*

*Dates: 20 October 2026 – 03 November 2026 (2 weeks).*

## Sprint Summary

The sprint implemented a Java-based localization foundation and integrated language selection for English, Russian, and Japanese. The repository contains locale bundles, a `ResourceBundle`-based localization service, a Settings language selector and controller, persisted language preferences, and tests for language switching, formatting, and layout.

## Completed Work and Evidence

### User Interface Localization

- The Settings screen offers English, Japanese, and Russian. Language changes are connected to the localization service, update the UI without a restart, and are persisted for the next launch.
- Translation resources are present under `src/main/resources/i18n/` for English, Russian, Japanese.
- The localization service uses Java `ResourceBundle` and `Locale`, and provides locale-aware date and number formatting.
- Settings controller tests cover language-to-locale mapping, language changes, retained selection, sample date/number formatting for English/Russian/Japanese, and LTR/RTL orientation behavior.
- User-provided text and uploaded document contents are not intended to be translated.

### Product Backlog

- Localization user stories were added to `Documents/User_story_map.md`, covering supported language selection, consistent translations, retained language choice, and right-to-left layout support.
- The notes reference Trello backlog cards and acceptance criteria.

## Team Member Tasks and Time

| Team Member Name | Assigned Tasks | Time Spent (hrs) | In-class tasks |
|---|---|---:|---|
| Luara Moreira Da Silva | Settings localization UI component (1h); convert the localization map/spreadsheet strings into code (15m); review localization service PR (20m); README and sprint documentation (40min). | 2h 15m reported | done |
| Veikka Liukkonen | Localization service (6h); localization controller (4h); review localization map and Settings UI component PRs (30m). | 10.5 | Not reported |
| Kseniia Shlenskaia | Russian/Japanese localization map and translations (2–3h); review controller and translated user-facing text (30m); finish UI localization and check overall view (1.5h). | 4–5 | Not reported |
| Unna Postila | Update use-case diagram; final review of the Settings localization UI. | Not reported | Not reported |

## Challenges

- **Translation coverage:** Validate all user-facing screens and backend-originated messages against the English, Russian, and Japanese bundles; maintain a key inventory so untranslated strings are visible.

---
