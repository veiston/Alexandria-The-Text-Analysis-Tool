# Acceptance Test Plan

*Version: 1.0.0*

*Team: Kseniia Shlenskaia, Veikka Liukkonen, Luara Moreira Da Silva, Unna Postila*

## 1. Introduction

This document defines the acceptance criteria and acceptance tests for Alexandria. Each test is linked to a user story from the [User Story Map](User_story_map.md). The application is accepted if all criteria below are met.

## 2. Scope


- User accounts and profile
- Creating projects from texts
- Text analysis, search and comparison
- Library, Archive and quotations
- Language selection (English, Russian, Japanese)

## 3. Acceptance Criteria

### Functional Criteria

- **AC-01:** Users can register, log in and log out. Registration requires a name, a valid email and a password of at least 8 characters. Invalid data shows an error.

- **AC-02:** Users stay logged in after restarting the application. Profile and password changes are saved.

- **AC-03:** Users can create projects by uploading PDF or TXT files up to 50 MB or by pasting text. Unsupported or larger files are rejected.

- **AC-04:** Users can open a project in Analyze or Compare mode. Analyze displays the text and its analysis. Compare requires a second text. Projects created by logged-in users appear in Library.

- **AC-05:** The Analyze screen displays correct word frequencies, key paragraphs and term statistics. Users can navigate to the relevant places in the document.

- **AC-06:** Search finds and highlights all matches according to the selected search options (Fuzzy, Case sensitive, Whole word). Users can navigate between matches and manage tracked words.

- **AC-07:** The Compare screen displays two texts side by side, with correct similarity scores, shared word frequencies and similar paragraphs. Duplicate texts and selections of fewer than two texts are rejected.

- **AC-08:** Search in Compare mode finds and highlights matches in both documents. Users can navigate between matches, use search options and manage tracked words.

- **AC-09:** Logged-in users can save and view text and term analyses, comparisons and quotations. Saved results include their source and date. Quotations can be edited, copied and deleted. Deleted items remain deleted after restarting the application.

- **AC-10:** Guests can use the analysis tools but cannot save results.

- **AC-11:** Users can switch between English, Russian and Japanese without restarting. All interface elements are translated into the selected language.

- **AC-12:** The selected language is saved and restored at the next login.

### Usability Criteria

- **AC-13:** A new user can create a project and find the most frequent word in 3 minutes or less, without help.
- **AC-14:** In every language, no text overlaps or is cut off, and users understand the error messages.

### Performance and Reliability Criteria

- **AC-15:** Analysis of a text with 100,000 words takes less than 5 seconds and counts all 100,000 words. Search in a text with 100,000 words takes less than 5 seconds and finds all matches.
- **AC-16:** If the database stops, or a file is broken or missing, the application shows an error message and does not crash. When the database starts again, saving works without restarting the application.

## 4. Tests Pass / Fail Rules

- A test passes only if the actual result matches the expected result.
- Critical failures block release: data loss, login problems or a crash.
- Performance tests pass only if they finish within their time limit.

## 5. Coverage Matrix

| User Story | Acceptance Criteria | Functional Test Cases | Usability Test Cases | Performance and Reliability Test Cases |
|---|---|---|---|---|
| US-01 Upload a text file | AC-03 | FT-03 | UT-01 | RT-02 |
| US-02 Open an uploaded text | AC-04 | FT-04 | | RT-02 |
| US-03 See the text with analysis tools | AC-04, AC-05 | FT-03 | | |
| US-04 Search for words and phrases | AC-06, AC-08, AC-15 | FT-06, FT-07 | | PT-02 |
| US-05 Find different forms of a word | AC-06, AC-08 | FT-06 | | |
| US-06 See results with surrounding text | AC-06 | FT-06 | | |
| US-07 See and go to the location of results | AC-05, AC-06 | FT-05, FT-06 | | |
| US-08 See the most frequent words | AC-05, AC-13, AC-15 | FT-05 | UT-01 | PT-01 |
| US-09 See the number of uses of a word | AC-05 | FT-05 | | |
| US-10 Leave out stop words | AC-05 | FT-05 | | |
| US-11 Choose several texts to compare | AC-04, AC-07 | FT-03, FT-04, FT-07 | | |
| US-12 See compared texts side by side | AC-07 | FT-07 | | |
| US-13 See shared frequent words | AC-07 | FT-07 | | |
| US-14 Compare a word in different texts | AC-07, AC-08 | FT-07 | | |
| US-15 Create a personal profile | AC-01, AC-02 | FT-01 | | |
| US-16 Manage personal information | AC-02 | FT-02 | | |
| US-17 Save statistics results | AC-09, AC-10, AC-16 | FT-08 | | RT-01 |
| US-18 See saved results with source and date | AC-09 | FT-08 | | |
| US-19 Save quotations | AC-09 | FT-09 | | |
| US-20 Choose an interface language | AC-11 | FT-10 | UT-02 | |
| US-21 All UI text in the chosen language | AC-11 | FT-10 | | |
| US-22 Language is remembered | AC-12 | FT-10 | | |
| US-23 Change language without restarting | AC-11 | FT-10 | | |
| US-24 Use the app in English, Russian or Japanese | AC-11 | FT-10 | | |
| US-25 Layout works in every language | AC-14 | | UT-02 | |
| All screens | AC-14 | | UT-03 | |

