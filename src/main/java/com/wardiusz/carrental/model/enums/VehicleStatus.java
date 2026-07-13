package com.wardiusz.carrental.model.enums;

// Tylko DOSTĘPNY może zostać zarezerwowany
public enum VehicleStatus {
    AVAILABLE("Dostępny"),
    RESERVED("Zarezerwowany"),
    RENTED("Wynajęty"),
    IN_SERVICE("W serwisie"),
    DAMAGED("Uszkodzony");

    private final String label;

    VehicleStatus(String label) {
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
