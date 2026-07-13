package com.wardiusz.carrental.model;

import com.wardiusz.carrental.model.enums.PaymentMethod;
import com.wardiusz.carrental.model.enums.PaymentStatus;
import com.wardiusz.carrental.model.enums.ReservationStatus;

import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Employee extends User {
    @Serial
    private static final long serialVersionUID = 1L;

    private String employeeNumber;
    private Branch branch;
    private final List<Reservation> handledReservations = new ArrayList<>();

    public Employee(String login, String password, String firstName, String lastName, LocalDate birthDate,
                    String phoneNumber, String email, Address address, String employeeNumber) {
        super(login, password, firstName, lastName, birthDate, phoneNumber, email, address);

        try {
            setEmployeeNumber(employeeNumber);
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public final void setEmployeeNumber(String employeeNumber) {
        if (employeeNumber == null || employeeNumber.trim().isEmpty()) {
            throw new ValidationException("Numer pracownika jest wymagany.");
        }

        String v = employeeNumber.trim().toUpperCase();

        if (branch != null && !v.equals(this.employeeNumber)) {
            branch.changeEmployeeNumber(this, v);
        }

        this.employeeNumber = v;
    }

    public Branch getBranch() {
        return branch;
    }

    void setBranch(Branch branch) {
        this.branch = branch;
    }

    void addHandledReservation(Reservation reservation) {
        if (!handledReservations.contains(reservation)) {
            handledReservations.add(reservation);
        }
    }

    public List<Reservation> getHandledReservations() {
        return Collections.unmodifiableList(handledReservations);
    }


    public DamageReport registerDamage(Vehicle vehicle, Reservation reservation, String description, double estimatedCost) {
        return new DamageReport(vehicle, reservation, description, estimatedCost);
    }

    public void changeReservationStatus(Reservation reservation, ReservationStatus target) {
        reservation.changeStatus(target);

        if (reservation.getHandledBy().isEmpty()) {
            reservation.setHandledBy(this);
        }
    }

    public Payment registerPayment(Reservation reservation, PaymentMethod method) {
        if (reservation.getAmountDue() <= 0.005) {
            throw new ValidationException("Rezerwacja jest już w pełni opłacona.");
        }

        Payment paid = null;

        for (Payment p : reservation.getPayments()) {
            if (p.getStatus() == PaymentStatus.PENDING) {
                p.markPaid();
                paid = p;
                break;
            }
        }

        double stillDue = reservation.getAmountDue();
        if (stillDue > 0.005) {
            paid = reservation.addPayment(stillDue, method, PaymentStatus.PAID);
        }

        if (reservation.getStatus() == ReservationStatus.PENDING && reservation.isFullyPaid()) {
            reservation.confirm();
        }

        return paid;
    }

    @Override
    public String getRoleName() {
        return "Pracownik";
    }
}
