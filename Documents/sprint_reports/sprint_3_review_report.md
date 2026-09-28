# Sprint 3 Review Report

During Sprint 3, the team extended the functional prototype, extended the analysis workflow, updated library features, added settings and user guide features, and fixed errors from Sprint 2. Also the project was prepared for continuous integration: Jenkins now builds the project, runs unit tests, and publishes JaCoCo coverage reports, a Docker image was also built and tested locally.

## Individual Commit Update

- **Luara Moreira Da Silva:** Analysis screen controllers and tests, term search, quotation view and controllers, quotation highlighting and selection, quotation screen, and code review.
- **Kseniia Shlenskaia:** Settings screen with light and dark theme, User guide screen and user guide tour, archive screen, quotation and statistics backend, Jenkins CI/CD pipeline, JaCoCo report publication, Dockerfile preparing and local Docker testing.
- **Veikka Liukkonen:** Fixed fuzzy search duplicate and partial results, corrected page and paragraph counting for PDF text, added missing search settings and stop-word handling, refactored text comparison for full-word comparison, and updated the Library UI and stop-word list.
- **Unna Postila:** Comparison screen and unit tests.

## Product / Sprint Backlog Update

Completed tasks:

- Completed the Analysis screen, including:
  - PDF and TXT document reader with page navigation and zoom controls.
  - Term Frequency results for the most frequent non-stop words.
  - Term search with case-sensitive, whole-word, and fuzzy search options.
  - Tracked Words list with occurrence counts and match navigation.
  - Key Paragraphs results with links back to the relevant text.
  - Quotation selection, highlighting, saving, managing and viewing.
  - Saving analysis findings to Archive.
  - Controllers for the analysis workflow.
- Added a Settings screen with light and dark theme support.
- Added a User Guide screen.
- Added an Archive screen.
- Started work on the Comparison screen.
- Fixed fuzzy search, PDF page and paragraph counting, Library UI, stop-word handling, and text comparison behaviour.
- Configured Jenkins for checkout, build, unit tests, and JaCoCo coverage reporting. Added local Docker image build and test instructions, the Docker test run completed successfully with 73 tests passing.
- Increased automated test coverage to 60-70%.
- Added performance, security and end-to-end tests.

## GitHub Update

Changes added to the repository:

- Jenkins pipeline configuration, JaCoCo reporting configuration, and Jenkins setup documentation.
- Dockerfile, Docker profile, and Docker setup documentation.
- New and updated JavaFX screens, controllers, and backend services.
- Unit, performance, security, and manual end-to-end testing updates.
- 
- Sprint 3 planning and review reports.

## Team Member Tasks

| Team Member | Assigned Tasks | Time Spent (hrs) | In-class Tasks |
| --- | --- | ---: | --- |
| Luara Moreira Da Silva | Analysis workflow, quotations, highlighting, selection, analysis and quotation tests, code review | 37 | Submitted |
| Kseniia Shlenskaia | Settings, User Guide, Archive, statistics and quotations backend, Jenkins, JaCoCo, Docker | 31 | Submitted |
| Veikka Liukkonen | Bug fixes: fuzzy search, PDF page and paragraph counting, Library UI, stop-word handling, and text comparison | 16.5 | Submitted |
| Unna Postila | Comparison screen and unit tests | TBA | TBA |

## Sprint Summary

### What Went Well

- The main application screens were completed, and the application can be used for its core workflows.
- The team found and fixed several frontend and backend bugs.

### What Could Be Improved

- Communication between team members could be more consistent.
- The workload could be distributed more evenly across the team.
