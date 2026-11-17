# Sprint 6 Review Report

*Sprint: 6 — Code Clean-up, Static Code Analysis and Code Scanning*

*Scrum Master: Veikka Liukkonen*

*Team: Kseniia Shlenskaia, Veikka Liukkonen, Luara Moreira Da Silva, Unna Postila*

*Dates: 1 November 2026 – 16 November 2026 (2 weeks).*

## Sprint Summary

The sprint focused on reducing technical debt and adding code-quality tooling. Unused files and dead code were removed from the application. Static code analysis (PMD, CPD, Checkstyle and SpotBugs) was added to the Maven build. Automated scanning was set up with SonarQube Cloud (SonarCloud) and Aikido. In addition, the language selection from Sprint 5 was extended so that each user's language is now stored in the database and restored when they log in.

## Planned vs. Delivered

| Planned item | Status |
|---|---|
| Technical debt and dead code removal | Done |
| Static code analysis configuration (PMD, CPD, Checkstyle, SpotBugs) | Done |
| Code scanning evaluation (Snyk / Aikido) | Aikido and SonarQube Cloud set up instead of Snyk |
| CI/CD static analysis integration | Done |
| Database localization (not in the original plan) | Done |

## Team Member Tasks and Time

| Team Member Name | Assigned Tasks | Time Spent (hrs) | In-class tasks |
|---|---|---:|---|
| Luara Moreira Da Silva | Remove unused files and dead code. | 3 | Not reported |
| Veikka Liukkonen | Add and set up static code analysis tools (PMD, CPD, Checkstyle, SpotBugs) with fixes; set up Aikido and SonarQube Cloud scanning. | 3 | Not reported |
| Kseniia Shlenskaia | Database localization (PR #48). | 3 | Not reported |
| Unna Postila | No tasks reported. | Not reported | Not reported |

## Challenges

- **Remaining scope:** The planned refactoring of the large renderer and controller classes, the DAO audit and the Jenkins integration move to the next sprint.

---
