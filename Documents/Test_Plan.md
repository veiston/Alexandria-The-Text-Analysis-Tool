# Test Plan

*Version: 1.0.0*

*Team: Kseniia Shlenskaia, Veikka Liukkonen, Luara Moreira Da Silva, Unna Postila*

## 1. Introduction

This document describes how the team tests Alexandria before release: what is tested, by whom, where, how and when. Acceptance criteria and detailed acceptance test cases are defined in the [Acceptance Test Plan](Acceptance_Test_Plan.md). Bugs found during testing are recorded in [Bug Tracking](Bug_Tracking.md).

## 2. Objective

Testing verifies that Alexandria:

- meets the functional requirements of every user story in the [User Story Map](User_story_map.md)
- meets the non-functional requirements for performance, reliability, usability, localization and code quality
- works the same way for guests and logged-in users, in English, Russian and Japanese
- can be built, tested and packaged automatically without manual steps

## 3. Scope

### In Scope

| Area | What is tested |
|---|---|
| User accounts | Registration, login, logout, session restore, profile and password changes |
| Projects | Creating projects from PDF files, TXT files and pasted text; opening them in Analyze or Compare mode |
| Analysis | Word frequency, stop words, term statistics, key paragraphs, navigation to results |
| Search | Fuzzy, case-sensitive and whole-word search, match navigation, tracked words |
| Comparison | Side-by-side view, similarity index, shared words, similar paragraphs |
| Library and Archive | Saving, viewing and deleting analyses, comparisons and quotations |
| Localization | Switching between English, Russian and Japanese, and remembering the choice |
| Non-functional | Performance on large texts, error handling, usability, static code quality |

### Out of Scope

- Third-party libraries (PDFBox, iText, JavaFX, MariaDB). Only how Alexandria uses them is tested.
- Operating systems other than Windows and macOS.
- Load testing with many simultaneous users. Alexandria is a single-user desktop application.

## 4. Resources

### People

| Person | Responsibilities |
|---|---|
| All team members | Write unit tests for their own code, run the user acceptance tests and complete an individual UAT report, record bugs in Bug Tracking |
| Kseniia Shlenskaia | Scrum Master for Sprint 7, static analysis and SonarQube fixes, Jenkins pipeline and coverage report |
| Usability test participants | At least 3 people who have not used Alexandria before (UT-01 to UT-03) |

### Tools

| Tool | Purpose |
|---|---|
| JUnit 4 and Mockito | Unit and component tests |
| TestFX with Monocle | JavaFX UI and end-to-end tests, also without a display |
| JaCoCo | Code coverage report |
| PMD, CPD, Checkstyle, SpotBugs | Static analysis, run by `mvn verify` |
| SonarQube Cloud | Code quality, reliability and security ratings |
| GitHub Actions | Runs tests and static analysis on every push to `main` and every pull request |
| Jenkins | Builds the application, runs tests, publishes coverage and builds the Docker image |
| Bug Tracking table | Tracking defects from discovery to retest |

### Test Data

- `database/schema.sql` and `database/data.sql` for the database structure and seed data
- `src/test/resources`: sample texts (`Alpha.txt`, `Betta.txt`, `Gamma.txt`) and `test-database.properties`
- A PDF file with at least 3 pages, a damaged PDF file, a file larger than 50 MB and an unsupported file type (`.docx`)
- Generated texts with 100,000 words for performance tests

## 5. Test Environment

| Environment | Used for | Configuration |
|---|---|---|
| Developer machine | Unit, integration and manual tests | Windows or macOS, JDK 21 (the code targets Java 17), Maven, MariaDB from `docker/docker-compose.yml` |
| GitHub Actions | Automated tests and static analysis on every change | Ubuntu, Temurin JDK 21, MariaDB 11 service container, `xvfb` virtual display, SonarQube Cloud |
| Jenkins | Build pipeline, coverage report and Docker image | Maven tool `Maven3`, HTML Publisher plugin, see [Jenkins setup](jenkins_setup.md) |
| Docker | Running the packaged application | See [Docker setup](docker_setup.md); requires an X server (XQuartz or XMing) |

The database is reset from `schema.sql` and `data.sql` before each acceptance test session, so every run starts from the same data.

## 6. Test Approach

| Level | Type | Method | Automated |
|---|---|---|---|
| Unit | Functional | Services, utilities, controllers and DAOs tested in isolation with JUnit and Mockito | Yes |
| Integration | Functional | DAO tests against a real MariaDB database | Yes |
| UI component | Functional | Individual JavaFX components tested with TestFX | Yes |
| End-to-end | Functional | Complete user flows through the UI with TestFX (`src/test/java/e2e`) | Yes |
| Static analysis | Non-functional | PMD, CPD, Checkstyle, SpotBugs and SonarQube on every build | Yes |
| Performance | Non-functional | Timed tests on texts with 100,000 words | Yes |
| Acceptance | Functional and non-functional | Manual test cases from the Acceptance Test Plan | No |
| Usability | Non-functional | Moderated tests with new users and a heuristic evaluation against Nielsen's 10 heuristics | No |

