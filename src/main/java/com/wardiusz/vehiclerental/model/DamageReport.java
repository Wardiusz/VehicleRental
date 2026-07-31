package com.wardiusz.vehiclerental.model;

import com.wardiusz.vehiclerental.model.enums.VehicleStatus;

import java.io.Serial;
import java.time.LocalDate;
import java.util.Optional;

public class DamageReport extends ObjectExtent {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Vehicle vehicle;
    private final Reservation reservation;
    private String description;
    private double estimatedCost;
    private final LocalDate reportDate;

    public DamageReport(Vehicle vehicle, Reservation reservation, String description, double estimatedCost) {
        try {
            if (vehicle == null) {
                throw new ValidationException("Zgłoszenie uszkodzenia musi dotyczyć pojazdu.");
            }

            setDescription(description);
            setEstimatedCost(estimatedCost);

            this.vehicle = vehicle;
            this.reservation = reservation;
            this.reportDate = LocalDate.now();

            vehicle.addDamageReport(this);
            vehicle.setStatus(VehicleStatus.DAMAGED);

            if (reservation != null) {
                reservation.addDamageReport(this);
            }
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Optional<Reservation> getReservation() {
        return Optional.ofNullable(reservation);
    }

    public String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Opis uszkodzenia jest wymagany.");
        }

        this.description = description.trim();
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public final void setEstimatedCost(double estimatedCost) {
        if (estimatedCost < 0) {
            throw new ValidationException("Szacowany koszt naprawy nie może być ujemny.");
        }

        this.estimatedCost = estimatedCost;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s — %s, koszt: %.2f zł", reportDate, vehicle.getLicensePlate(), description, estimatedCost);
    }
}
