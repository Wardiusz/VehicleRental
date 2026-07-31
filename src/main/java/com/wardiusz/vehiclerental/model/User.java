package com.wardiusz.vehiclerental.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;

public abstract class User extends ObjectExtent implements Addressable {
    private static final long serialVersionUID = 1L;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{9,15}$");

    private String login;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String email;
    private Address address;
    private final LocalDate registrationDate;

    protected User(String login, String password, String firstName, String lastName, LocalDate birthDate, String phoneNumber, String email, Address address) {
        try {
            validateLoginUnique(login);
            this.login = login.trim();
            setPassword(password);
            setFirstName(firstName);
            setLastName(lastName);
            setBirthDate(birthDate);
            setPhoneNumber(phoneNumber);
            setEmail(email);
            setAddress(address);
            this.registrationDate = LocalDate.now();
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    private static void validateLoginUnique(String login) {
        if (login == null || login.trim().length() < 3) {
            throw new ValidationException("Login musi mieć co najmniej 3 znaki.");
        }

        if (findByLogin(login) != null) {
            throw new ValidationException("Login \"" + login.trim() + "\" jest już zajęty.");
        }
    }

    public static User findByLogin(String login) {
        if (login == null) {
            return null;
        }

        List<User> users = getExtentWithSubclasses(User.class);

        for (User u : users) {
            if (u.login != null && u.login.equalsIgnoreCase(login.trim())) {
                return u;
            }
        }

        return null;
    }

    static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();

            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Brak algorytmu SHA-256", e);
        }
    }

    public boolean checkPassword(String password) {
        return password != null && passwordHash.equals(hashPassword(password));
    }

    public boolean login(String password) {
        return checkPassword(password);
    }

    public static boolean validatePhone(String phoneNumber) {
        String v = phoneNumber == null ? "" : phoneNumber.replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(v).matches();
    }

    public static boolean validateEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public final void setPassword(String password) {
        if (password == null || password.length() < 5) {
            throw new ValidationException("Hasło musi mieć co najmniej 5 znaków.");
        }

        this.passwordHash = hashPassword(password);
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (!checkPassword(oldPassword)) {
            throw new ValidationException("Nieprawidłowe dotychczasowe hasło.");
        }

        setPassword(newPassword);
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public final void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("Imię jest wymagane.");
        }

        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public final void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("Nazwisko jest wymagane.");
        }

        this.lastName = lastName.trim();
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public final void setBirthDate(LocalDate birthDate) {
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Data urodzenia jest nieprawidłowa.");
        }

        this.birthDate = birthDate;
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public final void setPhoneNumber(String phoneNumber) {
        if (!validatePhone(phoneNumber)) {
            throw new ValidationException("Numer telefonu musi zawierać 9-15 cyfr.");
        }

        this.phoneNumber = phoneNumber.replaceAll("[\\s-]", "");
    }

    public String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        if (!validateEmail(email)) {
            throw new ValidationException("Adres e-mail ma nieprawidłowy format.");
        }

        this.email = email.trim();
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public final void setAddress(Address address) {
        if (address == null) {
            throw new ValidationException("Adres jest wymagany.");
        }
        this.address = address;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public abstract String getRoleName();

    @Override
    public String toString() {
        return getFullName() + " (" + login + ")";
    }
}
