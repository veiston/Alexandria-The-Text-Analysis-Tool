package com.alexandria.service;

import com.alexandria.dao.TermAnalysisDAO;
import com.alexandria.dao.TextDAO;
import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.FileType;
import com.alexandria.model.TermAnalysis;
import com.alexandria.model.Text;
import com.alexandria.service.analysis.TermAnalysisResult;
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
public class ArchiveTermAnalysisServiceTest {

	@Mock
	private TermAnalysisDAO termAnalysisDAO;

	@Mock
	private TextDAO textDAO;

	private ArchiveTermAnalysisService archiveTermAnalysisService;

	@Before
	public void setUp() {
		archiveTermAnalysisService = new ArchiveTermAnalysisService(termAnalysisDAO, textDAO);
	}

	@Test
	public void savesTermAnalysis() throws SQLException {
		Text text = new Text(1, 1, "Test text", "test.txt", FileType.TXT, "Test text.", null);
		TermAnalysisResult result = new TermAnalysisResult("test", 1, 500.0, 1, 1, List.of());
		TermAnalysis expectedSavedTermAnalysis = new TermAnalysis(1, 1, 1, "test", "{}", null);

		when(termAnalysisDAO.create(any(TermAnalysis.class))).thenReturn(expectedSavedTermAnalysis);

		TermAnalysis savedTermAnalysis = archiveTermAnalysisService.save(text, result);

		assertSame(expectedSavedTermAnalysis, savedTermAnalysis);
		verify(termAnalysisDAO).create(any(TermAnalysis.class));
	}

	@Test
	public void findsTermAnalysis() throws SQLException {
		TermAnalysis termAnalysis = savedTermAnalysis();
		when(termAnalysisDAO.findById(1)).thenReturn(termAnalysis);
		when(textDAO.findById(1)).thenReturn(text());

		ArchiveTermAnalysis archiveTermAnalysis = archiveTermAnalysisService.findById(1);

		assertEquals("test", archiveTermAnalysis.getTerm());
		assertEquals("test.txt", archiveTermAnalysis.getSourceFileName());
	}

	@Test
	public void findsTermAnalysesByUser() throws SQLException {
		when(termAnalysisDAO.findAllByUserId(1)).thenReturn(List.of(savedTermAnalysis()));
		when(textDAO.findById(1)).thenReturn(text());

		List<ArchiveTermAnalysis> archiveTermAnalyses = archiveTermAnalysisService.findAllByUserId(1);

		assertEquals(1, archiveTermAnalyses.size());
	}

	@Test
	public void findsTermAnalysesByText() throws SQLException {
		when(termAnalysisDAO.findAllByTextId(1)).thenReturn(List.of(savedTermAnalysis()));
		when(textDAO.findById(1)).thenReturn(text());

		List<ArchiveTermAnalysis> archiveTermAnalyses = archiveTermAnalysisService.findAllByTextId(1);

		assertEquals(1, archiveTermAnalyses.size());
	}

	@Test
	public void deletesTermAnalysis() throws SQLException {
		when(termAnalysisDAO.delete(1)).thenReturn(true);

		boolean deleted = archiveTermAnalysisService.deleteById(1);

		assertEquals(true, deleted);
	}

	private Text text() {
		return new Text(1, 1, "Test text", "test.txt", FileType.TXT, "Test text.", null);
	}

	private TermAnalysis savedTermAnalysis() {
		TermAnalysisResult result = new TermAnalysisResult("test", 1, 500.0, 1, 1, List.of());
		return new TermAnalysis(1, 1, 1, "test", JsonMapper.toJson(result), LocalDateTime.now());
	}
}