## 6. Acceptance Test Cases

### 6.1 Functional Test Cases

**FT-01 Register, Log In, Log Out and Session**

User Story: US-15

Preconditions: The user is logged out. Another user account already exists.

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Try to register with a 7-character password. | "Password must be at least 8 characters." is shown. No account is created. |
| 2 | Try to register with an email that already has an account. | "An account with this email already exists." is shown. No account is created. |
| 3 | Register with valid data. | The account is created and the user is logged in. |
| 4 | Close and reopen the application. | The user is still logged in. |
| 5 | Log out, then log in with a wrong password. | "Incorrect email or password." is shown. |
| 6 | Log in with the right password. | The user is logged in. |

---

**FT-02 Manage Profile**

User Story: US-16

Preconditions: The user is logged in.

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Change the organization and add a PNG photo. | "Profile updated successfully." is shown, and the new data is displayed. |
| 2 | Change the password. | "Password changed successfully." is shown. |
| 3 | Log out and log in with the new password. | The user is logged in. |

---

**FT-03 Create a Project**

User Story: US-01, US-03, US-11

Preconditions: The user is logged in.

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Click + New Project and try to upload a .docx file. | The file is refused with an error message. No project is created. |
| 2 | Click + New Project and try to upload a file larger than 50 MB. | The file is refused with an error message. No project is created. |
| 3 | Click + New Project, choose Upload File and a TXT file, set Open In to Analyze and click Create Project. | The Analyze screen shows the document on the left and the analysis results on the right. The project appears in Library. |
| 4 | Click + New Project, choose Upload File and a PDF file, set Open In to Analyze and click Create Project. | The Analyze screen shows the PDF document on the left and the analysis results on the right. The project appears in Library. |
| 5 | Click + New Project, choose Paste Text, enter a name and some text, set Open In to Analyze and click Create Project. | The Analyze screen shows the document on the left and the analysis results on the right. The project appears in Library. |
| 6 | Click + New Project, choose Upload File and a TXT file, set Open In to Compare and click Create Project. | A window asks for a second text ("Add a second text to compare"). |
| 7 | Add a second text and click "Add to comparison". | The Compare screen opens with both texts. |

---

**FT-04 Library**

User Story: US-02, US-11

Preconditions: The user is logged in. At least two projects are saved in Library.

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Open Library and search for a project. | Only matching projects are shown. |
| 2 | Open the project in Analyze. | The Analyze screen opens with the project's text. |
| 3 | In Library, choose "Compare with another file…" and select a second text. | The Compare screen opens with both texts. |

---

**FT-05 Word Frequency and Word Statistics**

User Story: US-08, US-09, US-10

Preconditions: A project with a short text in which the word counts are known exists.

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Open the project in Analyze. | Term Frequency shows the 5 most frequent words with correct counts. Stop words such as *the* and *and* are not shown. |
| 2 | Click a word in Term Frequency. | The term analysis window opens. The number of uses, the number per 1,000 words, sentences and paragraphs are correct. Nearby Words and Occurrences are shown. |
| 3 | Click "Go to →" for one occurrence. | The document moves to that occurrence. |
| 4 | Click a result in Key Paragraphs. | The document moves to the page and paragraph of the key paragraph. |

---

**FT-06 Search and Search Options**

User Story: US-04, US-05, US-06, US-07

Preconditions: A project created from a PDF file exists.

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Open the project and search for a word that appears in the text several times. | All matches are found and highlighted. The word is added to Tracked Words with its count. |
| 2 | Click the next and previous buttons. | The document moves from one match to the next. |
| 3 | Search again with Case sensitive on. | Only matches with the same capital letters are found. |
| 4 | Search for part of a word with Whole word on. | Nothing is found. |
| 5 | Search for a word with one letter missing with Fuzzy on. | The correct word is found. |

---

**FT-07 Compare Two Texts**

User Story: US-11, US-12, US-13, US-14

Preconditions: The user is logged in. At least two projects are saved in Library.

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Try to compare only one text. | "Select at least two texts to compare." is shown. |
| 2 | Try to compare the same text twice. | "Selected texts must be unique." is shown. |
| 3 | Compare two different texts. | The texts are shown side by side. The panel shows the Similarity Index with a correlation label, shared words with a count for each text, and key paragraphs. |
| 4 | Click a shared word. | The word is searched in both texts. The matches are highlighted in both documents, and the word is added to Tracked Words. |
| 5 | Click a paragraph in Key paragraphs. | The document moves to that paragraph. |

