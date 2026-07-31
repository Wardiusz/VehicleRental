package com.wardiusz.vehiclerental.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

public class Address implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String street;
    private String houseNumber;
    private String apartmentNumber;
    private String postalCode;
    private String city;
    private String country;

    public Address(String street, String houseNumber, String apartmentNumber, String postalCode, String city, String country) {
        setStreet(street);
        setHouseNumber(houseNumber);
        setApartmentNumber(apartmentNumber);
        setPostalCode(postalCode);
        setCity(city);
        setCountry(country);
    }

    private static String required(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("Pole \"" + fieldName + "\" jest wymagane.");
        }

        return value.trim();
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = required(street, "ulica");
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = required(houseNumber, "numer domu");
    }

    public Optional<String> getApartmentNumber() {
        return Optional.ofNullable(apartmentNumber);
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = (apartmentNumber == null || apartmentNumber.trim().isEmpty()) ? null : apartmentNumber.trim();
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        String v = required(postalCode, "kod pocztowy");

        if (!v.matches("\\d{2}-\\d{3}")) {
            throw new ValidationException("Kod pocztowy musi mieć format XX-XXX.");
        }

        this.postalCode = v;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = required(city, "miasto");
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = required(country, "kraj");
    }

    @Override
    public String toString() {
        String flat = apartmentNumber != null ? "/" + apartmentNumber : "";

        return "ul. " + street + " " + houseNumber + flat + ", " + postalCode + " " + city + ", " + country;
    }
}
