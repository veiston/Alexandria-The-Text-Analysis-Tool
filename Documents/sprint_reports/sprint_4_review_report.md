# Sprint 4 Review Report

During Sprint 4, the team focused on finalizing the prototype, improving the comparison functionality, fixing remaining frontend and backend issues, testing the application, and deploying the existing Docker image.

The team completed a significant amount of implementation and bug fixing. Several tasks originally assigned to Unna were not delivered during the sprint, so some of these tasks had to be redistributed to other team members in order to keep the project moving toward the final demonstration.

The Docker/containerization work itself had already been completed in the previous sprint. During Sprint 4, the focus was therefore on deployment and testing of the existing Docker image.

## Individual Commit Update

### Luara Moreira Da Silva

Luara worked on testing, code review, UI improvements, comparison functionality, and quotation/highlighting functionality.

Completed work included:

- Unit testing.
- Code review.
- Added "Jump to Page" functionality to the term detail card.
- Fixed compound-term highlighting.
- Fixed the New Project modal.
- Fixed font sizes in the analysis screen.
- Added controllers for saving analyses and quotations from the comparison screen.
- Added documents to the Comparison Analyze tab.
- Added placeholders for text and input term analysis, search, and word tracker containers.
- Connected quotation highlighting to documents in the comparison analysis.
- Removed quotation functionality where required.

### Kseniia Shlenskaia

Kseniia worked on deployment, documentation, comparison functionality, backend services, and frontend integration.

Completed work included:

- Updated the user guide.
- Deployed and tested the Docker image.
- Replaced placeholders with actual UI components and controllers for text analysis comparison.
- Implemented functionality related to most frequent words and similar paragraphs.
- Created interfaces and records for comparison services.
- Implemented comparison term and text database services.
- Fixed the Library "Open With" functionality so that the PDF file path is used instead of extracted PDF text.

### Veikka Liukkonen

Veikka worked primarily on backend comparison functionality, search, Library improvements, and PDF-related bugs.

Completed work included:

- Fixed text comparison logic so that comparison does not incorrectly require a word to appear in both documents.
- Improved the text comparison service.
- Added similarity score functionality.
- Implemented similar paragraph functionality.
- Added filters to the Library screen.
- Added search functionality and settings to the search box.
- Added dual search functionality so that one call can return matches and counts for two documents.
- Fixed incorrect PDF pagination when opening a PDF from the Library screen.

### Unna Postila

Unna was assigned tasks related to the comparison screen controllers and comparison screen UI.

However, the assigned work was not completed during the sprint. Because these tasks were important for the final integration, other team members had to take over parts of the work.

The team attempted to address the situation during the sprint by:

- Reviewing the assigned tasks and deadlines.
- Providing additional support.
- Redistributing work where necessary.
- Continuing implementation with the available team members.

## Product / Sprint Backlog Update

The following Sprint 4 work was completed:

- Unit testing.
- Code review.
- "Jump to Page" functionality.
- Compound-term highlighting.
- New Project modal fixes.
- Analysis screen font-size fixes.
- Comparison document display.
- Comparison UI integration.
- Comparison service interfaces and records.
- Comparison term and text database services.
- Text comparison fixes.
- Similarity score implementation.
- Similar paragraph functionality.
- Library filters.
- Search improvements.
- Dual document search.
- PDF opening fixes.
- PDF pagination fixes.
- Docker image deployment and testing.
- User guide update.

### Additional / Redistributed Work

Some comparison screen work originally assigned to Unna was taken over by other team members because it was not completed during the sprint.

This redistribution allowed the team to continue the integration of the comparison functionality and maintain progress toward the final demonstration.

### Remaining Work

The following work is still in progress:

- End-to-end testing.
- Fixing any bugs discovered during the remaining end-to-end testing.
- Final verification of all major user flows before the final demonstration.

## GitHub Update

The GitHub repository was updated during Sprint 4 with:

- New frontend functionality.
- Backend comparison services.
- Comparison interfaces and records.
- Bug fixes.
- Unit tests.
- UI improvements.
- Search improvements.
- PDF-related fixes.
- Comparison functionality.
- Updated user guide and documentation.
- Docker-related deployment work.
- Sprint documentation.

