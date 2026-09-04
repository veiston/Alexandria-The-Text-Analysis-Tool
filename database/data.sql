-- SQL Data

INSERT INTO users (id, name, email, photo, organization, password)
VALUES
    (1, 'Sample User 1', 'sample-user-1@q.com', NULL, 'Sample organization 1', 'sample-hashed-password-1'),
    (2, 'Sample User 2', 'sample-user-2@alexandria.local', NULL, 'Sample organization 1', 'sample-hashed-password-2');

INSERT INTO texts (id, user_id, title, file_name, file_type, content)
VALUES
    (1, 1, 'Sample Manual Text', NULL, 'MANUAL', 'Language researchers read texts in a quiet library. The library stores notes about language, words, and texts. Each text can provide useful material for research.\n\nResearchers compare words in each text. They record common words, frequent words, and important fragments. The results help researchers return to the same text later.\n\nA clear text collection makes research easier. Researchers can search a text, analyse its language, and compare it with other texts.'),
    (2, 1, 'Sample TXT Text', 'sample.txt', 'TXT', 'Students study language in a small library. They read texts and record frequent words in each text. The library gives students a quiet place for research.\n\nThe students compare one text with another text. They search for common words and count how often each word appears. Their research uses language data from several texts.\n\nStudents save useful results after analysis. Later, they can open a saved text comparison and continue their research.'),
    (3, 2, 'Sample PDF Text', 'sample.pdf', 'PDF', 'Researchers compare texts from different sources. They count words and study language patterns in every text. A careful comparison can show common words between texts.\n\nA library can store each text for research. Researchers can search for a word, analyse the text, and save important fragments. Frequent words help researchers understand the language of a text.\n\nThe research team reviews the results together. They compare language data from several texts and record the most important findings.');

INSERT INTO search_results (id, user_id, text_id, query, results_data)
VALUES (1, 1, 1, 'sample', '"Search results data. Data format is not specified yet"');

INSERT INTO text_analysis (id, user_id, text_id, analysis_data)
VALUES (1, 1, 1, '"Text analysis data. It should include total words, unique words, total sentences, total paragraphs, frequent words, and important fragments. It can be stored in JSON format."');

INSERT INTO term_analysis (id, user_id, text_id, term, analysis_data)
VALUES (1, 1, 1, 'sample', '"Term analysis data. It should include the term, total occurrences, relative frequency, sentence count, paragraph count, and neighboring words. It can be stored in JSON format."');

INSERT INTO text_comparisons (id, user_id, comparison_data)
VALUES (1, 1, '"Text comparison data. It should include common words and their counts in each compared text. It can be stored in JSON format."');

INSERT INTO text_comparison_texts (comparison_id, text_id)
VALUES
    (1, 1),
    (1, 2);

INSERT INTO term_comparisons (id, user_id, term, comparison_data)
VALUES (1, 1, 'sample', '"Term comparison data. It should include the term and total occurrences for each compared text. It can be stored in JSON format."');

INSERT INTO term_comparison_texts (comparison_id, text_id)
VALUES
    (1, 1),
    (1, 2);

INSERT INTO quotations (id, user_id, text_id, quotation_text, location)
VALUES (1, 1, 1, 'This is a sample text.', 'This is quotation location. Format is yet to be specified.');
