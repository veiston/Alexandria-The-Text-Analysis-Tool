package com.alexandria.service;

import com.alexandria.dao.TextAnalysisDAO;
import com.alexandria.dao.TextDAO;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.model.TextAnalysis;
import com.alexandria.service.analysis.TextAnalysisResult;
import com.alexandria.utils.JsonMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArchiveTextAnalysisServiceTest {

	@Mock
	private TextAnalysisDAO textAnalysisDAO;

	@Mock
	private TextDAO textDAO;

	private ArchiveTextAnalysisService archiveTextAnalysisService;

	@Before
	public void setUp() {
		archiveTextAnalysisService = new ArchiveTextAnalysisService(textAnalysisDAO, textDAO);
	}

	@Test
	public void savesTextAnalysis() throws SQLException {
		Text text = new Text(1, 1, "Test text", "test.txt", FileType.TXT, "Test text.", null);
		TextAnalysisResult result = new TextAnalysisResult(2, 2, 1, 1, List.of(), List.of());
		TextAnalysis expectedSavedTextAnalysis = new TextAnalysis(1, 1, 1, "{}", null);

		when(textAnalysisDAO.create(any(TextAnalysis.class))).thenReturn(expectedSavedTextAnalysis);

		TextAnalysis savedTextAnalysis = archiveTextAnalysisService.save(text, result);

		assertSame(expectedSavedTextAnalysis, savedTextAnalysis);
		verify(textAnalysisDAO).create(any(TextAnalysis.class));
	}

	@Test
	public void findsTextAnalysis() throws SQLException {
		TextAnalysis textAnalysis = savedTextAnalysis();
		when(textAnalysisDAO.findById(1)).thenReturn(textAnalysis);
		when(textDAO.findById(1)).thenReturn(text());

		ArchiveTextAnalysis archiveTextAnalysis = archiveTextAnalysisService.findById(1);

		assertEquals("Test text", archiveTextAnalysis.getProjectTitle());
		assertEquals("test.txt", archiveTextAnalysis.getSourceFileName());
	}

	@Test
	public void findsTextAnalysesByUser() throws SQLException {
		when(textAnalysisDAO.findAllByUserId(1)).thenReturn(List.of(savedTextAnalysis()));
		when(textDAO.findById(1)).thenReturn(text());

		List<ArchiveTextAnalysis> archiveTextAnalyses = archiveTextAnalysisService.findAllByUserId(1);

		assertEquals(1, archiveTextAnalyses.size());
	}

	@Test
	public void findsTextAnalysesByText() throws SQLException {
		when(textAnalysisDAO.findAllByTextId(1)).thenReturn(List.of(savedTextAnalysis()));
		when(textDAO.findById(1)).thenReturn(text());

		List<ArchiveTextAnalysis> archiveTextAnalyses = archiveTextAnalysisService.findAllByTextId(1);

		assertEquals(1, archiveTextAnalyses.size());
	}

	@Test
	public void deletesTextAnalysis() throws SQLException {
		when(textAnalysisDAO.delete(1)).thenReturn(true);

		boolean deleted = archiveTextAnalysisService.deleteById(1);

		assertEquals(true, deleted);
	}

	private Text text() {
		return new Text(1, 1, "Test text", "test.txt", FileType.TXT, "Test text.", null);
	}

	private TextAnalysis savedTextAnalysis() {
		TextAnalysisResult result = new TextAnalysisResult(2, 2, 1, 1, List.of(), List.of());
		return new TextAnalysis(1, 1, 1, JsonMapper.toJson(result), LocalDateTime.now());
	}
}
