# Sprint 5 Planning Report

*Sprint: 5 — UI Localization Planning and Implementation*

*Scrum Master: Luara Moreira Da Silva*

*Team: Kseniia Shlenskaia, Veikka Liukkonen, Luara Moreira Da Silva, Unna Postila*

*Dates: 20 October 2026 – 03 November 2026 (2 weeks).*

## Sprint Goal

Prepare Alexandria for multilingual use by externalizing user-facing interface text, integrating language selection, supporting English, Russian, and Japanese, and verifying dynamic UI updates and locale-aware formatting. Document the localization approach and prepare the backlog and planning work for database localization in Sprint 6.

## Planned Objectives and Deliverables

### 1. User Interface Localization Preparation — 2 points

- Identify user-facing labels, messages, and other strings requiring translation.
- Externalize static UI text into localization resources and connect the Settings language selector to the localization flow.
- Support locale-aware dates and numbers in UI locations, including tables.

**Acceptance criteria**

- English, Russian, and Japanese are available in the Settings language selector.
- UI strings are mapped to translation keys and maintained in the localization bundles.
- Selecting a language updates the active locale and the Settings selection.
- User-provided text and uploaded document contents are not translated.

### 2. Full GUI Localization Implementation — 5 points

- Load English, Russian, and Japanese resources and make translated strings available throughout the GUI.
- Update localized text dynamically without restarting the application and retain the selection between launches.
- Verify Japanese and Russian content and locale-formatted dates and numbers.
- Test layout orientation, including an explicit Arabic/RTL test where supported.

**Acceptance criteria**

- Japanese and Russian provide two non-Latin language options.
- Changing the language updates user-facing UI text without restarting.
- The selected language is restored on the next launch.
- Dates and numbers use the active locale.
- LTR and RTL orientation behavior is tested; Arabic resource availability and selector support are documented separately.
- Missing resources or keys do not silently appear as successful translations during development.

### 3. Sprint and Product Backlog Update — 1 point

- Add localization stories and tasks to the product backlog.
- Define acceptance criteria and estimates for localization work.
- Ensure the sprint backlog reflects localization priorities.

**Acceptance criteria**

- The user story map includes localization stories.
- Localization tasks have explicit acceptance criteria and estimates.

### 4. GitHub Repository and Documentation Update — 1 point

- Document how to select a language and run the localized application in README.
- Identify the localization resources and people/process needed to maintain translations.
- Update the relevant UML documentation in the repository.

**Acceptance criteria**

- README explains language selection and localized launch behavior.
- README identifies translation bundles, translators/reviewers, and whether a separate tool or CMS is needed.
- Relevant updated UML documentation is available in the repository.

### 5. Sprint Planning and Review Reports — 1 point

- Record Sprint 5 work, outcomes, estimates, time spent, and submission status.
- Prepare a Sprint 6 plan focused on database localization, data-model changes, and multilingual content.

**Acceptance criteria**

- Sprint 5 planning and review reports are saved in `Documents/sprint_reports/`.
- The review includes the requested team task/time/in-class status table and an honest summary of unknowns.
- The Sprint 6 plan includes tasks, estimates, responsibilities, dependencies, and acceptance criteria.

## Planned Responsibilities

| Team member | Planned work |
|---|---|
| Luara Moreira Da Silva | Design the Settings language selector component and callback API; prepare the localization user-story map and README/report documentation; review localization changes. |
| Veikka Liukkonen | Implement the localization service and locale-aware date/number formatting; add service tests. |
| Kseniia Shlenskaia | Prepare Russian and Japanese translation resources from the English strings; review translated text and layout fit; review localization controller and UI integration. |
| Unna Postila | connect the Settings controller to the service;  add service/controller tests and review UI integration. Update the use-case diagram. |

## Definition of Done

A localization task is done when:

- The relevant implementation and translation resources are integrated.
- Acceptance criteria are verified by tests or documented UI checks.
- Locale changes, formatting, persistence, and applicable layout behavior are checked.
- Relevant backlog and documentation are updated.
- The work is reviewed and pushed to GitHub.
- Actual time spent and in-class submission status are recorded, or explicitly marked as unreported.

---
