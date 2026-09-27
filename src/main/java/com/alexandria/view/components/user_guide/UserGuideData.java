package com.alexandria.view.components.user_guide;

import java.util.List;

public final class UserGuideData {

    public static final List<String> ABOUT_PARAGRAPHS = List.of(
            "Alexandria is an application for working with texts.",
            "You can add your own text or upload a TXT or PDF file. Alexandria can search for the words "
                    + "and phrases you need, count words and sentences, show which words are used most often, "
                    + "analyze a specific word or phrase, and compare several texts.",
            "You can also save your texts, analysis results, comparisons, search results, and quotations to your account.");

    public static final List<String> NEW_PROJECT_STEPS = List.of(
            "Click + New Project in the sidebar to add a text.",
            "Choose Upload File or Paste Text. Only PDF and TXT files are supported.",
            "Enter the project name.",
            "Choose where to open your project.\n\n"
                    + "Analyze lets you explore one text, search for words or phrases, and view its statistics.\n\n"
                    + "Compare lets you compare word use across several texts.",
            "Click Create Project. You will be taken to either the Analyze or Compare page, depending on "
                    + "your selection. Your project will be added to your Library so you can return to it later.");

    public static final String ANALYZE_INTRODUCTION = "Create a new project and choose Analyze, or open an existing project from Library. Analyze works with one text at a time. Reader is shown on the left, while the panel on the right shows automatically calculated Term Frequency and Key Paragraphs. The selected text remains available in Library, so you can return to the same project later.";
    public static final List<String> ANALYZE_METHODS = List.of(
            "Term Frequency: returns the five non-stop words with the highest raw occurrence counts. The fixed stop-word list excludes: the, be, to, of, and, a, in, that, have, i, it, for, not, on, with, he, as, you, do, and at.",
            "Term analysis: matches a word or phrase without regard to case and reports total occurrences, occurrences per 1,000 words, the number of sentences and paragraphs containing the term, and the five most frequent neighbouring words. Neighbours are counted within five word positions on either side of each match. Stop words are excluded.",
            "Search Term Frequency: searches the current text for a word or phrase and returns every matching occurrence with its page, when page information is available, and up to 40 characters of surrounding context on each side. Search settings can control case sensitivity and whole-word matching.",
            "Fuzzy search: performs case-insensitive approximate matching. Queries are limited to 64 characters. Queries shorter than four characters allow no differences, queries of four to seven characters allow one difference, and longer queries allow up to two differences.",
            "Tracked words list: keeps searched words and phrases with their occurrence counts. It marks the active term in the document, provides term details, and supports navigation to individual occurrences or removal of a tracked term.",
            "Key Paragraphs: the text is split into sentences. Each sentence receives a score equal to the sum of the overall frequencies of its words. The five highest-scoring sentences are returned with their page and paragraph references.");

    public static final List<String> LIBRARY_STEPS = List.of(
            "Open Library to view the texts and projects saved to your account.",
            "Use the search field to find a text or project.",
            "Select a project and choose Open with.",
            "Choose Analyze to work with one text, or Compare to work with several texts.");

    public static final List<String> ARCHIVE_STEPS = List.of(
            "Sign in to access your Archive.",
            "Choose Text analysis, Term analysis, or Text comparisons.",
            "Use the search field to find saved findings.",
            "Open a saved result to review it, or remove it when you no longer need it.");

    public static final List<String> PROFILE_STEPS = List.of(
            "Open Profile from the sidebar.",
            "Choose Register to create an account, or Log in if you already have one.",
            "When signed in, update your profile details or change your password.",
            "Signing in lets you save projects and findings to your account.");

    public static final List<String> SETTINGS_STEPS = List.of(
            "Open Settings to manage application preferences.");
    public static final String SETTINGS_NOTE = "More settings will be available in a future version";

    private UserGuideData() {
    }
}
