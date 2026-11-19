# Bug Tracking

This table records bugs and issues found during testing. It is updated when a bug is found, fixed and retested.

| Bug ID | Description | Steps to Reproduce | Priority | Assigned to | Status | Resolution |
|---|---|---|---|---|---|---|
| BUG-01 | The regular expression that splits a text into paragraphs in AnalysisUtils.java uses a repeated group, `(\r?\n\s*){2,}`. With very large texts, it can cause a stack overflow. SonarQube reported it as a bug (Reliability C). | Run the SonarQube analysis: `mvn verify sonar:sonar`. | Medium | Kseniia Shlenskaia | Closed | The regular expression was replaced with `\r?\n\s*\r?\n\s*`, which splits paragraphs in the same way without a repeated group. Retested: all 367 tests pass and the Reliability grade is A. |
| BUG-02 | The GitHub Actions workflow static-analysis.yml has the test database password written directly in the `mysql` command. SonarQube reported it as a vulnerability (Security E). | Run the SonarQube analysis: `mvn verify sonar:sonar`. | High | Kseniia Shlenskaia | Closed | The password was moved to the `MYSQL_PWD` environment variable. Retested: the Security grade is A. |
| BUG-03 | In Docker, Japanese text shows as empty squares. The image has no Japanese fonts. | Run the app in Docker with XQuartz and switch the language to Japanese in Settings. | High | Kseniia Shlenskaia | Closed | Added Japanese fonts (`fonts-ipafont-gothic`, `fonts-ipafont-mincho`) to the Dockerfile. Retested: Japanese text shows correctly. |
| BUG-04 | The Docker Hub image is from sprint 4, so it has no language settings. The old database volume also keeps the old schema. | Run `docker compose -f docker/docker-compose.yml up` and open Settings. | High | Kseniia Shlenskaia | Open | Push a new image and recreate the database volume. |
