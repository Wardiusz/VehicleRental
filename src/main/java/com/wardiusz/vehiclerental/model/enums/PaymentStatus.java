package com.wardiusz.vehiclerental.model.enums;

public enum PaymentStatus {
    PENDING("Oczekująca"),
    PAID("Opłacona"),
    REFUNDED("Zwrócona"),
    FAILED("Nieudana");

    private final String label;

    PaymentStatus(String label) {
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
