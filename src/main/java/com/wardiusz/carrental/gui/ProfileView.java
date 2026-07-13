package com.wardiusz.carrental.gui;

import com.wardiusz.carrental.model.Address;
import com.wardiusz.carrental.model.User;
import com.wardiusz.carrental.model.ValidationException;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProfileView {

    private final MainApp app;

    public ProfileView(MainApp app) {
        this.app = app;
    }

    public Node getView() {
        User user = MainApp.getService().getCurrentUser();
        Address address = user.getAddress();

        Label header = new Label("Login: " + user.getLogin()
                + "\t|\tRola: " + user.getRoleName()
                + "\t|\tData rejestracji: " + user.getRegistrationDate().format(UiHelper.DATE)
                + "\t|\tWiek: " + user.getAge());

        TextField phone = new TextField(user.getPhoneNumber());
        TextField email = new TextField(user.getEmail());
        TextField street = new TextField(address.getStreet());
        TextField house = new TextField(address.getHouseNumber());
        TextField apartment = new TextField(address.getApartmentNumber().orElse(""));
        TextField postal = new TextField(address.getPostalCode());
        TextField city = new TextField(address.getCity());
        TextField country = new TextField(address.getCountry());

        GridPane form = UiHelper.formGrid();

        String[] labels = {
                "Telefon:", "E-mail:", "Ulica:", "Nr domu:",
                "Nr mieszkania:", "Kod pocztowy:", "Miasto:", "Kraj:"
        };
        TextField[] fields = {phone, email, street, house, apartment, postal, city, country};

        for (int r = 0; r < fields.length; r++) {
            UiHelper.addRow(form, r, labels[r], fields[r]);
        }

        Button saveData = new Button("Zapisz dane");

        saveData.setOnAction(e -> {
            try {
                user.setPhoneNumber(phone.getText());
                user.setEmail(email.getText());
                address.setStreet(street.getText());
                address.setHouseNumber(house.getText());
                address.setApartmentNumber(apartment.getText());
                address.setPostalCode(postal.getText());
                address.setCity(city.getText());
                address.setCountry(country.getText());
                MainApp.getService().save();
                UiHelper.info("Dane zostały zapisane.");
            } catch (ValidationException ex) {
                UiHelper.error(ex.getMessage());
            }
        });

        PasswordField oldPassword = new PasswordField();
        PasswordField newPassword = new PasswordField();
        PasswordField repeatPassword = new PasswordField();

        GridPane passwordForm = UiHelper.formGrid();

        UiHelper.addRow(passwordForm, 0, "Dotychczasowe hasło:", oldPassword);
        UiHelper.addRow(passwordForm, 1, "Nowe hasło:", newPassword);
        UiHelper.addRow(passwordForm, 2, "Powtórz nowe hasło:", repeatPassword);

        Button changePassword = new Button("Zmień hasło");
        changePassword.setOnAction(e -> {
            try {
                if (!newPassword.getText().equals(repeatPassword.getText())) {
                    throw new ValidationException("Nowe hasła nie są identyczne.");
                }
                user.changePassword(oldPassword.getText(), newPassword.getText());
                MainApp.getService().save();
                UiHelper.info("Hasło zostało zmienione.");
                oldPassword.clear();
                newPassword.clear();
                repeatPassword.clear();
            } catch (ValidationException ex) {
                UiHelper.error(ex.getMessage());
            }
        });

        Button back = new Button("Powrót");
        back.setOnAction(e -> app.showWelcome());

        VBox box = new VBox(14, UiHelper.title("Mój profil"), header, form, saveData,
                UiHelper.title("Zmiana hasła"), passwordForm, changePassword, new HBox(10, back));
        box.setPadding(new Insets(16));

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);

        return scroll;
    }
}
