package com.wardiusz.carrental.model;

import com.wardiusz.carrental.model.enums.PaymentMethod;
import com.wardiusz.carrental.model.enums.PaymentStatus;
import com.wardiusz.carrental.model.enums.ReservationStatus;
import com.wardiusz.carrental.model.enums.VehicleStatus;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.*;

public class Reservation extends ObjectExtent {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final LocalDateTime creationDate;
    private ReservationStatus status = ReservationStatus.PENDING;

    private final Customer customer;
    private Employee handledBy;
    private final Branch pickupBranch;

    private final List<ReservationDetails> items = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private final List<DamageReport> damageReports = new ArrayList<>();

    public Reservation(Customer customer, Branch pickupBranch) {
        try {
            if (customer == null) {
                throw new ValidationException("Rezerwacja musi być powiązana z klientem.");
            }

            if (pickupBranch == null) {
                throw new ValidationException("Rezerwacja musi mieć oddział odbioru.");
            }

            this.id = UUID.randomUUID().toString();
            this.customer = customer;
            this.pickupBranch = pickupBranch;
            this.creationDate = LocalDateTime.now();

            customer.addReservation(this);
            pickupBranch.addReservation(this);
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Optional<Employee> getHandledBy() {
        return Optional.ofNullable(handledBy);
    }

    public Branch getPickupBranch() {
        return pickupBranch;
    }


    public void setHandledBy(Employee employee) {
        if (employee == null) {
            throw new ValidationException("Pracownik obsługujący jest wymagany.");
        }

        this.handledBy = employee;

        employee.addHandledReservation(this);
    }

    public ReservationDetails addItem(Vehicle vehicle, LocalDateTime pickupDate, LocalDateTime returnDate) {
        if (status != ReservationStatus.PENDING) {
            throw new ValidationException("Pozycje można dodawać tylko do rezerwacji oczekującej.");
        }

        for (ReservationDetails d : items) {
            if (d.getVehicle() == vehicle) {
                throw new ValidationException("Ten pojazd jest już w rezerwacji (unikalność pary rezerwacja-pojazd).");
            }
        }

        ReservationDetails details = new ReservationDetails(this, vehicle, pickupDate, returnDate);

        items.add(details);

        return details;
    }

    public void removeItem(ReservationDetails details) {
        if (status != ReservationStatus.PENDING) {
            throw new ValidationException("Pozycje można usuwać tylko z rezerwacji oczekującej.");
        }

        if (items.remove(details)) {
            details.unlink();
            removeFromExtent(details);
        }
    }

    public List<ReservationDetails> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getTotalPrice() {
        double sum = 0;
        for (ReservationDetails d : items) {
            sum += d.calculatePrice();
        }
        return sum;
    }

    public double getPaidAmount() {
        double sum = 0;
        for (Payment p : payments) {
            if (p.getStatus() == PaymentStatus.PAID) {
                sum += p.getAmount();
            }
        }
        return sum;
    }

    public double getDamagesCost() {
        double sum = 0;
        for (DamageReport d : damageReports) {
            sum += d.getEstimatedCost();
        }
        return sum;
    }

    public double getAmountDue() {
        return getTotalPrice() + getDamagesCost() - getPaidAmount();
    }

    public boolean isFullyPaid() {
        return !items.isEmpty() && getPaidAmount() >= getTotalPrice() + getDamagesCost() - 0.005;
    }

    public Payment addPayment(double amount, PaymentMethod method, PaymentStatus paymentStatus) {
        Payment payment = new Payment(this, amount, method, paymentStatus);

        payments.add(payment);

        return payment;
    }

    public List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }

    void addDamageReport(DamageReport report) {
        damageReports.add(report);
    }

    public List<DamageReport> getDamageReports() {
        return Collections.unmodifiableList(damageReports);
    }

    public boolean canChangeStatusTo(ReservationStatus target) {
        if (target == null || !status.canTransitionTo(target)) {
            return false;
        }

        if (target == ReservationStatus.CONFIRMED) {
            return isFullyPaid();
        }

        return true;
    }

    public void changeStatus(ReservationStatus target) {
        if (target == null) {
            throw new ValidationException("Status docelowy jest wymagany.");
        }

        if (!status.canTransitionTo(target)) {
            throw new ValidationException("Niedozwolone przejście statusu: " + status + " → " + target + ".");
        }

        if (target == ReservationStatus.CONFIRMED && !isFullyPaid()) {
            throw new ValidationException("Potwierdzenie rezerwacji wymaga zarejestrowania opłaconej płatności na pełną kwotę.");
        }

        if (target == ReservationStatus.CONFIRMED && items.isEmpty()) {
            throw new ValidationException("Rezerwacja musi obejmować co najmniej jeden pojazd.");
        }

        switch (target) {
            case CONFIRMED -> setVehiclesStatus(VehicleStatus.RESERVED);
            case ACTIVE -> setVehiclesStatus(VehicleStatus.RENTED);
            case COMPLETED -> releaseVehicles();
            case CANCELLED -> {
                releaseVehicles();
                refundPayments();
            }
        }
        this.status = target;
    }

    public void confirm() {
        changeStatus(ReservationStatus.CONFIRMED);
    }

    public void activate() {
        changeStatus(ReservationStatus.ACTIVE);
    }

    public void complete() {
        changeStatus(ReservationStatus.COMPLETED);
    }

    public void cancel() {
        changeStatus(ReservationStatus.CANCELLED);
    }

    private void setVehiclesStatus(VehicleStatus vehicleStatus) {
        for (ReservationDetails d : items) {
            if (d.getVehicle().getStatus() != VehicleStatus.DAMAGED) {
                d.getVehicle().setStatus(vehicleStatus);
            }
        }
    }

    private void releaseVehicles() {
        for (ReservationDetails d : items) {
            VehicleStatus s = d.getVehicle().getStatus();
            if (s == VehicleStatus.RESERVED || s == VehicleStatus.RENTED) {
                d.getVehicle().setStatus(VehicleStatus.AVAILABLE);
            }
        }
    }

    private void refundPayments() {
        for (Payment p : payments) {
            if (p.getStatus() == PaymentStatus.PAID) {
                p.refund();
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Rezerwacja %s — %s, %s (%.2f zł)", id, customer.getFullName(), status, getTotalPrice());
    }
}