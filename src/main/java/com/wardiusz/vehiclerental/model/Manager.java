package com.wardiusz.vehiclerental.model;

import com.wardiusz.vehiclerental.model.enums.VehicleStatus;

import java.io.Serial;
import java.time.LocalDate;

public class Manager extends Employee {
    @Serial
    private static final long serialVersionUID = 1L;

    public Manager(String login, String password, String firstName, String lastName, LocalDate birthDate,
                   String phoneNumber, String email, Address address, String employeeNumber) {
        super(login, password, firstName, lastName, birthDate, phoneNumber, email, address, employeeNumber);
    }

    public void addVehicle(Branch branch, Vehicle vehicle) {
        branch.addVehicle(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        if (vehicle.getBranch() == null) {
            if (vehicle.hasActiveReservation()) {
                throw new ValidationException("Nie można usunąć pojazdu powiązanego z aktywną rezerwacją.");
            }
            removeFromExtent(vehicle);
        } else {
            vehicle.getBranch().removeVehicle(vehicle);
        }
    }

    public void modifyVehicle(Vehicle vehicle, double dailyRate, int mileage, String color, VehicleStatus status) {
        vehicle.setDailyRate(dailyRate);
        vehicle.setMileage(mileage);
        vehicle.setColor(color);
        vehicle.setStatus(status);
    }

    public void addEmployee(Branch branch, Employee employee) {
        branch.employ(employee);
    }

    public void removeEmployee(Employee employee) {
        if (employee == this) {
            throw new ValidationException("Nie można zwolnić samego siebie.");
        }
        if (employee.getBranch() != null) {
            employee.getBranch().dismiss(employee);
        }
        removeFromExtent(employee);
    }

    public void modifyEmployee(Employee employee, String phoneNumber, String email, Address address) {
        employee.setPhoneNumber(phoneNumber);
        employee.setEmail(email);
        employee.setAddress(address);
    }

    @Override
    public String getRoleName() {
        return "Menadżer";
    }
}
