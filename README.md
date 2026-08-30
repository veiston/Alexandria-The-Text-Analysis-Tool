# Alexandria The Text Analysis Tool

Alexandria is a JavaFX-based desktop application for quantitative analysis of large texts and text collections. Its key features include searching for words and phrases within uploaded texts, including different forms of the same word, getting statistics on how often they are used, and comparing texts. The application is designed for language researchers who work with texts in study or research projects.

## Team

- Kseniia Shlenskaia
- Veikka Liukkonen
- Luara Moreira Da Silva
- Unna Postila

## Methodology
Agile development using Scrum. Trello is used for the product backlog, sprint planning, task assignment, and progress tracking.

## Product Vision

### Problem Statement

Researching books and documents may include finding words and phrases, counting how many times they are used, and comparing these results between different texts. For this kind of work, the results should be accurate and repeatable. Generative AI can help with understanding and interpreting texts, but it is less suitable for exact statistical analysis.

### Target Audience

This product is designed for linguists, literary scholars, philologists, humanities researchers, teachers, and students who work with texts in study or research projects and need advanced text search, statistics, comparison of several texts, and tools for working with quotations.

### Value Proposition

Alexandria is a desktop application that provides advanced text search, statistical analysis, text comparison, and the ability to save results and quotations in one place. The results can be checked against the original text and used later in research.

### Key Features and Functionality

- Text Upload
  - Open one or more local files to work with them in the application,
  - Support several file formats when possible,
  - Display the opened texts next to the search and statistics panel.

- Text search
  - Search for a word or phrase, including different forms of the same word,
  - Show all found uses with the surrounding text and, when possible, their location in the text,
  - Save search results to the user profile,
  - Save found quotations to the user profile.

- Statistics for one text
  - Show the most common words in the text and how many times each of them is used, excluding stop words,
  - Allow the user to enter any word or phrase and see how many times it is used in the text,
  - Save the statistical result to the user profile.

- Comparison of two or more texts
  - Show the most common words in two or more texts and how many times each word is used in each text,
  - Allow the user to enter one word or phrase and see how many times it is used in all provided texts at the same time,
  - Save the comparison result to the user profile.

- User Profile
  - Store the user's name, email, profile photo, and organization,
  - Show saved results from text search, statistical analysis, and text comparison, including the text title, result type, result data, and save date,
  - Show saved quotations, including the quotation text, source title, location when available, and save date.

### Goals and Objectives

- Create a tool for text search and quantitative text analysis,
- Allow users to find relevant parts of a text, count how often words are used, compare several texts, and save results and quotations for later use,
- Create a working prototype of the desktop application within 8 weeks,
- Develop the project using the practices and tools used in the course.

### Vision Statement

Our vision is to create a desktop application for quantitative text research using modern development, testing, automation, and deployment practices. The application should provide accurate and repeatable results, save them for later work, and remain a simple and practical tool for people who research texts.

## Technology Stack

- Programming language: Java - because the course uses Java and Java works well for desktop applications.
- User interface: JavaFX - because it is made for building desktop interfaces in Java.
- Build and dependency management: Maven - because it is a standard tool for Java projects.
- Database: MariaDB - because it works well with Java, supports SQL, and fits our relational data.
- PDF processing: iText - because it can extract text from PDF files in Java.
- Testing: JUnit 5 - because it is widely used for testing Java applications.
- Version control: Git - because it supports branches and team development.
- Repository hosting: GitHub - for pull requests, collaboration, and project files.
- Project management: Trello - because it is simple for managing backlog, sprints, tasks.
- CI/CD: Jenkins - because it can automate builds and tests and is used in the course.
- Containerization: Docker - because it makes the project environment easier to run and reproduce.
- Container orchestration: Kubernetes - because it is used in the course for running containers.


## Folder Structure
```markdown
Alexandria-The-Text-Analysis-Tool/
├── pom.xml
├── README.md
├── LICENSE
│
├── docs/
│   ├── architecture-diagram.png
│   ├── user-guide.md
│   └── deployment-guide.md
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── alexandria/
│   │   │           ├── controller/
│   │   │           ├── dao/
│   │   │           ├── model/
│   │   │           ├── service/
│   │   │           ├── utils/
│   │   │           ├── Main.java
│   │   │           └── ParsePdf.java
│   │   └── resources/
│   │       ├── fxml/
│   │       │   └── main.fxml
│   │       ├── styles/
│   │       │   └── application.css
│   │       └── images/
│   │           └── logo.png
│   │
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── alexandria/
│       │           ├── dao/
│       │           ├── service/
│       │           └── integration/
│       └── resources/
│           └── test-database.properties
│
├── database/
│   ├── schema.sql
│   └── data.sql
│
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── .dockerignore
│   ├── mariadb/
│   │   └── init.sql
│   └── jenkins/
│       └── Dockerfile
│
├── jenkins/
│   └── Jenkinsfile
│
└── scripts/
├── setup-db.sh
├── run-app.sh
├── run-gui-windows.sh
├── run-gui-linux.sh
└── run-gui-mac.sh

```
