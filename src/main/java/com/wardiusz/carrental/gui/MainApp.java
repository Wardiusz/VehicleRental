package com.wardiusz.carrental.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.wardiusz.carrental.model.User;
import com.wardiusz.carrental.service.RentalService;

import java.util.Objects;

public class MainApp extends Application {

    private static final RentalService service = new RentalService();

    private BorderPane root;

    public static RentalService getService() { return service; }

    @Override
    public void init() {
        service.load();
    }

    @Override
    public void start(Stage stage) {
        root = new BorderPane();

        Scene scene = new Scene(root, 1150, 900);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/app.css")).toExternalForm());
        stage.setTitle("MAS - Wypożyczalnia Pojazdów");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> service.save());

        showLogin();

        stage.show();
    }

    @Override
    public void stop() {
        service.save();
    }

    public void showLogin() {
        root.setTop(null);
        root.setLeft(null);

        setContent(new LoginView(this).getView());
    }

    public void showMain() {
        User user = service.getCurrentUser();
        root.setTop(buildHeader(user));
        root.setLeft(buildMenu());

        showWelcome();
    }

    public void showWelcome() {
        User user = service.getCurrentUser();
        VBox box = new VBox(12,
                UiHelper.title("Witaj, " + user.getFullName() + "!"),
                new Label("Wybierz sekcję z menu obok aby zmienić dane profilu, przeglądać pojazdy lub zmienić dane profilu!")
        );

        box.setPadding(new Insets(30));

        setContent(box);
    }

    private HBox buildHeader(User user) {
        Label appName = new Label("MAS - Wypożyczalnia Pojazdów");
        appName.getStyleClass().add("app-name-label");

        Label userLabel = new Label(user.getFullName() + " — " + user.getRoleName());
        userLabel.getStyleClass().add("user-label");

        Button logout = new Button("Wyloguj");
        logout.setOnAction(e -> {
            service.save();
            service.logout();
            showLogin();
        });

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(14, appName, spacer, userLabel, logout);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 16, 10, 16));
        header.getStyleClass().add("app-header");

        return header;
    }

    private VBox buildMenu() {
        VBox menu = new VBox(6);
        menu.setPadding(new Insets(14));
        menu.setPrefWidth(230);
        menu.getStyleClass().add("menu");

        addMenuButton(menu, "Przeglądaj pojazdy", () -> setContent(new VehicleBrowserView(this).getView()));

        addMenuButton(menu, "Obsługa rezerwacji", () -> setContent(new ReservationStatusView(this).getView()));

        addMenuButton(menu, "Mój profil", () -> setContent(new ProfileView(this).getView()));

        return menu;
    }

    private void addMenuButton(VBox menu, String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> action.run());

        menu.getChildren().add(button);
    }

    public void setContent(Node node) {
        root.setCenter(node);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
