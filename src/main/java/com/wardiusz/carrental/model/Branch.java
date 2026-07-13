package com.wardiusz.carrental.model;

import java.io.Serial;
import java.util.*;

public class Branch extends ObjectExtent implements Addressable {
    @Serial
    private static final long serialVersionUID = 1L;

    public String id;
    private String name;
    private String phoneNumber;
    private Address address;

    private final Map<String, Vehicle> fleet = new LinkedHashMap<>();
    private final Map<String, Employee> staff = new LinkedHashMap<>();
    private final List<Reservation> reservations = new ArrayList<>();

    public Branch(String name, String phoneNumber, Address address) {
        try {
            this.id = UUID.randomUUID().toString();
            setName(name);
            setPhoneNumber(phoneNumber);
            setAddress(address);
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nazwa oddziału jest wymagana.");
        }

        this.name = name.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public final void setPhoneNumber(String phoneNumber) {
        String v = phoneNumber == null ? "" : phoneNumber.replaceAll("[\\s-]", "");

        if (!v.matches("\\+?\\d{9,15}")) {
            throw new ValidationException("Numer telefonu oddziału musi zawierać 9-15 cyfr.");
        }

        this.phoneNumber = v;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public final void setAddress(Address address) {
        if (address == null) {
            throw new ValidationException("Adres oddziału jest wymagany.");
        }

        this.address = address;
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new ValidationException("Pojazd jest wymagany.");
        }

        String key = vehicle.getLicensePlate();

        if (fleet.containsKey(key)) {
            throw new ValidationException("Pojazd " + key + " jest już we flocie oddziału " + name + ".");
        }

        if (vehicle.getBranch() != null && vehicle.getBranch() != this) {
            vehicle.getBranch().fleet.remove(key);
        }

        fleet.put(key, vehicle);
        vehicle.setBranch(this);
    }

    public Vehicle findVehicle(String licensePlate) {
        if (licensePlate == null) {
            return null;
        }

        return fleet.get(licensePlate.trim().toUpperCase().replaceAll("\\s+", " "));
    }

    public void removeVehicle(Vehicle vehicle) {
        if (vehicle == null || fleet.get(vehicle.getLicensePlate()) != vehicle) {
            throw new ValidationException("Pojazd nie należy do tego oddziału.");
        }

        if (vehicle.hasActiveReservation()) {
            throw new ValidationException("Nie można usunąć pojazdu powiązanego z aktywną rezerwacją.");
        }

        fleet.remove(vehicle.getLicensePlate());

        vehicle.setBranch(null);
        removeFromExtent(vehicle);
    }

    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : fleet.values()) {
            if (v.isAvailable()) {
                result.add(v);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public List<Vehicle> getFleet() {
        return Collections.unmodifiableList(new ArrayList<>(fleet.values()));
    }

    public void employ(Employee employee) {
        if (employee == null) {
            throw new ValidationException("Pracownik jest wymagany.");
        }

        String key = employee.getEmployeeNumber();
        Employee existing = staff.get(key);

        if (existing != null && existing != employee) {
            throw new ValidationException("W oddziale " + name + " istnieje już pracownik o numerze " + key + ".");
        }

        if (employee.getBranch() != null && employee.getBranch() != this) {
            employee.getBranch().staff.remove(key);
        }

        staff.put(key, employee);
        employee.setBranch(this);
    }

    public Employee findEmployee(String employeeNumber) {
        if (employeeNumber == null) {
            return null;
        }
        return staff.get(employeeNumber.trim().toUpperCase());
    }

    public void dismiss(Employee employee) {
        if (employee == null || staff.get(employee.getEmployeeNumber()) != employee) {
            throw new ValidationException("Pracownik nie jest zatrudniony w tym oddziale.");
        }

        staff.remove(employee.getEmployeeNumber());
        employee.setBranch(null);
    }

    void changeEmployeeNumber(Employee employee, String newNumber) {
        Employee existing = staff.get(newNumber);

        if (existing != null && existing != employee) {
            throw new ValidationException("W oddziale " + name + " istnieje już pracownik o numerze " + newNumber + ".");
        }

        staff.remove(employee.getEmployeeNumber());
        staff.put(newNumber, employee);
    }

    public List<Employee> getStaff() {
        return Collections.unmodifiableList(new ArrayList<>(staff.values()));
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
    public String toString() {
        return name + " (" + address.getCity() + ")";
    }
}
