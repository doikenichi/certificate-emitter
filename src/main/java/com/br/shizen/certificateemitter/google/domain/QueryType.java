package com.br.shizen.certificateemitter.google.domain;

public enum QueryType {
    MIME_TYPE("mimeType"),
    NAME("name"),
    FILE_CONTENT("fullText");

    private final String name;

    QueryType(String s) {
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
