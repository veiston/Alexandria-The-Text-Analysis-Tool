# Sprint 6 Planning Report

*Scrum Master: Veikka Liukkonen*

*Team: Kseniia Shlenskaia, Veikka Liukkonen, Luara Moreira Da Silva, Unna Postila*

*Dates: 1.11.–16.11.*

## Sprint Goal

To systematically refactor the Alexandria codebase, eliminate accumulated technical debt, and improve overall structure and readability during Week 3, followed by configuring and running automated static code analysis tools (Checkstyle, SpotBugs, PMD/CPD) and evaluating code scanning tools (Snyk / Aikido) under a medium-strict enforcement policy during Week 4. The sprint concludes with fixing reported defects, integrating quality checks into the CI/CD pipeline, and preparing the Sprint 6 Review.

## Selected Product Backlog Items

The following items were selected for Sprint 6:

- Code Refactoring & Readability Improvements
- Technical Debt & Dead Code Removal
- Database Resource & Safety Audit
- Static Code Analysis Configuration
- Code Scanning & Vulnerability Analysis Evaluation (Snyk / Aikido)
- Quality Standard Enforcement
- CI/CD Pipeline Static Analysis Integration
- Regression Testing & Quality Documentation

## Planned Tasks / Breakdown

### Week 3: Code Clean & Refactoring

#### Technical Debt & Dead Code Removal
- Remove obsolete prototype code (`ParsePdf.java` in root package) and clean up JaCoCo excludes in `pom.xml`.
- Review and resolve remaining TODO comments across the codebase (e.g. `TermComparisonTextDAO`, `TextComparisonTextDAO`, `AnalyseController`).
- Clean up unused imports, dead methods, and obsolete scratch comments.

#### High-Complexity Class Decomposition
- Refactor the document rendering layer (`TextDocumentRenderer` and `PdfDocumentRenderer`) by extracting shared pagination math, selection calculation, and coordinate handling into shared helper components.
- Decouple oversized controllers (`AnalyseController`, `CompareController`) by separating UI event handling from direct data transformations and database operations.
- Simplify state management and decrease class coupling to reduce cognitive load across the UI layer.

#### Resource Management & Database Safety
- Perform a thorough audit of all DAO classes (`TextDAO`, `UserDAO`, `SearchResultDAO`, `QuotationDAO`, `TermAnalysisDAO`, `TermComparisonDAO`, `TextComparisonDAO`) to ensure strict `try-with-resources` compliance for `Connection`, `PreparedStatement`, and `ResultSet`.
- Standardize error logging across data access layers to eliminate silent exception swallowing or raw `System.err.println` usage.

#### Code Readability & Style Normalization
- Modernize code patterns using current Java idioms (Java 17/21 records, pattern matching, switch expressions).
- Replace magic numbers and hardcoded layout parameters in JavaFX view components with named style classes and layout constants.
- Ensure consistent naming conventions across model, DAO, service, and controller layers.

### Week 4: Static Code Analysis & Code Scanning

#### Static Analysis Tools Configuration (Maven)
- Integrate `maven-checkstyle-plugin` in `pom.xml` configured with standard coding conventions (naming, layout, import ordering, Javadoc).
- Integrate `spotbugs-maven-plugin` in `pom.xml` for static bytecode inspection targeting potential null pointer dereferences, resource leaks, and multithreading bugs.
- Integrate `maven-pmd-plugin` in `pom.xml` for source code analysis, including PMD rules for code complexity and CPD (Copy-Paste Detector) to find duplicated code blocks.

#### Code Scanning & Security Tools Consideration (Snyk / Aikido)
- Evaluate and consider enabling automated code and dependency scanning tools such as **Snyk** or **Aikido**:
  - **Snyk**: Evaluate Snyk CLI / GitHub integration for scanning third-party dependencies in `pom.xml` (e.g., Jackson, MariaDB driver, PDFBox, iText, Password4j) against known CVE databases and scanning container images.
  - **Aikido Security**: Evaluate Aikido as an all-in-one, developer-friendly alternative covering Software Composition Analysis (SCA), Static Application Security Testing (SAST), secret detection, and Docker image scanning with low false-positive noise.

