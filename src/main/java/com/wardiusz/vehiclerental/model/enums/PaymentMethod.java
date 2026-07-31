package com.wardiusz.vehiclerental.model.enums;

public enum PaymentMethod {
    CASH("Gotówka"),
    CARD("Karta"),
    TRANSFER("Przelew"),
    BLIK("BLIK");

    private final String label;

    PaymentMethod(String label) {
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