## 7. Test Tasks

### 7.1 Functional Testing

| ID | Task | How | Covers |
|---|---|---|---|
| T-01 | Unit test services and utilities | JUnit tests for analysis, search, comparison, validation and file storage | AC-05 to AC-08 |
| T-02 | Test database access | DAO tests for users, texts, analyses, comparisons and quotations against MariaDB | AC-01, AC-02, AC-09 |
| T-03 | Test controllers | JUnit and Mockito tests for screen logic and error handling | AC-01 to AC-10 |
| T-04 | Test UI components | TestFX tests for forms, validation, modals and document views | AC-01, AC-03, AC-14 |
| T-05 | Run end-to-end flows | TestFX suites for login, project analysis, comparison, settings, profile, guest mode and the user guide tour | AC-01 to AC-12 |
| T-06 | Run functional acceptance tests | Execute FT-01 to FT-10 manually on Windows and macOS | AC-01 to AC-12 |

### 7.2 Non-Functional Testing

| ID | Task | How | Covers |
|---|---|---|---|
| T-07 | Performance | `testAnalyzeLargeTextPerformance` and `testSearchLargeTextPerformance` must finish within 5 seconds (PT-01, PT-02) | AC-15 |
| T-08 | Reliability | Stop the database and use damaged or missing files during use (RT-01, RT-02) | AC-16 |
| T-09 | Usability | Run UT-01 to UT-03 with at least 3 new users | AC-13, AC-14 |
| T-10 | Heuristic evaluation | Each team member reviews all screens against Nielsen's 10 heuristics; issues go to Bug Tracking | AC-13, AC-14 |
| T-11 | Localization | Check every screen in all three languages for missing translations and cut-off or overlapping text | AC-11, AC-12, AC-14 |
| T-12 | Code quality | Fix SonarQube issues to reach rating A, or at least B, for reliability, security and maintainability | - |
| T-13 | Build and packaging | Confirm the Jenkins pipeline and the Docker image build and run successfully | - |

### 7.3 Defect Handling

1. Record each defect in [Bug Tracking](Bug_Tracking.md) with steps to reproduce and a priority.
2. Fix the defect and add an automated test for it when possible.
3. Retest the defect and rerun the related test cases.
4. Close the defect only after the retest passes.

## 8. Schedule

Testing takes place in Sprint 7 (16.11.–29.11.).

| Dates | Activity | Tasks |
|---|---|---|
| Continuous | Automated tests and static analysis run on every push and pull request | T-01 to T-05, T-07, T-12 |
| 16.11.–24.11. | Prepare the Acceptance Test Plan and this Test Plan; fix SonarQube issues and the static analysis workflow | T-12 |
| 24.11.–27.11. | User acceptance testing, usability tests, heuristic evaluation and localization checks | T-06, T-08 to T-11 |
| 24.11.–28.11. | Fix and retest reported defects | Defect handling |
| 28.11. | Final regression run, build and packaging check | T-05, T-13 |
| 29.11. | Results summary in the Sprint 7 review and sign-off | - |

## 9. Risks

| Risk | Impact | Mitigation |
|---|---|---|
| The database is not available in a test environment | DAO and end-to-end tests fail | Use the MariaDB container from `docker/docker-compose.yml`; CI starts its own MariaDB service |
| JavaFX UI tests fail without a display or behave unreliably | False failures block the pipeline | Run headless with Monocle locally and `xvfb` in CI; rerun a failing test before reporting a defect |
| Not enough time before the end of the sprint | Some tests are not run | Run critical tests first: login, project creation, saving and data loss scenarios |
| Too few usability test participants | Usability results are not reliable | Agree on participants early; run sessions in parallel |
| Translations are incomplete or do not fit the layout | Users see mixed languages or cut-off text | Check every screen in each language (T-11) |
| Behaviour differs between Windows and macOS | Defects appear on only one platform | Run acceptance tests on both platforms |
| SonarQube Cloud authentication fails in CI | Code quality results are not published | Check the `SONAR_TOKEN` secret and organization key; static analysis reports are still produced locally by `mvn verify` |

## 10. Exit Criteria

Testing is complete when:

- all automated tests pass in GitHub Actions and Jenkins
- all acceptance tests meet the sign-off criteria in the [Acceptance Test Plan](Acceptance_Test_Plan.md)
- performance tests finish within their time limits
- SonarQube ratings are A, or at least B, for reliability, security and maintainability
- no critical or high-priority defects are open in [Bug Tracking](Bug_Tracking.md)
- all fixed defects have been retested
