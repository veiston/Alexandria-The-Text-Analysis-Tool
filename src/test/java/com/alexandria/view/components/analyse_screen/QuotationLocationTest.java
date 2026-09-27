package com.alexandria.view.components.analyse_screen;

import com.alexandria.model.QuotationType;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuotationLocationTest {

    @Test
    public void encode_withType_prependsTypePrefix() {
        String result = QuotationLocation.encode(QuotationType.DIRECT, "page:1;offset:10-20");
        assertEquals("type:DIRECT;page:1;offset:10-20", result);
    }

    @Test
    public void encode_withNullType_returnsRawLocationUnchanged() {
        assertEquals("page:1;offset:10-20",
                QuotationLocation.encode(null, "page:1;offset:10-20"));
    }

    @Test
    public void parseType_validLocation_returnsType() {
        assertEquals(QuotationType.DIRECT,
                QuotationLocation.parseType("type:DIRECT;page:1;offset:10-20"));
    }

    @Test
    public void parseType_nullLocation_returnsNull() {
        assertNull(QuotationLocation.parseType(null));
    }

    @Test
    public void parseType_noTypeSegment_returnsNull() {
        assertNull(QuotationLocation.parseType("page:1;offset:10-20"));
    }

    @Test
    public void parseType_invalidEnumName_returnsNull() {
        assertNull(QuotationLocation.parseType("type:NOTAREALTYPE;page:1"));
    }

    @Test
    public void parsePage_validLocation_returnsPage() {
        assertEquals(Integer.valueOf(3),
                QuotationLocation.parsePage("type:DIRECT;page:3;offset:10-20"));
    }

    @Test
    public void parsePage_missingPage_returnsNull() {
        assertNull(QuotationLocation.parsePage("type:DIRECT;offset:10-20"));
    }

    @Test
    public void parsePage_nullLocation_returnsNull() {
        assertNull(QuotationLocation.parsePage(null));
    }

    @Test
    public void withoutType_stripsPrefix() {
        assertEquals("page:1;offset:10-20",
                QuotationLocation.withoutType("type:DIRECT;page:1;offset:10-20"));
    }

    @Test
    public void withoutType_noPrefix_returnsUnchanged() {
        assertEquals("page:1;offset:10-20",
                QuotationLocation.withoutType("page:1;offset:10-20"));
    }

    @Test
    public void withoutType_nullLocation_returnsNull() {
        assertNull(QuotationLocation.withoutType(null));
    }

    @Test
    public void parseStartOffset_and_parseEndOffset_validLocation() {
        String location = "type:DIRECT;page:1;offset:10-20";
        assertEquals(Integer.valueOf(10), QuotationLocation.parseStartOffset(location));
        assertEquals(Integer.valueOf(20), QuotationLocation.parseEndOffset(location));
    }

    @Test
    public void parseStartOffset_missingOffset_returnsNull() {
        assertNull(QuotationLocation.parseStartOffset("type:DIRECT;page:1"));
    }

    @Test
    public void parseEndOffset_missingOffset_returnsNull() {
        assertNull(QuotationLocation.parseEndOffset("type:DIRECT;page:1"));
    }
}
