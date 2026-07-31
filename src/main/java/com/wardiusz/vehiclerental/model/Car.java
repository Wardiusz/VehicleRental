package com.wardiusz.vehiclerental.model;

import com.wardiusz.vehiclerental.model.enums.CarType;
import com.wardiusz.vehiclerental.model.enums.FuelType;
import com.wardiusz.vehiclerental.model.enums.TransmissionType;

import java.io.Serial;

public class Car extends Vehicle {
    @Serial
    private static final long serialVersionUID = 1L;

    private CarType bodyType;
    private int numberOfSeats;
    private int numberOfDoors;
    private FuelType fuelType;
    private TransmissionType transmission;
    private int trunkCapacity;

    public Car(String licensePlate, String brand, String model, int productionYear, double dailyRate, int mileage, String color,
               CarType bodyType, int numberOfSeats, int numberOfDoors, FuelType fuelType, TransmissionType transmission, int trunkCapacity) {
        super(licensePlate, brand, model, productionYear, dailyRate, mileage, color);

        try {
            setBodyType(bodyType);
            setNumberOfSeats(numberOfSeats);
            setNumberOfDoors(numberOfDoors);
            setFuelType(fuelType);
            setTransmission(transmission);
            setTrunkCapacity(trunkCapacity);
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    public CarType getBodyType() {
        return bodyType;
    }

    public final void setBodyType(CarType bodyType) {
        if (bodyType == null) {
            throw new ValidationException("Typ nadwozia jest wymagany.");
        }

        this.bodyType = bodyType;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public final void setNumberOfSeats(int numberOfSeats) {
        if (numberOfSeats < 0) {
            throw new ValidationException("Liczba miejsc nie może być ujemna.");
        }

        this.numberOfSeats = numberOfSeats;
    }

    public int getNumberOfDoors() {
        return numberOfDoors;
    }

    public final void setNumberOfDoors(int numberOfDoors) {
        if (numberOfDoors < 0) {
            throw new ValidationException("Liczba drzwi nie może być ujemna.");
        }

        this.numberOfDoors = numberOfDoors;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public final void setFuelType(FuelType fuelType) {
        if (fuelType == null) {
            throw new ValidationException("Rodzaj paliwa jest wymagany.");
        }

        this.fuelType = fuelType;
    }

    public TransmissionType getTransmission() {
        return transmission;
    }

    public final void setTransmission(TransmissionType transmission) {
        if (transmission == null) {
            throw new ValidationException("Rodzaj skrzyni biegów jest wymagany.");
        }

        this.transmission = transmission;
    }

    public int getTrunkCapacity() {
        return trunkCapacity;
    }

    public final void setTrunkCapacity(int trunkCapacity) {
        if (trunkCapacity < 0) {
            throw new ValidationException("Pojemność bagażnika nie może być ujemna.");
        }

        this.trunkCapacity = trunkCapacity;
    }

    @Override
    public double calculateCost(int days) {
        double cost = getDailyRate() * days;

        if (transmission == TransmissionType.AUTOMATIC) {
            cost += 10.0 * days;
        }

        if (bodyType == CarType.SUV) {
            cost *= 1.15;
        }

        return cost;
    }

    @Override
    public String getTypeName() {
        return "Samochód";
    }

    @Override
    public String getSpecificInfo() {
        return "Nadwozie:\t" + bodyType + "\n" +
                "Miejsca:\t\t" + numberOfSeats + "\n" +
                "Drzwi:\t\t" + numberOfDoors + "\n" +
                "Paliwo:\t\t" + fuelType + "\n" +
                "Skrzynia:\t\t" + transmission + "\n" +
                "Bagażnik:\t\t" + trunkCapacity + " l";
    }
}
