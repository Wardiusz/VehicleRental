package com.wardiusz.vehiclerental.model.enums;

public enum MotorcycleType {
    SPORT("Sportowy"),
    CRUISER("Cruiser"),
    TOURING("Turystyczny"),
    SCOOTER("Skuter"),
    ENDURO("Enduro"),
    NAKED("Naked");

    private final String label;

    MotorcycleType(String label) {
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
