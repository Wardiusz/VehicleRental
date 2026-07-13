package com.wardiusz.carrental.model.enums;

public enum FuelType {
    PETROL("Benzyna"),
    DIESEL("Diesel"),
    LPG("LPG"),
    HYBRID("Hybryda"),
    ELECTRIC("Elektryczny");

    private final String label;

    FuelType(String label) {
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
