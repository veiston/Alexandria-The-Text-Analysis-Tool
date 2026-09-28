package com.alexandria.controller;

import com.alexandria.model.Quotation;
import com.alexandria.service.QuotationService;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class QuotationControllerTest {

    private QuotationService quotationService;
    private QuotationController controller;

    private Quotation quotation1;
    private Quotation quotation2;

    @Before
    public void setUp() {
        quotationService = mock(QuotationService.class);
        controller = new QuotationController(quotationService);

        quotation1 = mock(Quotation.class);
        quotation2 = mock(Quotation.class);

        when(quotation1.getId()).thenReturn(1);
        when(quotation2.getId()).thenReturn(2);
    }

    // ---------------------------------------------------------
    // openText
    // ---------------------------------------------------------

    @Test
    public void openText_loadsQuotations() throws SQLException {
        when(quotationService.findAllByTextId(10, 5))
                .thenReturn(List.of(quotation1, quotation2));

        controller.openText(5, 10);

        assertEquals(
                List.of(quotation1, quotation2),
                controller.getQuotations()
        );

        verify(quotationService).findAllByTextId(10, 5);
    }

    @Test
    public void openText_withNoQuotations_createsEmptyList()
            throws SQLException {

        when(quotationService.findAllByTextId(10, 5))
                .thenReturn(List.of());

        controller.openText(5, 10);

        assertTrue(controller.getQuotations().isEmpty());

        verify(quotationService).findAllByTextId(10, 5);
    }

    @Test
    public void openText_replacesPreviousQuotations()
            throws SQLException {

        when(quotationService.findAllByTextId(10, 5))
                .thenReturn(List.of(quotation1));

        controller.openText(5, 10);

        assertEquals(List.of(quotation1), controller.getQuotations());

        when(quotationService.findAllByTextId(20, 6))
                .thenReturn(List.of(quotation2));

        controller.openText(6, 20);

        assertEquals(List.of(quotation2), controller.getQuotations());

        verify(quotationService).findAllByTextId(10, 5);
        verify(quotationService).findAllByTextId(20, 6);
    }

    // ---------------------------------------------------------
    // addQuotation
    // ---------------------------------------------------------

    @Test
    public void addQuotation_withoutUser_returnsNull()
            throws SQLException {

        Quotation result =
                controller.addQuotation("A quotation", "page 1");

        assertNull(result);

        verify(quotationService, never())
                .create(anyInt(), anyInt(), anyString(), any());
    }

    @Test
    public void addQuotation_withoutText_returnsNull()
            throws SQLException {

        // Establish a user, but no text.
        // There is no public API for setting only the user, so
        // this case is represented by the initial controller state.
        Quotation result =
                controller.addQuotation(null, "page 1");

        assertNull(result);

        verify(quotationService, never())
                .create(anyInt(), anyInt(), anyString(), any());
    }

    @Test
    public void addQuotation_withNullQuotationText_returnsNull()
            throws SQLException {

        controller.openText(5, 10);

        Quotation result =
                controller.addQuotation(null, "page 1");

        assertNull(result);

        verify(quotationService, never())
                .create(anyInt(), anyInt(), anyString(), any());
    }

    @Test
    public void addQuotation_withBlankQuotationText_returnsNull()
            throws SQLException {

        controller.openText(5, 10);

        Quotation result =
                controller.addQuotation("   ", "page 1");

        assertNull(result);

        verify(quotationService, never())
                .create(anyInt(), anyInt(), anyString(), any());
    }

    @Test
    public void addQuotation_withEmptyQuotationText_returnsNull()
            throws SQLException {

        controller.openText(5, 10);

        Quotation result =
                controller.addQuotation("", "page 1");

        assertNull(result);

        verify(quotationService, never())
                .create(anyInt(), anyInt(), anyString(), any());
    }

    @Test
    public void addQuotation_createsAndAddsQuotation()
            throws SQLException {

        controller.openText(5, 10);

        when(quotationService.create(
                5,
                10,
                "A quotation",
                "page 1"
        )).thenReturn(quotation1);

        Quotation result =
                controller.addQuotation("A quotation", "page 1");

        assertSame(quotation1, result);
        assertEquals(List.of(quotation1), controller.getQuotations());

        verify(quotationService).create(
                5,
                10,
                "A quotation",
                "page 1"
        );
    }

    @Test
    public void addQuotation_addsMultipleQuotations()
            throws SQLException {

        controller.openText(5, 10);

        when(quotationService.create(
                5, 10, "First", "page 1"
        )).thenReturn(quotation1);

        when(quotationService.create(
                5, 10, "Second", "page 2"
        )).thenReturn(quotation2);

        controller.addQuotation("First", "page 1");
        controller.addQuotation("Second", "page 2");

        assertEquals(
                List.of(quotation1, quotation2),
                controller.getQuotations()
        );
    }

    // ---------------------------------------------------------
    // removeQuotation
    // ---------------------------------------------------------

    @Test
    public void removeQuotation_withoutContext_returnsFalse()
            throws SQLException {

        boolean result = controller.removeQuotation(1);

        assertFalse(result);

        verify(quotationService, never())
                .deleteById(anyInt(), anyInt());
    }

    @Test
    public void removeQuotation_whenServiceDeleteFails_returnsFalse()
            throws SQLException {

        controller.openText(5, 10);

        when(quotationService.deleteById(1, 5))
                .thenReturn(false);

        boolean result = controller.removeQuotation(1);

        assertFalse(result);

        verify(quotationService).deleteById(1, 5);
        assertTrue(controller.getQuotations().isEmpty());
    }

    @Test
    public void removeQuotation_whenServiceDeleteSucceeds_removesQuotation()
            throws SQLException {

        when(quotationService.findAllByTextId(10, 5))
                .thenReturn(List.of(quotation1, quotation2));

        controller.openText(5, 10);

        when(quotationService.deleteById(1, 5))
                .thenReturn(true);

        boolean result = controller.removeQuotation(1);

        assertTrue(result);

        assertEquals(
                List.of(quotation2),
                controller.getQuotations()
        );

        verify(quotationService).deleteById(1, 5);
    }

    @Test
    public void removeQuotation_whenServiceDeleteSucceeds_butQuotationNotInList()
            throws SQLException {

        controller.openText(5, 10);

        when(quotationService.deleteById(99, 5))
                .thenReturn(true);

        boolean result = controller.removeQuotation(99);

        assertFalse(result);

        verify(quotationService).deleteById(99, 5);
    }

    @Test
    public void removeQuotation_onlyRemovesMatchingQuotation()
            throws SQLException {

        when(quotationService.findAllByTextId(10, 5))
                .thenReturn(List.of(quotation1, quotation2));

        controller.openText(5, 10);

        when(quotationService.deleteById(2, 5))
                .thenReturn(true);

        assertTrue(controller.removeQuotation(2));

        assertEquals(
                List.of(quotation1),
                controller.getQuotations()
        );
    }

    // ---------------------------------------------------------
    // updateQuotation
    // ---------------------------------------------------------

    @Test
    public void updateQuotation_withoutContext_returnsFalse()
            throws SQLException {

        boolean result =
                controller.updateQuotation(quotation1);

        assertFalse(result);

        verify(quotationService, never())
                .update(any(Quotation.class), anyInt());
    }

    @Test
    public void updateQuotation_withContext_delegatesToService()
            throws SQLException {

        controller.openText(5, 10);

        when(quotationService.update(quotation1, 5))
                .thenReturn(true);

        boolean result =
                controller.updateQuotation(quotation1);

        assertTrue(result);

        verify(quotationService).update(quotation1, 5);
    }

    @Test
    public void updateQuotation_whenServiceReturnsFalse_returnsFalse()
            throws SQLException {

        controller.openText(5, 10);

        when(quotationService.update(quotation1, 5))
                .thenReturn(false);

        boolean result =
                controller.updateQuotation(quotation1);

        assertFalse(result);

        verify(quotationService).update(quotation1, 5);
    }

    // ---------------------------------------------------------
    // reloadQuotations
    // ---------------------------------------------------------

    @Test
    public void reloadQuotations_withoutValidContext_clearsList()
            throws SQLException {

        controller.reloadQuotations();

        assertTrue(controller.getQuotations().isEmpty());

        verify(quotationService, never())
                .findAllByTextId(anyInt(), anyInt());
    }

    @Test
    public void reloadQuotations_withValidContext_reloadsQuotations()
            throws SQLException {

        when(quotationService.findAllByTextId(10, 5))
                .thenReturn(List.of(quotation1, quotation2));

        controller.openText(5, 10);

        // openText already loads once.
        clearInvocations(quotationService);

        when(quotationService.findAllByTextId(10, 5))
                .thenReturn(List.of(quotation2));

        controller.reloadQuotations();

        assertEquals(
                List.of(quotation2),
                controller.getQuotations()
        );

        verify(quotationService).findAllByTextId(10, 5);
    }

    @Test
    public void reloadQuotations_replacesOldQuotations()
            throws SQLException {

        when(quotationService.findAllByTextId(10, 5))
                .thenReturn(List.of(quotation1));

        controller.openText(5, 10);

        assertEquals(
                List.of(quotation1),
                controller.getQuotations()
        );

        when(quotationService.findAllByTextId(10, 5))
                .thenReturn(List.of(quotation2));

        controller.reloadQuotations();

        assertEquals(
                List.of(quotation2),
                controller.getQuotations()
        );
    }

    @Test
    public void reloadQuotations_withNoResults_clearsList()
            throws SQLException {

        when(quotationService.findAllByTextId(10, 5))
                .thenReturn(List.of(quotation1));

        controller.openText(5, 10);

        assertFalse(controller.getQuotations().isEmpty());

        when(quotationService.findAllByTextId(10, 5))
                .thenReturn(List.of());

        controller.reloadQuotations();

        assertTrue(controller.getQuotations().isEmpty());
    }

    // ---------------------------------------------------------
    // getQuotations
    // ---------------------------------------------------------

    @Test
    public void getQuotations_returnsEmptyListInitially() {

        assertNotNull(controller.getQuotations());
        assertTrue(controller.getQuotations().isEmpty());
    }

    @Test
    public void getQuotations_returnsCopyOfInternalList()
            throws SQLException {

        when(quotationService.findAllByTextId(10, 5))
                .thenReturn(List.of(quotation1));

        controller.openText(5, 10);

        List<Quotation> result = controller.getQuotations();

        assertEquals(List.of(quotation1), result);

        try {
            result.clear();
        } catch (UnsupportedOperationException expected) {
            // Expected because List.copyOf() returns an unmodifiable list.
        }

        assertEquals(
                List.of(quotation1),
                controller.getQuotations()
        );
    }
}
