package com.alexandria.controller;

import com.alexandria.model.FileType;
import com.alexandria.model.Quotation;
import com.alexandria.model.Text;
import com.alexandria.service.analysis.*;
import com.alexandria.view.screens.AnalyseScreen;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AnalyseControllerConfigureScreenTest {

    private SearchServiceINT searchService;
    private TermAnalysisServiceINT termAnalysisService;
    private TextAnalysisServiceINT textAnalysisService;
    private AnalyseController controller;
    private AnalyseScreen screen;
    private Text text;

    @Before
    public void setUp() {
        searchService = mock(SearchServiceINT.class);
        termAnalysisService = mock(TermAnalysisServiceINT.class);
        textAnalysisService = mock(TextAnalysisServiceINT.class);
        screen = mock(AnalyseScreen.class);

        controller = new AnalyseController(searchService, termAnalysisService, textAnalysisService);

        // userId null + unpersisted (id null) -> QuotationController's
        // canSaveQuotations() stays false, so nothing here ever touches
        // a real database, regardless of what other tests logged in.
        text = new Text(null, "Title", "file.txt", FileType.MANUAL, "some document content");

        TextAnalysisResult analysisResult = mock(TextAnalysisResult.class);
        when(analysisResult.frequentWords()).thenReturn(List.of());
        when(analysisResult.importantFragments()).thenReturn(List.of());
        when(textAnalysisService.analyzeText(anyString(), anyList())).thenReturn(analysisResult);
    }

    @Test
    public void configureScreen_wiresAllCallbacks() {
        controller.configureScreen(screen, text, List.of(), null);

        verify(screen).loadDocument(
                eq("Title"), eq("file.txt"), eq("some document content"),
                eq(FileType.MANUAL), isNull(), eq(List.of()));

        verify(screen).setQuotations(anyList());
        verify(screen).setTextAnalysis(anyList(), anyList());

        verify(screen).setOnTermDetailRequested(any());
        verify(screen).setOnQuotationRequested(any());
        verify(screen).setOnQuotationDeleteRequested(any());
        verify(screen).setOnQuotationEditRequested(any());
        verify(screen).setOnSearch(any());
        verify(screen).setOnPreviousMatch(any());
        verify(screen).setOnNextMatch(any());
    }

    @Test
    public void configureScreen_analysisFailure_stopsBeforeWiringSearchCallbacks() {
        when(textAnalysisService.analyzeText(anyString(), anyList()))
                .thenThrow(new RuntimeException("boom"));

        controller.configureScreen(screen, text, List.of(), null);

        // loadDocument and setQuotations happen unconditionally, before
        // the success check — only the post-check wiring should be skipped.
        verify(screen).loadDocument(any(), any(), any(), any(), any(), any());
        verify(screen).setQuotations(anyList());
        verify(screen, never()).setTextAnalysis(any(), any());
        verify(screen, never()).setOnSearch(any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void termDetailCallback_successfulSearch_showsTermDetail() {
        controller.configureScreen(screen, text, List.of(), null);

        ArgumentCaptor<Consumer<String>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(screen).setOnTermDetailRequested(captor.capture());

        SearchMatch match = new SearchMatch("alice", 0, 5, 1, null, "context");
        TermAnalysisResult termResult = mock(TermAnalysisResult.class);

        when(searchService.search(anyString(), eq("alice"), any(SearchSettings.class), anyList()))
                .thenReturn(List.of(match));
        when(termAnalysisService.analyzeTerm(anyString(), eq("alice")))
                .thenReturn(termResult);

        captor.getValue().accept("alice");

        verify(screen).showTermDetail(eq("alice"), eq(termResult), eq(List.of(match)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void termDetailCallback_blankTerm_doesNotShowTermDetail() {
        controller.configureScreen(screen, text, List.of(), null);

        ArgumentCaptor<Consumer<String>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(screen).setOnTermDetailRequested(captor.capture());

        captor.getValue().accept("   "); // search(...) returns SearchOutcome.error(...)

        verify(screen, never()).showTermDetail(any(), any(), any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void quotationRequestedCallback_guestText_returnsNull_andDoesNotUpdateScreenAgain() {
        controller.configureScreen(screen, text, List.of(), null);

        ArgumentCaptor<BiFunction<String, String, Integer>> captor = ArgumentCaptor.forClass(BiFunction.class);
        verify(screen).setOnQuotationRequested(captor.capture());

        clearInvocations(screen); // drop configureScreen's own setQuotations(...) call

        Integer resultId = captor.getValue().apply("a quote", "page:1;offset:0-5");

        assertNull(resultId);
        verify(screen, never()).setQuotations(anyList());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void quotationDeleteCallback_unknownId_doesNothing() {
        controller.configureScreen(screen, text, List.of(), null);

        ArgumentCaptor<Consumer<Quotation>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(screen).setOnQuotationDeleteRequested(captor.capture());

        Quotation quotation = mock(Quotation.class);
        when(quotation.getId()).thenReturn(999);

        captor.getValue().accept(quotation);

        verify(screen, never()).removeQuotationHighlight(anyInt());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void quotationDeleteCallback_nullQuotation_doesNotThrow() {
        controller.configureScreen(screen, text, List.of(), null);

        ArgumentCaptor<Consumer<Quotation>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(screen).setOnQuotationDeleteRequested(captor.capture());

        captor.getValue().accept(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void quotationEditCallback_nullQuotation_doesNotThrow() {
        controller.configureScreen(screen, text, List.of(), null);

        ArgumentCaptor<Consumer<Quotation>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(screen).setOnQuotationEditRequested(captor.capture());

        captor.getValue().accept(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void searchCallback_success_showsSearchResults() {
        controller.configureScreen(screen, text, List.of(), null);

        ArgumentCaptor<Consumer<String>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(screen).setOnSearch(captor.capture());

        SearchMatch match = new SearchMatch("bob", 0, 3, 1, null, "context");
        when(searchService.search(anyString(), eq("bob"), any(SearchSettings.class), anyList()))
                .thenReturn(List.of(match));

        captor.getValue().accept("bob");

        verify(screen).showSearchResults(eq("bob"), eq(List.of(match)), any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void searchCallback_blankTerm_doesNotShowResults() {
        controller.configureScreen(screen, text, List.of(), null);

        ArgumentCaptor<Consumer<String>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(screen).setOnSearch(captor.capture());

        captor.getValue().accept("");

        verify(screen, never()).showSearchResults(any(), any(), any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void previousAndNextMatchCallbacks_noActiveSearch_doNothing() {
        controller.configureScreen(screen, text, List.of(), null);

        ArgumentCaptor<Runnable> prevCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Runnable> nextCaptor = ArgumentCaptor.forClass(Runnable.class);

        verify(screen).setOnPreviousMatch(prevCaptor.capture());
        verify(screen).setOnNextMatch(nextCaptor.capture());

        prevCaptor.getValue().run();
        nextCaptor.getValue().run();

        verify(screen, never()).showSearchMatch(anyList(), anyInt());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void nextMatchCallback_withActiveSearch_showsNextMatch() {
        controller.configureScreen(screen, text, List.of(), null);

        SearchMatch match = new SearchMatch("bob", 0, 3, 1, null, "context");
        when(searchService.search(anyString(), eq("bob"), any(SearchSettings.class), anyList()))
                .thenReturn(List.of(match));

        ArgumentCaptor<Consumer<String>> searchCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(screen).setOnSearch(searchCaptor.capture());
        searchCaptor.getValue().accept("bob");

        ArgumentCaptor<Runnable> nextCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(screen).setOnNextMatch(nextCaptor.capture());
        nextCaptor.getValue().run();

        verify(screen, atLeastOnce()).showSearchMatch(eq(List.of(match)), eq(0));
    }
}