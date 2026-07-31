package com.wardiusz.vehiclerental.model;

import com.wardiusz.vehiclerental.model.enums.ReservationStatus;
import com.wardiusz.vehiclerental.model.enums.VehicleStatus;

import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public abstract class Vehicle extends ObjectExtent {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String licensePlate;
    private String brand;
    private String model;
    private int productionYear;
    private double dailyRate;
    private int mileage;
    private String color;
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    private Branch branch;
    private final List<ReservationDetails> reservationDetails = new ArrayList<>();
    private final List<DamageReport> damageReports = new ArrayList<>();

    protected Vehicle(String licensePlate, String brand, String model, int productionYear, double dailyRate, int mileage, String color) {
        try {
            this.licensePlate = validatePlate(licensePlate);
            setBrand(brand);
            setModel(model);
            setProductionYear(productionYear);
            setDailyRate(dailyRate);
            setMileage(mileage);
            setColor(color);
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    private static String validatePlate(String plate) {
        if (plate == null || plate.trim().length() < 4) {
            throw new ValidationException("Numer rejestracyjny musi mieć co najmniej 4 znaki.");
        }

        String v = plate.trim().toUpperCase().replaceAll("\\s+", " ");

        if (findByPlate(v) != null) {
            throw new ValidationException("Pojazd o numerze " + v + " już istnieje.");
        }

        return v;
    }

    public static Vehicle findByPlate(String plate) {
        if (plate == null) {
            return null;
        }

        String v = plate.trim().toUpperCase().replaceAll("\\s+", " ");

        for (Vehicle vehicle : getExtentWithSubclasses(Vehicle.class)) {
            if (v.equals(vehicle.licensePlate)) {
                return vehicle;
            }
        }

        return null;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public final void setBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            throw new ValidationException("Marka jest wymagana.");
        }

        this.brand = brand.trim();
    }

    public String getModel() {
        return model;
    }

    public final void setModel(String model) {
        if (model == null || model.trim().isEmpty()) {
            throw new ValidationException("Model jest wymagany.");
        }

        this.model = model.trim();
    }

    public int getProductionYear() {
        return productionYear;
    }

    public final void setProductionYear(int productionYear) {
        int current = LocalDate.now().getYear();

        if (productionYear < 0 || productionYear > current + 1) {
            throw new ValidationException("Rok produkcji nie może być negatywny bądź większy niż obecny" + (current + 1) + ".");
        }

        this.productionYear = productionYear;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public final void setDailyRate(double dailyRate) {
        if (dailyRate <= 0) {
            throw new ValidationException("Stawka dobowa musi być większa od zera.");
        }

        this.dailyRate = dailyRate;
    }

    public int getMileage() {
        return mileage;
    }

    public final void setMileage(int mileage) {
        if (mileage < 0) {
            throw new ValidationException("Przebieg nie może być ujemny.");
        }

        this.mileage = mileage;
    }

    public String getColor() {
        return color;
    }

    public final void setColor(String color) {
        if (color == null || color.trim().isEmpty()) {
            throw new ValidationException("Kolor jest wymagany.");
        }

        this.color = color.trim();
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        if (status == null) {
            throw new ValidationException("Status pojazdu jest wymagany.");
        }

        this.status = status;
    }

    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    public Branch getBranch() {
        return branch;
    }

    void setBranch(Branch branch) {
        this.branch = branch;
    }

    void addReservationDetails(ReservationDetails details) {
        reservationDetails.add(details);
    }

    void removeReservationDetails(ReservationDetails details) {
        reservationDetails.remove(details);
    }

    public List<ReservationDetails> getReservationDetails() {
        return Collections.unmodifiableList(reservationDetails);
    }

    void addDamageReport(DamageReport report) {
        damageReports.add(report);
    }

    public List<DamageReport> getDamageReports() {
        return Collections.unmodifiableList(damageReports);
    }

    public boolean hasActiveReservation() {
        EnumSet<ReservationStatus> blocking = EnumSet.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED, ReservationStatus.ACTIVE);

        for (ReservationDetails d : reservationDetails) {
            if (blocking.contains(d.getReservation().getStatus())) {
                return true;
            }
        }

        return false;
    }

    public abstract double calculateCost(int days);

    public abstract String getTypeName();

    public abstract String getSpecificInfo();

    public String getShortDescription() {
        return licensePlate + " — " + brand + " " + model + " (" + productionYear + ")";
    }

    @Override
    public String toString() {
        return getTypeName() + ": " + getShortDescription();
    }
}
