package com.wardiusz.vehiclerental.test;

import com.wardiusz.vehiclerental.model.*;
import com.wardiusz.vehiclerental.model.enums.*;
import com.wardiusz.vehiclerental.service.DataSeeder;
import com.wardiusz.vehiclerental.service.RentalService;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class IntegrationTest {

    private static int passed = 0;

    public static void main(String[] args) throws Exception {
        ObjectExtent.clearExtents();
        DataSeeder.seed();
        RentalService service = new RentalService();

        // Uwierzytelnianie
        User user = service.login("jan.kowalski", "haslo123");
        check("logowanie poprawne", user instanceof Customer);
        try {
            service.login("jan.kowalski", "zle_haslo");
            fail("logowanie błędnym hasłem powinno zostać odrzucone");
        } catch (ValidationException e) {
            check("błędne hasło odrzucone", true);
        }
        try {
            service.login("nie.istnieje", "haslo123");
            fail("logowanie nieistniejącym loginem powinno zostać odrzucone");
        } catch (ValidationException e) {
            check("nieistniejący login odrzucony", true);
        }

        // Walidacje: pełnoletność, unikalność loginu i rejestracji
        Address address = new Address("Testowa", "1", null, "00-001", "Warszawa", "Polska");
        try {
            new Customer("mlody", "haslo123", "Adam", "Młody", LocalDate.now().minusYears(16),
                    "+48555111222", "adam@example.com", address, "PJ/9/09");
            fail("niepełnoletni klient powinien zostać odrzucony");
        } catch (ValidationException e) {
            check("pełnoletność klienta wymuszona (MIN_AGE)", true);
        }
        try {
            new Customer("jan.kowalski", "haslo123", "Jan", "Drugi", LocalDate.of(1990, 1, 1),
                    "+48555111333", "jan2@example.com", address, "PJ/10/90");
            fail("duplikat loginu powinien zostać odrzucony");
        } catch (ValidationException e) {
            check("unikalność loginu na ekstensji", true);
        }
        try {
            new Car("WA 12345", "Duplikat", "Auto", 2020, 100, 0, "Biały",
                    CarType.SEDAN, 5, 4, FuelType.PETROL, TransmissionType.MANUAL, 300);
            fail("duplikat numeru rejestracyjnego powinien zostać odrzucony");
        } catch (ValidationException e) {
            check("unikalność numeru rejestracyjnego na ekstensji", true);
        }

        // Cykl życia rezerwacji
        Customer jan = (Customer) user;
        Branch warszawa = ObjectExtent.getExtent(Branch.class).get(0);
        Vehicle tiguan = Vehicle.findByPlate("WA 22222");
        Vehicle mt07 = Vehicle.findByPlate("WA 7777");
        AdditionalService gps = ObjectExtent.getExtent(AdditionalService.class).get(0);

        RentalService.CartItem item1 = new RentalService.CartItem(tiguan,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(4), List.of(gps));
        RentalService.CartItem item2 = new RentalService.CartItem(mt07,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), List.of());
        Reservation reservation = service.placeReservation(jan, warszawa,
                List.of(item1, item2), PaymentMethod.CARD, true);

        check("rezerwacja po opłaceniu jest POTWIERDZONA",
                reservation.getStatus() == ReservationStatus.CONFIRMED);
        check("rezerwacja w pełni opłacona", reservation.isFullyPaid());
        check("pojazdy po potwierdzeniu ZAREZERWOWANE",
                tiguan.getStatus() == VehicleStatus.RESERVED
                        && mt07.getStatus() == VehicleStatus.RESERVED);
        double expected = tiguan.calculateCost(3) + gps.getPrice() + mt07.calculateCost(1);
        check("cena całkowita = suma pozycji (atrybut pochodny)",
                Math.abs(reservation.getTotalPrice() - expected) < 0.001);

        // Strażniki maszyny stanów
        check("POTWIERDZONA → ZAKOŃCZONA zabronione",
                !reservation.canChangeStatusTo(ReservationStatus.COMPLETED));
        try {
            reservation.changeStatus(ReservationStatus.COMPLETED);
            fail("niedozwolone przejście powinno zostać zablokowane");
        } catch (ValidationException e) {
            check("niedozwolone przejście zgłasza wyjątek", true);
        }
        reservation.activate();
        check("POTWIERDZONA → AKTYWNA, pojazdy WYNAJĘTE",
                reservation.getStatus() == ReservationStatus.ACTIVE
                        && tiguan.getStatus() == VehicleStatus.RENTED);
        try {
            service.cancelReservation(jan, reservation);
            fail("anulowanie AKTYWNEJ rezerwacji powinno zostać zablokowane");
        } catch (ValidationException e) {
            check("blokada anulowania rezerwacji aktywnej", true);
        }

        // Uszkodzenie podczas obsługi
        service.login("pracownik", "haslo123");
        DamageReport report = service.registerDamage(reservation, mt07, "Rysa na baku", 350.0);
        check("pojazd po zgłoszeniu ma status USZKODZONY",
                mt07.getStatus() == VehicleStatus.DAMAGED);
        check("zgłoszenie powiązane z rezerwacją",
                report.getReservation().orElse(null) == reservation);

        // Zakończenie: pojazd wraca do DOSTĘPNY, uszkodzony pozostaje USZKODZONY
        service.changeReservationStatus(reservation, ReservationStatus.COMPLETED);
        check("po zwrocie pojazd DOSTĘPNY", tiguan.getStatus() == VehicleStatus.AVAILABLE);
        check("uszkodzony pojazd pozostaje USZKODZONY", mt07.getStatus() == VehicleStatus.DAMAGED);
        check("pracownik obsługujący zapisany",
                reservation.getHandledBy().isPresent());

        // Anulowanie ze zwrotem płatności (rezerwacja oczekująca z seedera)
        Reservation pending = null;
        for (Reservation r : ObjectExtent.getExtent(Reservation.class)) {
            if (r.getStatus() == ReservationStatus.PENDING) {
                pending = r;
            }
        }
        check("istnieje rezerwacja oczekująca (dane przykładowe)", pending != null);
        Customer owner = pending.getCustomer();
        service.login(owner.getLogin().equals("jan.kowalski") ? "jan.kowalski" : "anna.nowak", "haslo123");
        pending.addPayment(pending.getTotalPrice(), PaymentMethod.BLIK, PaymentStatus.PAID);
        pending.confirm();
        service.cancelReservation(owner, pending);
        check("anulowanie zwraca opłacone płatności",
                pending.getPayments().stream().anyMatch(p -> p.getStatus() == PaymentStatus.REFUNDED));

        // Blokada usunięcia pojazdu z aktywną rezerwacją
        service.login("kierownik", "haslo123");
        Vehicle corolla = Vehicle.findByPlate("WA 12345"); // w rezerwacji POTWIERDZONEJ z seedera
        try {
            service.removeVehicle(corolla);
            fail("usunięcie pojazdu z aktywną rezerwacją powinno zostać zablokowane");
        } catch (ValidationException e) {
            check("blokada usunięcia pojazdu z aktywną rezerwacją", true);
        }
        Vehicle sprinter = Vehicle.findByPlate("KR 66666");
        int before = ObjectExtent.getExtentWithSubclasses(Vehicle.class).size();
        service.removeVehicle(sprinter);
        check("wolny pojazd usunięty z ekstensji",
                ObjectExtent.getExtentWithSubclasses(Vehicle.class).size() == before - 1
                        && Vehicle.findByPlate("KR 66666") == null);

        // Zatrudnienie i unikalność kwalifikatora [employeeNumber]
        Address a2 = new Address("Prosta", "2", "1", "00-002", "Warszawa", "Polska");
        Employee nowy = service.addEmployee(warszawa, false, "e.nowy", "haslo123", "Ewa", "Nowa",
                LocalDate.of(1994, 4, 4), "+48555999888", "e.nowa@wypozyczalnia.pl", a2, "E002");
        check("zatrudnienie: kwalifikowany dostęp findEmployee",
                warszawa.findEmployee("E002") == nowy);
        try {
            service.addEmployee(warszawa, false, "e.dubel", "haslo123", "Jan", "Dubel",
                    LocalDate.of(1994, 4, 4), "+48555999777", "j.dubel@wypozyczalnia.pl", a2, "E002");
            fail("duplikat numeru pracownika w oddziale powinien zostać odrzucony");
        } catch (ValidationException e) {
            check("unikalność numeru pracownika w oddziale", true);
        }

        // Serializacja: zapis → wyczyszczenie → odczyt
        File file = new File("data-test" + File.separator + "extents.dat");
        int users = ObjectExtent.getExtentWithSubclasses(User.class).size();
        int vehicles = ObjectExtent.getExtentWithSubclasses(Vehicle.class).size();
        int reservations = ObjectExtent.getExtent(Reservation.class).size();
        ReservationStatus statusBefore = reservation.getStatus();
        ObjectExtent.saveExtents(file);
        ObjectExtent.clearExtents();
        check("po wyczyszczeniu ekstensje puste",
                ObjectExtent.getExtentWithSubclasses(User.class).isEmpty());
        ObjectExtent.loadExtents(file);
        check("liczność ekstensji po odczycie zgodna",
                ObjectExtent.getExtentWithSubclasses(User.class).size() == users
                        && ObjectExtent.getExtentWithSubclasses(Vehicle.class).size() == vehicles
                        && ObjectExtent.getExtent(Reservation.class).size() == reservations);
        check("wyszukiwanie po loginie działa po odczycie",
                User.findByLogin("jan.kowalski") != null);
        Reservation loaded = null;
        for (Reservation r : ObjectExtent.getExtent(Reservation.class)) {
            if (r.getId().equals(reservation.getId())) {
                loaded = r;
            }
        }
        check("status rezerwacji zachowany po odczycie",
                loaded != null && loaded.getStatus() == statusBefore);
        Customer janLoaded = (Customer) User.findByLogin("jan.kowalski");
        Branch branchLoaded = ObjectExtent.getExtent(Branch.class).get(0);
        Reservation fresh = new Reservation(janLoaded, branchLoaded);
        check("nowa rezerwacja otrzymuje odrębny UUID (36 znaków)",
                fresh.getId().length() == 36 && !fresh.getId().equals(loaded.getId()));

        System.out.println("\nWSZYSTKIE TESTY OK (" + passed + " sprawdzeń).");
    }

    private static void check(String name, boolean condition) {
        if (!condition) {
            fail(name);
        }
        passed++;
        System.out.println("[OK] " + name);
    }

    private static void fail(String name) {
        throw new AssertionError("[BŁĄD] " + name);
    }
}
