package com.br.shizen.certificateemitter.google.domain;

public enum GoogleDocsMimeType {
    HTML("text/html"),
    HTML_ZIPPED("application/zip"),
    PLAIN_TEXT("text/plain"),
    RICH_TEXT("application/rtf"),
    OPEN_OFFICE_DOC("application/vnd.oasis.opendocument.text"),
    PDF("application/pdf"),
    MS_WORD("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    EPUB("application/epub+zip"),
    MS_EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    OPEN_OFFICE_SHEET("application/x-vnd.oasis.opendocument.spreadsheet"),
    CSV("text/csv"),
    TSV("text/tab-separated-values"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    SVG("image/svg+xml"),
    MS_POWERPOINT("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    OPEN_OFFICE_PRESENTATION("application/vnd.oasis.opendocument.presentation"),
    JSON("application/vnd.google-apps.script+json");

    private final String name;

    GoogleDocsMimeType(String s) {
        this.name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return this.name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
