package com.wardiusz.vehiclerental.model.enums;

import java.util.EnumSet;
import java.util.Set;

public enum ReservationStatus {
    PENDING("Oczekująca"),
    CONFIRMED("Potwierdzona"),
    ACTIVE("Aktywna"),
    COMPLETED("Zakończona"),
    CANCELLED("Anulowana");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Set<ReservationStatus> allowedTransitions() {
        return switch (this) {
            case PENDING -> EnumSet.of(CONFIRMED, CANCELLED);
            case CONFIRMED -> EnumSet.of(ACTIVE, CANCELLED);
            case ACTIVE -> EnumSet.of(COMPLETED);
            default -> EnumSet.noneOf(ReservationStatus.class);
        };
    }

    public boolean canTransitionTo(ReservationStatus target) {
        return allowedTransitions().contains(target);
    }

    @Override
    public String toString() {
        return label;
    }
}
