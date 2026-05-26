package com.br.shizen.certificateemitter.google.domain;

public enum GoogleDriveMimeType {
    AUDIO("application/vnd.google-apps.audio"),
    DOCS("application/vnd.google-apps.document"),
    RD_PARTY_SHORTCUT("application/vnd.google-apps.drive-sdk"),
    DRAWING("application/vnd.google-apps.drawing"),
    DRIVE_FILE("application/vnd.google-apps.file"),
    DRIVE_FOLDER("application/vnd.google-apps.folder"),
    FORMS("application/vnd.google-apps.form"),
    FUSION_TABLES("application/vnd.google-apps.fusiontable"),
    MAPS("application/vnd.google-apps.map"),
    PHOTO("application/vnd.google-apps.photo"),
    SLIDES("application/vnd.google-apps.presentation"),
    APPS_SCRIPTS("application/vnd.google-apps.script"),
    SHORTCUT("application/vnd.google-apps.shortcut"),
    SITE("application/vnd.google-apps.site"),
    SHEETS("application/vnd.google-apps.spreadsheet"),
    UNKNOWN("application/vnd.google-apps.unknown"),
    VIDEO("application/vnd.google-apps.video");

    private final String name;

    GoogleDriveMimeType(String s) {
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
