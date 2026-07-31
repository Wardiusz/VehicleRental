package com.wardiusz.vehiclerental.service;

import com.wardiusz.vehiclerental.model.*;
import com.wardiusz.vehiclerental.model.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DataSeeder {

    private DataSeeder() {}

    public static void seed() {
//  Oddziały

        Branch warszawa = new Branch("Oddział Warszawa", "+48221234567",
                new Address("Marszałkowska", "10", null, "00-590", "Warszawa", "Polska"));
        Branch krakow = new Branch("Oddział Kraków", "+48121234567",
                new Address("Floriańska", "5", "2", "31-019", "Kraków", "Polska"));
        Branch gdansk = new Branch("Oddział Gdańsk", "+48581234567",
                new Address("Długi Targ", "8", null, "80-828", "Gdańsk", "Polska"));
        Branch wroclaw = new Branch("Oddział Wrocław", "+48711234567",
                new Address("Rynek", "14", "3", "50-101", "Wrocław", "Polska"));

//  Usługi dodatkowe

        AdditionalService gps = new AdditionalService("Nawigacja GPS",
                "Przenośna nawigacja z aktualnymi mapami", 25.0);
        AdditionalService fotelik = new AdditionalService("Fotelik dziecięcy",
                "Fotelik dla dziecka 9-36 kg", 30.0);
        AdditionalService ubezpieczenie = new AdditionalService("Ubezpieczenie pełne",
                null, 80.0);
        AdditionalService dodatkowyKierowca = new AdditionalService("Dodatkowy kierowca",
                "Upoważnienie drugiej osoby", 40.0);
        AdditionalService bagaznik = new AdditionalService("Bagażnik dachowy",
                null, 35.0);
        AdditionalService lancuchy = new AdditionalService("Łańcuchy śniegowe",
                "Komplet na koła napędowe", 20.0);
        AdditionalService pelnyBak = new AdditionalService("Pełny bak przy odbiorze",
                "Zwrot bez tankowania", 300.0);

//  Pojazdy

        Car corolla = new Car("WA 12345", "Toyota", "Corolla", 2022, 150.0, 42000, "Srebrny",
                CarType.SEDAN, 5, 4, FuelType.HYBRID, TransmissionType.AUTOMATIC, 470);
        Car tiguan = new Car("WA 22222", "Volkswagen", "Tiguan", 2023, 220.0, 18000, "Czarny",
                CarType.SUV, 5, 5, FuelType.DIESEL, TransmissionType.AUTOMATIC, 615);
        Car bmw = new Car("WA 44444", "BMW", "420i", 2022, 320.0, 30000, "Granatowy",
                CarType.COUPE, 4, 2, FuelType.PETROL, TransmissionType.AUTOMATIC, 445);
        Car tesla = new Car("WA 33330", "Tesla", "Model 3", 2023, 300.0, 15000, "Biały",
                CarType.SEDAN, 5, 4, FuelType.ELECTRIC, TransmissionType.AUTOMATIC, 425);
        Car fiesta = new Car("KR 33333", "Ford", "Fiesta", 2020, 100.0, 76000, "Czerwony",
                CarType.HATCHBACK, 5, 5, FuelType.PETROL, TransmissionType.MANUAL, 292);
        Car octavia = new Car("KR 11122", "Skoda", "Octavia Kombi", 2021, 170.0, 61000, "Szary",
                CarType.KOMBI, 5, 5, FuelType.DIESEL, TransmissionType.MANUAL, 640);
        Car clio = new Car("WR 40004", "Renault", "Clio", 2019, 90.0, 88000, "Niebieski",
                CarType.HATCHBACK, 5, 5, FuelType.LPG, TransmissionType.MANUAL, 300);
        Car audi = new Car("GD 10001", "Audi", "A5 Cabrio", 2022, 380.0, 22000, "Czerwony",
                CarType.CABRIO, 4, 2, FuelType.PETROL, TransmissionType.AUTOMATIC, 380);

        Truck daily = new Truck("WA 55555", "Iveco", "Daily", 2021, 260.0, 98000, "Biały",
                3.5, 16.0, true);
        Truck sprinter = new Truck("KR 66666", "Mercedes", "Sprinter", 2019, 240.0, 154000, "Biały",
                2.8, 14.0, false);
        Truck man = new Truck("GD 20002", "MAN", "TGL", 2020, 300.0, 120000, "Srebrny",
                5.0, 24.0, true);
        Truck ducato = new Truck("WR 60006", "Fiat", "Ducato", 2021, 230.0, 84000, "Biały",
                2.0, 13.0, false);

        Motorcycle mt07 = new Motorcycle("WA 7777", "Yamaha", "MT-07", 2023, 120.0, 6000, "Niebieski",
                MotorcycleType.NAKED, 689, true);
        Motorcycle vespa = new Motorcycle("KR 8888", "Vespa", "Primavera", 2022, 70.0, 3000, "Miętowy",
                MotorcycleType.SCOOTER, 125, false);
        Motorcycle harley = new Motorcycle("KR 55550", "Harley-Davidson", "Iron 883", 2021, 200.0, 12000, "Czarny",
                MotorcycleType.CRUISER, 883, true);
        Motorcycle africa = new Motorcycle("GD 30003", "Honda", "Africa Twin", 2022, 180.0, 9000, "Czerwony",
                MotorcycleType.ENDURO, 1084, true);
        Motorcycle ninja = new Motorcycle("WR 50005", "Kawasaki", "Ninja 650", 2023, 160.0, 4000, "Zielony",
                MotorcycleType.SPORT, 649, true);

//  Dodanie pojazdów

        warszawa.addVehicle(corolla);
        warszawa.addVehicle(tiguan);
        warszawa.addVehicle(bmw);
        warszawa.addVehicle(tesla);
        warszawa.addVehicle(daily);
        warszawa.addVehicle(mt07);

        krakow.addVehicle(fiesta);
        krakow.addVehicle(octavia);
        krakow.addVehicle(sprinter);
        krakow.addVehicle(vespa);
        krakow.addVehicle(harley);

        gdansk.addVehicle(audi);
        gdansk.addVehicle(man);
        gdansk.addVehicle(africa);

        wroclaw.addVehicle(clio);
        wroclaw.addVehicle(ducato);
        wroclaw.addVehicle(ninja);

//  Użytkownicy

        Customer c1 = new Customer("jan.kowalski", "haslo123", "Jan", "Kowalski",
                LocalDate.of(1990, 5, 12), "+48600100200", "jan.kowalski@example.com",
                new Address("Puławska", "12", "8", "02-512", "Warszawa", "Polska"),
                "PJ/001234/90");
        Customer c2 = new Customer("anna.nowak", "haslo123", "Anna", "Nowak",
                LocalDate.of(1995, 11, 3), "+48600300400", "anna.nowak@example.com",
                new Address("Długa", "3", null, "31-146", "Kraków", "Polska"),
                "PJ/005678/95");
        Customer c3 = new Customer("tomasz.lewandowski", "haslo123", "Tomasz", "Lewandowski",
                LocalDate.of(1987, 8, 24), "+48600900100", "tomasz.lewandowski@example.com",
                new Address("Grunwaldzka", "56", "4", "80-241", "Gdańsk", "Polska"),
                "PJ/009900/87");
        Customer c4 = new Customer("katarzyna.wojcik", "haslo123", "Katarzyna", "Wójcik",
                LocalDate.of(1998, 3, 17), "+48600220330", "katarzyna.wojcik@example.com",
                new Address("Legnicka", "9", null, "54-203", "Wrocław", "Polska"),
                "PJ/012233/98");

//  Pracownicy

        Employee e1 = new Employee("pracownik", "haslo123", "Piotr", "Wiśniewski",
                LocalDate.of(1988, 2, 20), "+48600500600", "p.wisniewski@wypozyczalnia.pl",
                new Address("Wspólna", "44", "12", "00-687", "Warszawa", "Polska"),
                "E001");

        Employee e2 = new Employee("doradca.k", "haslo123", "Krzysztof", "Kamiński",
                LocalDate.of(1991, 6, 5), "+48600510610", "k.kaminski@wypozyczalnia.pl",
                new Address("Grodzka", "18", "2", "31-006", "Kraków", "Polska"),
                "E002");

        Employee e3 = new Employee("doradca.g", "haslo123", "Agnieszka", "Mazur",
                LocalDate.of(1993, 12, 1), "+48600520620", "a.mazur@wypozyczalnia.pl",
                new Address("Ogarna", "7", null, "80-826", "Gdańsk", "Polska"),
                "E003");

        Manager m1 = new Manager("kierownik.k", "haslo123", "Robert", "Nowicki",
                LocalDate.of(1980, 4, 22), "+48600720820", "r.nowicki@wypozyczalnia.pl",
                new Address("Karmelicka", "30", "5", "31-131", "Kraków", "Polska"),
                "M002");

        Manager m2 = new Manager("kierownik", "haslo123", "Maria", "Zielińska",
                LocalDate.of(1982, 7, 8), "+48600700800", "m.zielinska@wypozyczalnia.pl",
                new Address("Nowy Świat", "20", null, "00-373", "Warszawa", "Polska"),
                "M001");

        warszawa.employ(e1);
        warszawa.employ(m2);
        krakow.employ(e2);
        krakow.employ(m1);
        gdansk.employ(e3);

//  Przykładowe rezerwacje

        // POTWIERDZONA (opłacona)
        Reservation r1 = new Reservation(c2, warszawa);
        ReservationDetails d1 = r1.addItem(corolla, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(6));
        d1.addService(gps);
        r1.addPayment(r1.getTotalPrice(), PaymentMethod.CARD, PaymentStatus.PAID);
        r1.confirm();

        // OCZEKUJĄCA (opłacona)
        Reservation r2 = new Reservation(c1, krakow);
        ReservationDetails d2 = r2.addItem(fiesta, LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(9));
        d2.addService(fotelik);
        d2.addService(ubezpieczenie);
        r2.addPayment(r2.getTotalPrice(), PaymentMethod.TRANSFER, PaymentStatus.PAID);

        // AKTYWNA (wydany pojazd)
        Reservation r3 = new Reservation(c1, warszawa);
        ReservationDetails d3 = r3.addItem(tesla, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5));
        d3.addService(pelnyBak);
        r3.addPayment(r3.getTotalPrice(), PaymentMethod.BLIK, PaymentStatus.PAID);
        r3.confirm();
        r3.activate();

        // ZAKOŃCZONA z uszkodzeniem
        Reservation r4 = new Reservation(c2, warszawa);
        ReservationDetails d4 = r4.addItem(bmw, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4));
        r4.addPayment(r4.getTotalPrice(), PaymentMethod.CARD, PaymentStatus.PAID);
        r4.confirm();
        r4.activate();
        new DamageReport(bmw, r4, "Rysa na przednim zderzaku", 600.0);
        r4.complete();

        // ANULOWANA (ze zwrotem)
        Reservation r5 = new Reservation(c3, krakow);
        ReservationDetails d5 = r5.addItem(octavia, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(12));
        r5.addPayment(r5.getTotalPrice(), PaymentMethod.CASH, PaymentStatus.PAID);
        r5.confirm();
        r5.cancel();

        // POTWIERDZONA, wiele pojazdów
        Reservation r6 = new Reservation(c3, gdansk);
        ReservationDetails d6a = r6.addItem(audi, LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(8));
        d6a.addService(dodatkowyKierowca);
        ReservationDetails d6b = r6.addItem(africa, LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(8));
        d6b.addService(gps);
        r6.addPayment(r6.getTotalPrice(), PaymentMethod.CARD, PaymentStatus.PAID);
        r6.confirm();

        // AKTYWNA
        Reservation r7 = new Reservation(c3, gdansk);
        ReservationDetails d7 = r7.addItem(man, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
        r7.addPayment(r7.getTotalPrice(), PaymentMethod.TRANSFER, PaymentStatus.PAID);
        r7.confirm();
        r7.activate();

        // OCZEKUJĄCA
//        Reservation r8 = new Reservation(c4, wroclaw);
//        ReservationDetails d8 = r8.addItem(clio, LocalDateTime.now().plusDays(9), LocalDateTime.now().plusDays(14));
//        d8.addService(bagaznik);
//        d8.addService(lancuchy);
//        r8.addPayment(r8.getTotalPrice(), PaymentMethod.BLIK, PaymentStatus.PENDING);
    }
}