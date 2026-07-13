package com.wardiusz.carrental.model.enums;

public enum CarType {
    SEDAN("Sedan"),
    HATCHBACK("Hatchback"),
    SUV("SUV"),
    KOMBI("Kombi"),
    COUPE("Coupe"),
    CABRIO("Kabriolet");

    private final String label;

    CarType(String label) {
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