---

**FT-08 Save and Delete Results**

User Story: US-17, US-18

Preconditions: A user account exists. The user is logged out.

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Without logging in, run an analysis and try to save it. | "Log in and save the project to keep its analysis." is shown. Nothing is saved. |
| 2 | Log in. On the Analyze screen, track a word and save the analysis. | The text analysis and the term analysis are saved. |
| 3 | On the Compare screen, select a shared word and save the comparison. | The text comparison and the term comparison are saved. |
| 4 | Open each Archive tab (Text analysis, Term analysis, Text comparison, Term comparison) and open one saved item in each. | Each item is in the correct tab, with the source text and the save date. Its details match the values on the Analyze or Compare screen. |
| 5 | Delete one item and restart the application. | The deleted item is gone. |

---

**FT-09 Quotations**

User Story: US-19

Preconditions: The user is logged in. A project created from a PDF file with at least 3 pages is open on the Analyze screen.

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Select text on page 3 and save it as a direct citation. | The quotation appears in the Quotations tab with the text, the type "Direct citation" and "Page 3". |
| 2 | Click "Go to →". | The document opens on page 3. |
| 3 | Click Edit, change the quotation text and click Update. | The quotation shows the new text. |
| 4 | Click Copy. | The quotation text is copied to the clipboard. |
| 5 | Click Delete. | The quotation is removed from the Quotations tab. |
| 6 | Restart the application and open the project. | The deleted quotation is not shown. |

---

**FT-10 Language**

User Story: US-20, US-21, US-22, US-23, US-24

Preconditions: A user account exists. The application language is English.

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Log in, choose Russian in Settings and open every screen, including the User Guide. | All screens change to Russian right away, without a restart. No English text is left. |
| 2 | Choose Japanese and open every screen again. | All screens change to Japanese right away, without a restart. No English text is left. |
| 3 | Log out, choose English, and log in again. | The application changes back to Japanese. |

### 6.2 Usability Test Cases

These tests are done with at least 3 people who have not used Alexandria before.

**UT-01 First Use**

User Story: US-01, US-08

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Ask the person to create a project from a TXT file and find the most frequent word. | Each person finishes in 3 minutes or less, without help. |

---

**UT-02 Language and Layout**

User Story: US-20, US-25

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Ask the person to change the language to Russian. | Each person finds the setting without help. |
| 2 | Ask the person to look at every screen. | No text overlaps or is cut off. |
| 3 | Repeat steps 1 and 2 with Japanese. | Each person finds the setting without help. No text overlaps or is cut off. |
| 4 | Repeat steps 1 and 2 with English. | Each person finds the setting without help. No text overlaps or is cut off. |

---

**UT-03 Error Messages**

User Story: All screens

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Show the person these errors: a wrong password, a wrong file type and saving as a guest. Ask what went wrong. | Each person understands the errors and knows what to do next. |

### 6.3 Performance and Reliability Test Cases

**PT-01 Analysis of a Large Text**

User Story: US-08

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Run the testAnalyzeLargeTextPerformance. The test analyses a text with 100,000 words. | The analysis finishes in less than 5 seconds and counts 100,000 words. |

---

**PT-02 Search in a Large Text**

User Story: US-04

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Run the testSearchLargeTextPerformance. The test searches for a word that appears 100 times in a text with 100,000 words. | The search finishes in less than 5 seconds and finds all 100 matches. |

---

**RT-01 Database Stops**

User Story: US-17

Preconditions: The user is logged in. A project is open on the Analyze screen.

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Stop the database while the application is running and try to save an analysis. | An error message is shown, and the application does not crash. |
| 2 | Start the database again and save again. | The analysis is saved without restarting the application. |

---

**RT-02 Broken or Missing File**

User Story: US-01, US-02

Preconditions: The user is logged in. A project created from a PDF file is saved in Library.

| Step | Action | Expected System Response |
|---|---|---|
| 1 | Upload a damaged PDF file. | "The PDF file could not be opened:" is shown, and the application does not crash. |
| 2 | Move the file of a saved project to another folder and open the project. | An error message is shown, and the application does not crash. |

## 7. Test Environment

- Local computer with macOS or Windows
- JDK 17 and Maven
- MariaDB with the test database from database/schema.sql and database/data.sql
- Test files: TXT and PDF files, including a PDF file with at least 3 pages

## 8. Sign-Off Criteria

Acceptance is approved when:

- all functional tests pass
- all usability tests pass with at least 3 people
- all performance tests pass
- all reliability tests pass
- no critical defects remain

## 9. Conclusion

This acceptance test plan ensures that Alexandria meets its functional, usability, performance and reliability requirements before it is accepted.