#### Quality Standards & Enforcement Policy (Medium-Strict)
- Establish a **medium-strict** quality gate:
  - **Fail Build (Blocking)**: Critical and High-severity issues reported by SpotBugs (e.g. potential NPEs, unclosed database resources, concurrency faults) and critical vulnerabilities detected by Snyk/Aikido.
  - **Warn / Advisory (Non-blocking)**: Medium and Low-severity style conventions (Checkstyle rules, minor naming suggestions, cosmetic PMD hints) will generate compiler warnings and dashboard reports rather than failing the build immediately, maintaining developer velocity while ensuring clear visibility of technical debt.
- Run baseline static analysis scans across the entire codebase (`mvn checkstyle:checkstyle`, `mvn spotbugs:check`, `mvn pmd:pmd pmd:cpd`).
- Triage and document all reported issues.

#### Defect Remediation
- Fix all blocking High and Critical severity issues identified by SpotBugs.
- Resolve severe code duplication flagged by PMD/CPD in renderers and comparison services.
- Correct major style violations reported by Checkstyle to achieve uniform team formatting.
- Document and provide rationale for any unavoidable false positives or intentional rule suppressions.

### Testing and Quality Assurance
- Run Alexandria's automated test suite (372 unit and integration tests) continuously after each refactoring change to ensure zero regressions.
- Add regression unit tests for any bug fixes performed during static analysis remediation.
- Validate that all JavaFX views and controllers maintain full backward compatibility and layout integrity.

### CI/CD Pipeline Integration (Jenkins)
- Update `jenkins/Jenkinsfile` to add automated stages for static code analysis (`Checkstyle`, `SpotBugs`, `PMD`).
- Publish static analysis reports in Jenkins alongside existing JUnit and JaCoCo test reports.
- Configure pipeline failure conditions adhering to the medium-strict quality threshold.

### Documentation and Sprint Review
- Prepare the Sprint 6 Review Report documenting:
  - Code clean and refactoring achievements.
  - Before-and-after static analysis metric comparisons (issue counts, complexity, duplication).
  - Findings and recommendations from the Snyk / Aikido code scanning evaluation.
  - Updated CI/CD quality gate status.
- Update project documentation, README, and Trello boards.

## Team Capacity & Assumptions

The main assumptions for Sprint 6 were:

- Alexandria's functional features are already in place with a solid regression test baseline (372 passing tests).
- Week 3 refactoring focuses strictly on code structure, readability, and technical debt removal without altering end-user functionality.
- Static code analysis tooling uses a medium-strict enforcement approach so that minor stylistic rules do not paralyze team progress while critical bugs and security issues are strictly caught.
- Evaluation of Snyk / Aikido provides an actionable roadmap for software composition analysis and continuous security scanning.
- Jenkins CI/CD pipeline has access to run Maven plugins without performance degradation.

## Definition of Done

A Sprint 6 task can be moved to `Done` when:

- The planned refactoring or tooling task is completed.
- Existing tests pass with zero regressions (all 372+ unit tests succeed).
- Code changes follow the medium-strict quality standard (zero Critical or High-severity SpotBugs issues).
- Code is reviewed and approved by peers.
- Code changes are committed and pushed to GitHub.
- The relevant Trello/Jira task is updated.
- The team member records the time spent on the task.

For the Sprint 6 deliverables to be considered complete:

- Prototype code clean is finished and technical debt is significantly reduced.
- High-complexity classes (`TextDocumentRenderer`, `PdfDocumentRenderer`, `AnalyseController`, `CompareController`) are refactored.
- Database resource handling is verified across all DAOs.
- Checkstyle, SpotBugs, and PMD/CPD plugins are integrated into `pom.xml` and operational.
- Snyk / Aikido code scanning feasibility and findings are evaluated and documented.
- All High/Critical static analysis defects are resolved.
- Jenkins pipeline is updated with automated static analysis stages.
- Automated tests pass with 100% success rate.
- Sprint 6 Review Report with before-and-after quality metrics is prepared.
- GitHub repository and Trello board are finalized.

---
