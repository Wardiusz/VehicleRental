package com.wardiusz.vehiclerental.model;

import java.io.Serial;

public class Truck extends Vehicle {
    @Serial
    private static final long serialVersionUID = 1L;

    private double maxLoadCapacity;
    private double cargoVolume;
    private boolean hasLiftgate;

    public Truck(String licensePlate, String brand, String model, int productionYear, double dailyRate,
                 int mileage, String color, double maxLoadCapacity, double cargoVolume, boolean hasLiftgate) {
        super(licensePlate, brand, model, productionYear, dailyRate, mileage, color);
        try {
            setMaxLoadCapacity(maxLoadCapacity);
            setCargoVolume(cargoVolume);
            this.hasLiftgate = hasLiftgate;
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    public double getMaxLoadCapacity() {
        return maxLoadCapacity;
    }

    public final void setMaxLoadCapacity(double maxLoadCapacity) {
        if (maxLoadCapacity <= 0) {
            throw new ValidationException("Ładowność musi być większa od zera.");
        }

        this.maxLoadCapacity = maxLoadCapacity;
    }

    public double getCargoVolume() {
        return cargoVolume;
    }

    public final void setCargoVolume(double cargoVolume) {
        if (cargoVolume <= 0) {
            throw new ValidationException("Objętość przestrzeni ładunkowej musi być większa od zera.");
        }

        this.cargoVolume = cargoVolume;
    }

    public boolean isHasLiftgate() {
        return hasLiftgate;
    }

    public void setHasLiftgate(boolean hasLiftgate) {
        this.hasLiftgate = hasLiftgate;
    }

    @Override
    public double calculateCost(int days) {
        double cost = getDailyRate() * days + maxLoadCapacity * 20.0 * days;

        if (hasLiftgate) {
            cost += 30.0 * days;
        }

        return cost;
    }

    @Override
    public String getTypeName() {
        return "Ciężarówka";
    }

    @Override
    public String getSpecificInfo() {
        return "Ładowność:\t" + maxLoadCapacity + " t" + "\n" +
                "Przestrzeń:\t" + cargoVolume + " m3" + "\n" +
                "Winda:\t\t" + (hasLiftgate ? "tak" : "nie");
    }
}
