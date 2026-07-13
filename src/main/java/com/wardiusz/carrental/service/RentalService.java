package com.wardiusz.carrental.service;

import com.wardiusz.carrental.model.*;
import com.wardiusz.carrental.model.enums.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentalService {

    public static final File DATA_FILE = new File("data" + File.separator + "extents.dat");

    private User currentUser;

    public void save() {
        try {
            ObjectExtent.saveExtents(DATA_FILE);
        } catch (Exception e) {
            throw new ValidationException("Nie udało się zapisać danych: " + e.getMessage());
        }
    }

    public void load() {
        if (DATA_FILE.exists()) {
            try {
                ObjectExtent.loadExtents(DATA_FILE);
                return;
            } catch (Exception e) {
                System.err.println("Nie udało się wczytać danych (" + e.getMessage() + ") — tworzę dane przykładowe.");
            }
        }

        ObjectExtent.clearExtents();

        DataSeeder.seed();

        save();
    }

    public User login(String login, String password) {
        User user = User.findByLogin(login);

        if (user == null || !user.login(password)) {
            throw new ValidationException("Nieprawidłowy login lub hasło.");
        }

        currentUser = user;

        return user;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void registerCustomer(String login, String password, String firstName, String lastName, LocalDate birthDate, String phone, String email, Address address, String drivingLicenseNumber) {
        Customer.register(login, password, firstName, lastName, birthDate, phone, email, address, drivingLicenseNumber);

        save();
    }

// LISTY
    public List<Branch> getBranches() {
        return ObjectExtent.getExtent(Branch.class);
    }

    public List<Vehicle> getAllVehicles() {
        return ObjectExtent.getExtentWithSubclasses(Vehicle.class);
    }

    public List<Vehicle> getAvailableVehicles(Branch branch) {
        if (branch != null) {
            return branch.getAvailableVehicles();
        }
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : getAllVehicles()) {
            if (v.isAvailable()) {
                result.add(v);
            }
        }
        return result;
    }

    public List<AdditionalService> getServicesCatalog() {
        return ObjectExtent.getExtent(AdditionalService.class);
    }

    public List<Employee> getAllEmployees() {
        return ObjectExtent.getExtentWithSubclasses(Employee.class);
    }

    public List<Customer> getAllCustomers() {
        return ObjectExtent.getExtent(Customer.class);
    }

    public List<Reservation> getAllReservations() {
        return ObjectExtent.getExtent(Reservation.class);
    }

// REZERWACJE (CLIENT)

    public record CartItem(Vehicle vehicle, LocalDateTime pickupDate, LocalDateTime plannedReturnDate, List<AdditionalService> services) {
        public CartItem(Vehicle vehicle, LocalDateTime pickupDate, LocalDateTime plannedReturnDate, List<AdditionalService> services) {
            this.vehicle = vehicle;
            this.pickupDate = pickupDate;
            this.plannedReturnDate = plannedReturnDate;
            this.services = new ArrayList<>(services);
        }
    }

    public Reservation placeReservation(Customer customer, Branch branch, List<CartItem> cart, PaymentMethod method, boolean payNow) {
        if (customer == null) {
            throw new ValidationException("Tylko zalogowany CLIENT może składać rezerwacje.");
        }

        if (cart == null || cart.isEmpty()) {
            throw new ValidationException("Rezerwacja musi obejmować co najmniej jeden pojazd.");
        }

        Reservation reservation = customer.makeReservation(branch);

        try {
            for (CartItem item : cart) {
                ReservationDetails details = reservation.addItem(item.vehicle, item.pickupDate, item.plannedReturnDate);

                for (AdditionalService s : item.services) {
                    details.addService(s);
                }
            }
        } catch (ValidationException e) {
            for (ReservationDetails d : new ArrayList<>(reservation.getItems())) {
                reservation.removeItem(d);
            }

            ObjectExtent.removeFromExtent(reservation);
            throw e;
        }

        double total = reservation.getTotalPrice();

        if (payNow) {
            reservation.addPayment(total, method, PaymentStatus.PAID);
            reservation.confirm();
        } else {
            reservation.addPayment(total, method, PaymentStatus.PENDING);
        }

        save();

        return reservation;
    }

    public void cancelReservation(Customer customer, Reservation reservation) {
        customer.cancelReservation(reservation);

        save();
    }

// REZERWACJE (EMPLOYEE)

    private Employee requireEmployee() {
        if (!(currentUser instanceof Employee)) {
            throw new ValidationException("Ta operacja wymaga uprawnień EMPLOYEEa.");
        }

        return (Employee) currentUser;
    }

    private Manager requireManager() {
        if (!(currentUser instanceof Manager)) {
            throw new ValidationException("Ta operacja wymaga uprawnień MANAGERa.");
        }

        return (Manager) currentUser;
    }

    public void changeReservationStatus(Reservation reservation, ReservationStatus target) {
        Employee employee = requireEmployee();

        employee.changeReservationStatus(reservation, target);

        save();
    }

    public void registerPaidPayment(Reservation reservation, PaymentMethod method) {
        Employee employee = requireEmployee();

        employee.registerPayment(reservation, method);

        save();
    }

    public DamageReport registerDamage(Reservation reservation, Vehicle vehicle, String description, double estimatedCost) {
        Employee employee = requireEmployee();
        DamageReport report = employee.registerDamage(vehicle, reservation, description, estimatedCost);

        if (reservation != null && reservation.getHandledBy().isEmpty()) {
            reservation.setHandledBy(employee);
        }

        save();

        return report;
    }



// POJAZDY (MANAGER)

    public Car addCar(Branch branch, String plate, String brand, String model, int year, double rate, int mileage,
                      String color, CarType bodyType, int seats, int doors, FuelType fuel, TransmissionType transmission, int trunk) {
        Manager manager = requireManager();

        Car car = new Car(plate, brand, model, year, rate, mileage, color, bodyType, seats, doors, fuel, transmission, trunk);

        manager.addVehicle(branch, car);

        save();

        return car;
    }

    public Truck addTruck(Branch branch, String plate, String brand, String model, int year,
                          double rate, int mileage, String color, double loadCapacity, double cargoVolume, boolean liftgate) {
        Manager manager = requireManager();

        Truck truck = new Truck(plate, brand, model, year, rate, mileage, color, loadCapacity, cargoVolume, liftgate);

        manager.addVehicle(branch, truck);

        save();

        return truck;
    }

    public Motorcycle addMotorcycle(Branch branch, String plate, String brand, String model, int year, double rate,
                                    int mileage, String color, MotorcycleType type, int engineCapacity, boolean abs) {
        Manager manager = requireManager();

        Motorcycle motorcycle = new Motorcycle(plate, brand, model, year, rate, mileage, color, type, engineCapacity, abs);

        manager.addVehicle(branch, motorcycle);

        save();

        return motorcycle;
    }

    public void removeVehicle(Vehicle vehicle) {
        Manager manager = requireManager();
        manager.removeVehicle(vehicle);

        save();
    }

// PERSONEL (MANAGER)

    public Employee addEmployee(Branch branch, boolean asManager, String login, String password, String firstName,
                                String lastName, LocalDate birthDate, String phone, String email, Address address, String employeeNumber) {
        Manager manager = requireManager();

        Employee employee = asManager ? new Manager(login, password, firstName, lastName, birthDate, phone, email, address, employeeNumber)
                : new Employee(login, password, firstName, lastName, birthDate, phone, email, address, employeeNumber);

        try {
            manager.addEmployee(branch, employee);
        } catch (ValidationException e) {
            ObjectExtent.removeFromExtent(employee);
            throw e;
        }

        save();

        return employee;
    }

    public void removeEmployee(Employee employee) {
        Manager manager = requireManager();

        manager.removeEmployee(employee);

        save();
    }
}
