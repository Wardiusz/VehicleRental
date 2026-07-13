package com.wardiusz.carrental.gui;

import com.wardiusz.carrental.model.ValidationException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class LoginView {

    private final MainApp app;

    public LoginView(MainApp app) {
        this.app = app;
    }

    public Node getView() {
        TextField loginField = new TextField();
        loginField.setPromptText("login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("hasło");

        Button loginButton = new Button("Zaloguj");
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(e -> {
            try {
                MainApp.getService().login(loginField.getText(), passwordField.getText());
                app.showMain();
            } catch (ValidationException ex) {
                UiHelper.error(ex.getMessage());
            }
        });

        Button registerButton = new Button("Zarejestruj się");
        registerButton.setOnAction(e -> app.setContent(new RegisterView(app).getView()));

        GridPane form = UiHelper.formGrid();

        UiHelper.addRow(form, 0, "Login:", loginField);
        UiHelper.addRow(form, 1, "Hasło:", passwordField);

        form.setAlignment(Pos.CENTER);

        Label hint = new Label("Konto demo:\nL: pracownik\nH: haslo123");
        hint.getStyleClass().add("hint-label");

        VBox box = new VBox(16, UiHelper.title("Logowanie"),
                form, loginButton, registerButton, hint);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));

        return box;
    }
}
