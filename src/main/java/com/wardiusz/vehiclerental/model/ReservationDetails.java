package com.wardiusz.vehiclerental.model;

import com.wardiusz.vehiclerental.model.enums.ReservationStatus;

import java.io.Serial;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReservationDetails extends ObjectExtent {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Reservation reservation;
    private final Vehicle vehicle;
    private LocalDateTime pickupDate;
    private LocalDateTime returnDate;
    private final List<AdditionalService> services = new ArrayList<>();

    ReservationDetails(Reservation reservation, Vehicle vehicle, LocalDateTime pickupDate, LocalDateTime returnDate) {
        try {
            if (reservation == null) {
                throw new ValidationException("Pozycja musi należeć do rezerwacji.");
            }

            if (vehicle == null) {
                throw new ValidationException("Pozycja musi dotyczyć pojazdu.");
            }

            if (!vehicle.isAvailable()) {
                throw new ValidationException("Tylko pojazd o statusie DOSTĘPNY może zostać zarezerwowany (" + vehicle.getLicensePlate() + " ma status: " + vehicle.getStatus() + ").");
            }

            validateDates(pickupDate, returnDate);

            this.reservation = reservation;
            this.vehicle = vehicle;
            this.pickupDate = pickupDate;
            this.returnDate = returnDate;

            vehicle.addReservationDetails(this);
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    private static void validateDates(LocalDateTime pickupDate, LocalDateTime returnDate) {
        if (pickupDate == null || returnDate == null) {
            throw new ValidationException("Data odbioru i data zwrotu są wymagane.");
        }

        if (pickupDate.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Data odbioru nie może być w przeszłości.");
        }

        if (returnDate.isBefore(pickupDate)) {
            throw new ValidationException("Data zwrotu nie może być wcześniejsza niż data odbioru.");
        }
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDateTime getPickupDate() {
        return pickupDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void changeDates(LocalDateTime pickupDate, LocalDateTime returnDate) {
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ValidationException("Daty można zmieniać tylko w rezerwacji oczekującej.");
        }

        validateDates(pickupDate, returnDate);

        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
    }

    public int getDurationDays() {
        return (int) Math.max(1, ChronoUnit.DAYS.between(pickupDate, returnDate));
    }

    public void addService(AdditionalService service) {
        if (service == null) {
            throw new ValidationException("Usługa jest wymagana.");
        }

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ValidationException("Usługi można dodawać tylko do rezerwacji oczekującej.");
        }

        if (services.contains(service)) {
            throw new ValidationException("Ta usługa jest już dodana do pozycji.");
        }

        services.add(service);
    }

    public void removeService(AdditionalService service) {
        services.remove(service);
    }

    public List<AdditionalService> getServices() {
        return Collections.unmodifiableList(services);
    }

    public double calculatePrice() {
        double sum = vehicle.calculateCost(getDurationDays());

        for (AdditionalService s : services) {
            sum += s.getPrice();
        }

        return sum;
    }

    void unlink() {
        vehicle.removeReservationDetails(this);
    }

    @Override
    public String toString() {
        return vehicle.getShortDescription() + ", " + pickupDate + " → " + returnDate + " (" + getDurationDays() + " dób)";
    }
}
