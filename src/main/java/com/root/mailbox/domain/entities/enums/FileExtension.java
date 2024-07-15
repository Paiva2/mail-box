package com.root.mailbox.domain.entities.enums;

public enum FileExtension {
    PDF,
    JPEG,
    JPG,
    XLSX,
    XLS,
    CSV,
    TXT;

    public String getExtension() {
        return this.name();
    }
}