The repository will be finalized before the final demonstration, including the README, Sprint Reports, and any remaining documentation required by the lecturer.

## Docker / Deployment Update

The Docker/containerization setup had already been completed during the previous sprint.

During Sprint 4, the team focused on deployment and testing of the existing Docker image.

Kseniia completed the Docker deployment and testing work, including verifying that the application could be run from the containerized image.

The Docker image and deployment information will be included in the final presentation and project documentation.

## Team Member Tasks

| Team Member | Assigned Tasks | Time Spent (hrs) | In-class Tasks |
|---|---|---:|---|
| Veikka Liukkonen | Text comparison fixes<br>Similarity score and similar paragraphs<br>Library filters<br>Search functionality<br>Dual search<br>PDF pagination fix | 15.5 | Submitted |
| Kseniia Shlenskaia | User guide<br>Docker deployment and testing<br>Comparison frontend integration<br>Comparison service interfaces<br>Comparison database services<br>Library PDF opening fix | 20 | Submitted |
| Luara Moreira Da Silva | Unit testing<br>Code review<br>Jump to Page functionality<br>Compound-term highlighting<br>New Project modal and font fixes<br>Comparison controllers<br>Comparison Analyze UI<br>Quotation highlighting | 22.5 | Submitted |
| Unna Postila | Comparison screen controllers<br>Comparison screen UI tasks | 0 | Not Submitted |

## Scrum Master Activities

As Scrum Master, I was responsible for monitoring the sprint progress, helping the team stay aligned with the sprint goals, and addressing blockers when they appeared.

At the beginning of the sprint, I provided an overview of the planned tasks and deadlines so that the team had a common understanding of the sprint priorities.

During the sprint, one of the main challenges was that some comparison screen tasks assigned to Unna were not progressing as expected.

To address this, I:

- Gave the team an overview of the sprint tasks and deadlines on Monday.
- Had an additional meeting with Unna on Tuesday to clarify the tasks and provide support.
- Contacted Unna directly by phone to try to resolve the blockers.
- Sent private messages to other team members to remind them about deadlines and unfinished tasks.
- Monitored the remaining work and identified tasks that needed to be redistributed.
- Supported the redistribution of tasks so that critical functionality could still be completed before the final demonstration.

Despite these efforts, some of the assigned comparison tasks were not completed by Unna and had to be taken over by other team members.

## Sprint Summary

### What Went Well

- The team completed a large amount of implementation work during the sprint.
- Major frontend and backend bugs were fixed.
- The comparison functionality was significantly improved.
- Unit testing and code review were performed.
- The existing Docker image was successfully deployed and tested.
- The Library and PDF functionality were improved.
- The UI was improved in several important areas.
- Team members were able to take over additional tasks when necessary.
- The team continued working toward the final functional prototype despite changes in task ownership.
- The user guide and project documentation were updated.

### What Could Be Improved

- Task ownership and progress should have been monitored more closely from the beginning of the sprint.
- Comparison screen tasks should have been broken into smaller, independently trackable tasks.
- Blockers should have been reported earlier.
- Tasks that were dependent on other team members should have had clearer dependencies in Trello.
- Work should have been redistributed earlier once it became clear that some tasks were not progressing.
- End-to-end testing should have started earlier to allow more time for fixing integration problems.
- Time tracking should be maintained continuously throughout the sprint rather than being reconstructed later.
- The team should have had a clearer backup plan for critical tasks if the assigned team member could not complete them.

## Overall Sprint Result

Sprint 4 resulted in substantial progress toward the final prototype. The team completed most of the planned implementation, testing, bug fixing, UI improvements, comparison functionality, and Docker deployment work.

The main remaining item is **end-to-end testing**, which is still in progress. The results of this testing will be used to identify and fix any final integration issues before the final demonstration.

The sprint also demonstrated the importance of early communication, clear task ownership, continuous progress monitoring, and timely redistribution of blocked work. Despite some difficulties with task completion, the team was able to adapt and continue development toward the final project demonstration.