package com.wardiusz.carrental.gui;

import com.wardiusz.carrental.model.Address;
import com.wardiusz.carrental.model.ValidationException;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RegisterView {

    private final MainApp app;

    public RegisterView(MainApp app) {
        this.app = app;
    }

    public Node getView() {
        TextField login = new TextField();
        PasswordField password = new PasswordField();
        TextField firstName = new TextField();
        TextField lastName = new TextField();
        DatePicker birthDate = new DatePicker();
        TextField phone = new TextField();
        phone.setPromptText("+48600100200");

        TextField email = new TextField();
        TextField license = new TextField();
        TextField street = new TextField();
        TextField house = new TextField();
        TextField apartment = new TextField();
        apartment.setPromptText("(opcjonalnie)");

        TextField postal = new TextField();
        postal.setPromptText("00-000");

        TextField city = new TextField();
        TextField country = new TextField("Polska");

        GridPane form = UiHelper.formGrid();

        int r = 0;

        UiHelper.addRow(form, r++, "Login:", login);
        UiHelper.addRow(form, r++, "Hasło:", password);
        UiHelper.addRow(form, r++, "Imię:", firstName);
        UiHelper.addRow(form, r++, "Nazwisko:", lastName);
        UiHelper.addRow(form, r++, "Data urodzenia:", birthDate);
        UiHelper.addRow(form, r++, "Telefon:", phone);
        UiHelper.addRow(form, r++, "E-mail:", email);
        UiHelper.addRow(form, r++, "Nr prawa jazdy:", license);
        UiHelper.addRow(form, r++, "Ulica:", street);
        UiHelper.addRow(form, r++, "Nr domu:", house);
        UiHelper.addRow(form, r++, "Nr mieszkania:", apartment);
        UiHelper.addRow(form, r++, "Kod pocztowy:", postal);
        UiHelper.addRow(form, r++, "Miasto:", city);
        UiHelper.addRow(form, r, "Kraj:", country);

        Button submit = new Button("Zarejestruj konto");
        submit.setOnAction(e -> {
            try {
                Address address = new Address(street.getText(), house.getText(), apartment.getText(),
                        postal.getText(), city.getText(), country.getText());

                MainApp.getService().registerCustomer(login.getText(), password.getText(),
                        firstName.getText(), lastName.getText(), birthDate.getValue(),
                        phone.getText(), email.getText(), address, license.getText());

                UiHelper.info("Konto zostało utworzone. Możesz się zalogować.");
                app.showLogin();
            } catch (ValidationException ex) {
                UiHelper.error(ex.getMessage());
            }
        });

        Button back = new Button("Powrót");
        back.setOnAction(e -> app.showLogin());

        VBox box = new VBox(14, UiHelper.title("Rejestracja konta klienta"),
                form, new HBox(10, submit, back));
        box.setPadding(new Insets(24));

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);

        return scroll;
    }
}
