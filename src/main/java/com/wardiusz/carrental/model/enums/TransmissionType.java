package com.wardiusz.carrental.model.enums;

public enum TransmissionType {
    MANUAL("Manualna"),
    AUTOMATIC("Automatyczna");

    private final String label;

    TransmissionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
