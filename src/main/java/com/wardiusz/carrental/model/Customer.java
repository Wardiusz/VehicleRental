package com.wardiusz.carrental.model;

import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer extends User {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final int MIN_AGE = 18;

    private String drivingLicenseNumber;
    private final List<Reservation> reservations = new ArrayList<>();

    public Customer(String login, String password, String firstName, String lastName, LocalDate birthDate,
                    String phoneNumber, String email, Address address, String drivingLicenseNumber) {
        super(login, password, firstName, lastName, birthDate, phoneNumber, email, address);

        try {
            if (getAge() < MIN_AGE) {
                throw new ValidationException("Klient musi mieć co najmniej " + MIN_AGE + " lat.");
            }
            setDrivingLicenseNumber(drivingLicenseNumber);
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    public static Customer register(String login, String password, String firstName, String lastName, LocalDate birthDate,
                                    String phoneNumber, String email, Address address, String drivingLicenseNumber) {
        return new Customer(login, password, firstName, lastName, birthDate, phoneNumber, email, address, drivingLicenseNumber);
    }

    public Reservation makeReservation(Branch pickupBranch) {
        return new Reservation(this, pickupBranch);
    }

    public void cancelReservation(Reservation reservation) {
        if (reservation == null || reservation.getCustomer() != this) {
            throw new ValidationException("Można anulować wyłącznie własną rezerwację.");
        }

        reservation.cancel();
    }

    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public final void setDrivingLicenseNumber(String drivingLicenseNumber) {
        if (drivingLicenseNumber == null || drivingLicenseNumber.trim().isEmpty()) {
            throw new ValidationException("Numer prawa jazdy jest wymagany.");
        }

        this.drivingLicenseNumber = drivingLicenseNumber.trim();
    }

    void addReservation(Reservation reservation) {
        if (!reservations.contains(reservation)) {
            reservations.add(reservation);
        }
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    @Override
    public String getRoleName() {
        return "Klient";
    }
}
