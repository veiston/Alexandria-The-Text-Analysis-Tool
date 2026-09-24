-- SQL Data

INSERT INTO users (id, name, email, photo, organization, password)
VALUES
    (1, 'Test User', 'test@test.com', NULL, 'Test organization', '$argon2id$v=19$m=19456,t=2,p=1$qmQTy9hIABc6GUbYn5xSAAvAZn5v6ziduEN3wsiGtlIJo5Qxxql1KASWXcZ1Oytm9SoniUeDjQ5BYeD8V9PDSQ$y19kCGYo05Iq4bDv+8sTmROPhAQOmwwZXUqiKPPhMrE'),
    (2, 'Sample User 2', 'sample-user-2@alexandria.local', NULL, 'Sample organization 1', 'sample-hashed-password-2');

INSERT INTO texts (id, user_id, title, file_name, file_type, content)
VALUES
    (1, 1, 'Sample Manual Text', NULL, 'MANUAL', 'Language researchers read texts in a quiet library. The library stores notes about language, words, and texts. Each text can provide useful material for research.\n\nResearchers compare words in each text. They record common words, frequent words, and important fragments. The results help researchers return to the same text later.\n\nA clear text collection makes research easier. Researchers can search a text, analyse its language, and compare it with other texts.'),
    (2, 1, 'Sample TXT Text', 'sample.txt', 'TXT', 'Students study language in a small library. They read texts and record frequent words in each text. The library gives students a quiet place for research.\n\nThe students compare one text with another text. They search for common words and count how often each word appears. Their research uses language data from several texts.\n\nStudents save useful results after analysis. Later, they can open a saved text comparison and continue their research.'),
    (3, 2, 'Sample PDF Text', 'sample.pdf', 'PDF', 'Researchers compare texts from different sources. They count words and study language patterns in every text. A careful comparison can show common words between texts.\n\nA library can store each text for research. Researchers can search for a word, analyse the text, and save important fragments. Frequent words help researchers understand the language of a text.\n\nThe research team reviews the results together. They compare language data from several texts and record the most important findings.'),
    (4, 1, 'Climate Research', 'climate-research.pdf', 'PDF', 'Climate researchers collect temperature records from different regions. The records show rising temperatures over recent decades. Researchers compare the results and prepare reports.'),
    (5, 1, 'Literature Review', 'literature-review.pdf', 'PDF', 'The review examines several studies about digital learning. The studies describe student engagement and online collaboration. The review identifies common research methods and conclusions.'),
    (6, 1, 'Survey Results', 'survey-results.pdf', 'PDF', 'The survey gathered responses from university students. Most students reported that clear feedback improves learning. The results also show that students value flexible schedules.');

INSERT INTO search_results (id, user_id, text_id, query, results_data)
VALUES (1, 1, 1, 'sample', '"Search results data. Data format is not specified yet"');

INSERT INTO text_analysis (id, user_id, text_id, analysis_data)
VALUES
    (1, 1, 4, '{"totalWords":22,"uniqueWords":17,"totalSentences":3,"totalParagraphs":1,"frequentWords":[{"word":"researchers","count":3,"relativeFrequency":136.4,"page":null,"paragraph":1},{"word":"records","count":2,"relativeFrequency":90.9,"page":null,"paragraph":1},{"word":"temperatures","count":2,"relativeFrequency":90.9,"page":null,"paragraph":1}],"importantFragments":[{"text":"Climate researchers collect temperature records from different regions.","score":8,"page":null,"paragraph":1},{"text":"The records show rising temperatures over recent decades.","score":6,"page":null,"paragraph":1}]}'),
    (2, 1, 5, '{"totalWords":21,"uniqueWords":16,"totalSentences":3,"totalParagraphs":1,"frequentWords":[{"word":"review","count":2,"relativeFrequency":95.2,"page":null,"paragraph":1},{"word":"studies","count":2,"relativeFrequency":95.2,"page":null,"paragraph":1},{"word":"research","count":1,"relativeFrequency":47.6,"page":null,"paragraph":1}],"importantFragments":[{"text":"The review examines several studies about digital learning.","score":7,"page":null,"paragraph":1},{"text":"The review identifies common research methods and conclusions.","score":7,"page":null,"paragraph":1}]}'),
    (3, 1, 6, '{"totalWords":22,"uniqueWords":18,"totalSentences":3,"totalParagraphs":1,"frequentWords":[{"word":"students","count":3,"relativeFrequency":136.4,"page":null,"paragraph":1},{"word":"results","count":2,"relativeFrequency":90.9,"page":null,"paragraph":1},{"word":"learning","count":1,"relativeFrequency":45.5,"page":null,"paragraph":1}],"importantFragments":[{"text":"Most students reported that clear feedback improves learning.","score":8,"page":null,"paragraph":1},{"text":"The results also show that students value flexible schedules.","score":7,"page":null,"paragraph":1}]}');

INSERT INTO term_analysis (id, user_id, text_id, term, analysis_data)
VALUES (1, 1, 4, 'researchers', '{"term":"researchers","totalOccurrences":3,"relativeFrequency":136.4,"sentenceCount":3,"paragraphCount":1,"neighboringWords":[{"word":"climate","count":2,"relativeFrequency":90.9,"page":null,"paragraph":1},{"word":"records","count":2,"relativeFrequency":90.9,"page":null,"paragraph":1},{"word":"results","count":1,"relativeFrequency":45.5,"page":null,"paragraph":1}]}');

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
