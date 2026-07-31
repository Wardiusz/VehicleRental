package com.wardiusz.vehiclerental.model;

import com.wardiusz.vehiclerental.model.enums.MotorcycleType;

import java.io.Serial;

public class Motorcycle extends Vehicle {
    @Serial
    private static final long serialVersionUID = 1L;

    private MotorcycleType type;
    private int engineCapacity; // cm3
    private boolean hasABS;

    public Motorcycle(String licensePlate, String brand, String model, int productionYear, double dailyRate,
                      int mileage, String color, MotorcycleType type, int engineCapacity, boolean hasABS) {
        super(licensePlate, brand, model, productionYear, dailyRate, mileage, color);

        try {
            setType(type);
            setEngineCapacity(engineCapacity);
            this.hasABS = hasABS;
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    public MotorcycleType getType() {
        return type;
    }

    public final void setType(MotorcycleType type) {
        if (type == null) {
            throw new ValidationException("Typ motocykla jest wymagany.");
        }

        this.type = type;
    }

    public int getEngineCapacity() {
        return engineCapacity;
    }

    public final void setEngineCapacity(int engineCapacity) {
        if (engineCapacity < 50) {
            throw new ValidationException("Pojemność silnika niższa niż 50.");
        }
        this.engineCapacity = engineCapacity;
    }

    public boolean isHasABS() {
        return hasABS;
    }

    public void setHasABS(boolean hasABS) {
        this.hasABS = hasABS;
    }

    @Override
    public double calculateCost(int days) {
        double cost = getDailyRate() * days;

        if (engineCapacity > 600) {
            cost *= 1.20;
        }

        return cost;
    }

    @Override
    public String getTypeName() {
        return "Motocykl";
    }

    @Override
    public String getSpecificInfo() {
        return "Typ:\t\t\t" + type + "\n" +
                "Pojemność:\t" + engineCapacity + " cm3" + "\n" +
                "ABS:\t\t\t" + (hasABS ? "tak" : "nie");
    }
}
