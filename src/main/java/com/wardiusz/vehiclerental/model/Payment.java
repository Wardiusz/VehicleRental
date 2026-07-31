package com.wardiusz.vehiclerental.model;

import com.wardiusz.vehiclerental.model.enums.PaymentMethod;
import com.wardiusz.vehiclerental.model.enums.PaymentStatus;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;

public class Payment extends ObjectExtent {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final Reservation reservation;
    private final double amount;
    private final LocalDateTime date;
    private final PaymentMethod method;
    private PaymentStatus status;

    Payment(Reservation reservation, double amount, PaymentMethod method, PaymentStatus status) {
        try {
            if (reservation == null) {
                throw new ValidationException("Płatność musi być powiązana z rezerwacją.");
            }

            if (amount <= 0) {
                throw new ValidationException("Kwota płatności musi być większa od zera.");
            }

            if (method == null || status == null) {
                throw new ValidationException("Metoda i status płatności są wymagane.");
            }

            this.id = UUID.randomUUID().toString();
            this.reservation = reservation;
            this.amount = amount;
            this.method = method;
            this.status = status;
            this.date = LocalDateTime.now();
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    public Reservation getReservation() {
        return reservation;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void markPaid() {
        if (status != PaymentStatus.PENDING) {
            throw new ValidationException("Tylko oczekującą płatność można oznaczyć jako opłaconą.");
        }

        status = PaymentStatus.PAID;
    }

    public void markFailed() {
        if (status != PaymentStatus.PENDING) {
            throw new ValidationException("Tylko oczekującą płatność można oznaczyć jako nieudaną.");
        }

        status = PaymentStatus.FAILED;
    }

    public void refund() {
        if (status != PaymentStatus.PAID) {
            throw new ValidationException("Zwrócić można tylko opłaconą płatność.");
        }

        status = PaymentStatus.REFUNDED;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("%.2f zł — %s (%s, %s)", amount, method, status, date);
    }
}
