package com.alexandria.view.components.analyse_screen;

import com.alexandria.model.QuotationType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class QuotationLocation {

    private static final Pattern TYPE_PATTERN = Pattern.compile("type:([A-Za-z]+)");
    private static final Pattern PAGE_PATTERN = Pattern.compile("page:(\\d+)");
    private static final Pattern OFFSET_PATTERN = Pattern.compile("offset:(\\d+)-(\\d+)");

    private QuotationLocation() {
    }

    public static String encode(QuotationType type, String rawLocation) {
        if (type == null) {
            return rawLocation;
        }

        return "type:" + type.name() + ";" + rawLocation;
    }

    public static QuotationType parseType(String location) {
        if (location == null) {
            return null;
        }

        Matcher matcher = TYPE_PATTERN.matcher(location);

        if (!matcher.find()) {
            return null;
        }

        try {
            return QuotationType.valueOf(matcher.group(1));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Integer parsePage(String location) {
        if (location == null) {
            return null;
        }

        Matcher matcher = PAGE_PATTERN.matcher(location);

        return matcher.find() ? Integer.valueOf(matcher.group(1)) : null;
    }

    public static String withoutType(String location) {
        if (location == null) {
            return null;
        }

        return location.replaceFirst("^type:[A-Za-z]+;", "");
    }

    public static Integer parseStartOffset(String location) {
        if (location == null) {
            return null;
        }

        Matcher matcher = OFFSET_PATTERN.matcher(location);

        return matcher.find()
                ? Integer.valueOf(matcher.group(1))
                : null;
    }

    public static Integer parseEndOffset(String location) {
        if (location == null) {
            return null;
        }

        Matcher matcher = OFFSET_PATTERN.matcher(location);

        return matcher.find()
                ? Integer.valueOf(matcher.group(2))
                : null;
    }

}
