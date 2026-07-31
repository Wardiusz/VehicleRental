package com.wardiusz.vehiclerental.gui;

import com.wardiusz.vehiclerental.model.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import com.wardiusz.vehiclerental.model.enums.PaymentMethod;
import com.wardiusz.vehiclerental.model.enums.ReservationStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReservationStatusView {

    private final MainApp app;
    private final VBox container = new VBox();

    private Branch selectedBranch;
    private Reservation selectedReservation;

    public ReservationStatusView(MainApp app) {
        this.app = app;
    }

    public Node getView() {
        container.setPadding(new Insets(16));
        container.setSpacing(10);
        showBranchList();

        return container;
    }

//  Lista odziałów
    private void showBranchList() {
        container.getChildren().clear();

        List<Branch> branches = MainApp.getService().getBranches();

        // przepływ alternatywny: brak oddziałów
        if (branches.isEmpty()) {
            UiHelper.info("Brak dostępnych oddziałów");

            Button back = new Button("Powrót");
            back.setOnAction(e -> app.showWelcome());

            container.getChildren().addAll(
                    UiHelper.title("Obsługa rezerwacji"),
                    new Label("Brak dostępnych oddziałów."),
                    back
            );

            return;
        }

        ListView<Branch> list = new ListView<>(FXCollections.observableArrayList(branches));

        Button next = new Button("Wybierz");
        next.disableProperty().bind(list.getSelectionModel().selectedItemProperty().isNull());

        next.setOnAction(e -> {
            selectedBranch = list.getSelectionModel().getSelectedItem();
            showReservationList();
        });

        Button back = new Button("Powrót");
        back.setOnAction(e -> app.showWelcome());

        container.getChildren().addAll(
                UiHelper.title("Obsługa rezerwacji — wybierz oddział"),
                list,
                new HBox(10, next, back)
        );
    }

//  Lista rezerwacji w oddziale
    private void showReservationList() {
        container.getChildren().clear();
        List<Reservation> reservations = new ArrayList<>(selectedBranch.getReservations());

        // przepływ alternatywny: brak rezerwacji
        if (reservations.isEmpty()) {
            UiHelper.info("Brak rezerwacji dla tego oddziału");
            showBranchList();
            return;
        }

        ListView<Reservation> list = new ListView<>(FXCollections.observableArrayList(reservations));
        list.setCellFactory(v -> new ListCell<>() {
            @Override
            protected void updateItem(Reservation item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : ReservationSupport.shortLabel(item));
            }
        });

        Button choose = new Button("Wybierz");
        choose.disableProperty().bind(list.getSelectionModel().selectedItemProperty().isNull());

        choose.setOnAction(e -> {
            selectedReservation = list.getSelectionModel().getSelectedItem();
            showReservationDetails();
        });

        Button back = new Button("Powrót");
        back.setOnAction(e -> showBranchList());

        container.getChildren().addAll(
                UiHelper.title("Rezerwacje — " + selectedBranch.getName()),
                list, new HBox(10, choose, back));
    }

    private static Label value(String text) {
        return new Label(text);
    }

    private static Label section(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-label");
        label.setPadding(new Insets(8, 0, 0, 0));

        return label;
    }

    private void showReservationDetails() {
        container.getChildren().clear();

        Reservation res = selectedReservation;
        Customer customer = res.getCustomer();

        String handledBy = res.getHandledBy()
                .map(e -> String.format("%s (nr %s)", e.getFullName(), e.getEmployeeNumber()))
                .orElse("—");

        GridPane info = UiHelper.formGrid();
        int row = 0;

//  Dane klienta
        UiHelper.addRow(info, row++, "Klient:", value(customer.getFullName()));
        UiHelper.addRow(info, row++, "Telefon:", value(customer.getPhoneNumber()));
        UiHelper.addRow(info, row++, "E-mail:", value(customer.getEmail()));

//  Szczegóły rezerwacji
        UiHelper.addRow(info, row++, "Oddział odbioru:", value(selectedReservation.getPickupBranch().toString()));
        UiHelper.addRow(info, row++, "Obsługuje:", value(handledBy));
        UiHelper.addRow(info, row, "Status:", value(selectedReservation.getStatus().toString()));

//  Cena całkowita
        Label totalMoneyLabel = new Label("Cena całkowita:");
        totalMoneyLabel.getStyleClass().add("totalMoney-label");

        Label totalMoney = new Label(UiHelper.money(res.getTotalPrice() + res.getAmountDue()));
        totalMoney.getStyleClass().add("totalMoney");

        VBox content = new VBox(8,
                section("Informacje"), info,
                section("Pozycje rezerwacji"), buildItemsTable(res),
                section("Płatności"), buildPaymentsTable(res),
                section("Zgłoszone uszkodzenia"), buildDamagesTable(res),
                new HBox(10, totalMoneyLabel, totalMoney));
        content.setPadding(new Insets(4, 8, 4, 0));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Button changeStatus = new Button("Zmień status");
        changeStatus.setOnAction(e -> changeStatusDialog());

        Button registerPayment = new Button("Zarejestruj płatność");
        registerPayment.setOnAction(e -> registerPaymentDialog());

        Button registerDamage = new Button("Zarejestruj uszkodzenie");
        registerDamage.setOnAction(e -> registerDamageDialog());

        Button back = new Button("Powrót");
        back.setOnAction(e -> showReservationList());

        container.getChildren().addAll(
                UiHelper.title("Rezerwacja " + ReservationSupport.identifier(res)),
                scroll,
                new HBox(10, changeStatus, registerPayment, registerDamage, back));
    }

    private void changeStatusDialog() {
        ChoiceDialog<ReservationStatus> dialog = new ChoiceDialog<>(selectedReservation.getStatus(), ReservationStatus.values());

        dialog.setTitle("Zmiana statusu");
        dialog.setHeaderText("Aktualny status: " + selectedReservation.getStatus());
        dialog.setContentText("Nowy status:");
        Optional<ReservationStatus> chosen = dialog.showAndWait();

        if (chosen.isEmpty() || chosen.get() == selectedReservation.getStatus()) {
            return;
        }

        if (!UiHelper.confirm("Czy na pewno zmienić status rezerwacji "
                + ReservationSupport.identifier(selectedReservation) + " na: " + chosen.get() + "?")) {
            return;
        }

        try {
            MainApp.getService().changeReservationStatus(selectedReservation, chosen.get());
            UiHelper.info("Status rezerwacji został zmieniony na: " + chosen.get() + ".");
        } catch (ValidationException ex) {
            UiHelper.error(ex.getMessage());
        }

        showReservationDetails();
    }

    private void registerPaymentDialog() {
        ChoiceDialog<PaymentMethod> dialog = new ChoiceDialog<>(PaymentMethod.CARD, PaymentMethod.values());

        dialog.setTitle("Rejestracja płatności");
        dialog.setHeaderText("Do zapłaty: " + UiHelper.money(selectedReservation.getAmountDue()));
        dialog.setContentText("Metoda płatności:");

        Optional<PaymentMethod> method = dialog.showAndWait();

        if (method.isEmpty() || method.get() == PaymentMethod.CARD) {
            return;
        }

        try {
            MainApp.getService().registerPaidPayment(selectedReservation, method.get());
            UiHelper.info("Płatność została zarejestrowana."
                    + (selectedReservation.getStatus() == ReservationStatus.CONFIRMED ? " Rezerwacja została potwierdzona." : ""));
        } catch (ValidationException ex) {
            UiHelper.error(ex.getMessage());
        }

        showReservationDetails();
    }

    private void registerDamageDialog() {
        List<Vehicle> vehicles = new ArrayList<>();

        for (ReservationDetails d : selectedReservation.getItems()) {
            vehicles.add(d.getVehicle());
        }

        if (vehicles.isEmpty()) {
            UiHelper.error("Rezerwacja nie zawiera pojazdów.");
            return;
        }

        ChoiceDialog<Vehicle> vehicleDialog = new ChoiceDialog<>(vehicles.get(0), vehicles);

        vehicleDialog.setTitle("Zgłoszenie uszkodzenia");
        vehicleDialog.setHeaderText("Którego pojazdu dotyczy uszkodzenie?");
        vehicleDialog.setContentText("Pojazd:");

        Optional<Vehicle> vehicle = vehicleDialog.showAndWait();

        if (vehicle.isEmpty())
            return;

        TextInputDialog descDialog = new TextInputDialog();

        descDialog.setTitle("Zgłoszenie uszkodzenia");
        descDialog.setHeaderText("Opis uszkodzenia:");

        Optional<String> desc = descDialog.showAndWait();

        if (desc.isEmpty())
            return;

        TextInputDialog costDialog = new TextInputDialog("0");

        costDialog.setTitle("Zgłoszenie uszkodzenia");
        costDialog.setHeaderText("Szacowany koszt naprawy (zł):");

        Optional<String> cost = costDialog.showAndWait();

        if (cost.isEmpty())
            return;

        try {
            double value = Double.parseDouble(cost.get().replace(',', '.'));
            MainApp.getService().registerDamage(selectedReservation, vehicle.get(), desc.get(), value);
            UiHelper.info("Uszkodzenie zostało zarejestrowane. Pojazd otrzymał status USZKODZONY.");
        } catch (NumberFormatException ex) {
            UiHelper.error("Koszt naprawy musi być liczbą.");
        } catch (ValidationException ex) {
            UiHelper.error(ex.getMessage());
        }
        showReservationDetails();
    }

    // Tabelki (metody pomocnicze)
    private TableView<Payment> buildPaymentsTable(Reservation res) {
        TableView<Payment> table = new TableView<>();

        TableColumn<Payment, String> amountCol = new TableColumn<>("Kwota");
        amountCol.setCellValueFactory(cd -> new SimpleStringProperty(
                UiHelper.money(cd.getValue().getAmount())));

        TableColumn<Payment, String> methodCol = new TableColumn<>("Metoda");
        methodCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getMethod().toString()));

        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getStatus().toString()));

        TableColumn<Payment, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getDate().format(UiHelper.DATE)));

        table.getColumns().add(amountCol);
        table.getColumns().add(methodCol);
        table.getColumns().add(statusCol);
        table.getColumns().add(dateCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setItems(FXCollections.observableArrayList(res.getPayments()));
        table.setPrefHeight(120);
        table.setPlaceholder(new Label("(brak płatności)"));

        return table;
    }

    private TableView<ReservationDetails> buildItemsTable(Reservation res) {
        TableView<ReservationDetails> table = new TableView<>();

        TableColumn<ReservationDetails, String> vehicleCol = new TableColumn<>("Pojazd");
        vehicleCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getVehicle().getTypeName() + " " + cd.getValue().getVehicle().getShortDescription())
        );

        TableColumn<ReservationDetails, String> pickupCol = new TableColumn<>("Odbiór");
        pickupCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getPickupDate().format(UiHelper.DATE))
        );

        TableColumn<ReservationDetails, String> returnCol = new TableColumn<>("Zwrot");
        returnCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getReturnDate().format(UiHelper.DATE))
        );

        TableColumn<ReservationDetails, Number> daysCol = new TableColumn<>("Liczba dób");
        daysCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getDurationDays())
        );

        TableColumn<ReservationDetails, String> servicesCol = new TableColumn<>("Usługi dodatkowe");
        servicesCol.setCellValueFactory(cd -> {
            List<AdditionalService> services = cd.getValue().getServices();
            String text = services.isEmpty() ? "—" : services.stream().map(AdditionalService::getName).collect(Collectors.joining(", "));
            return new SimpleStringProperty(text);
        });

        TableColumn<ReservationDetails, String> priceCol = new TableColumn<>("Cena pozycji");
        priceCol.setCellValueFactory(cd -> new SimpleStringProperty(
                UiHelper.money(cd.getValue().calculatePrice()))
        );

        table.getColumns().add(vehicleCol);
        table.getColumns().add(pickupCol);
        table.getColumns().add(returnCol);
        table.getColumns().add(daysCol);
        table.getColumns().add(servicesCol);
        table.getColumns().add(priceCol);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setItems(FXCollections.observableArrayList(res.getItems()));
        table.setPrefHeight(150);
        table.setPlaceholder(new Label("(brak pozycji)"));

        return table;
    }

    private TableView<DamageReport> buildDamagesTable(Reservation res) {
        TableView<DamageReport> table = new TableView<>();

        TableColumn<DamageReport, String> dateCol = new TableColumn<>("Data zgłoszenia");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getReportDate().format(UiHelper.DATE))
        );

        TableColumn<DamageReport, String> vehicleCol = new TableColumn<>("Pojazd");
        vehicleCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getVehicle().getLicensePlate())
        );

        TableColumn<DamageReport, String> descCol = new TableColumn<>("Opis");
        descCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getDescription())
        );

        TableColumn<DamageReport, String> costCol = new TableColumn<>("Szacowany koszt");
        costCol.setCellValueFactory(cd -> new SimpleStringProperty(
                UiHelper.money(cd.getValue().getEstimatedCost()))
        );

        table.getColumns().add(dateCol);
        table.getColumns().add(vehicleCol);
        table.getColumns().add(descCol);
        table.getColumns().add(costCol);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setItems(FXCollections.observableArrayList(res.getDamageReports()));
        table.setPrefHeight(110);
        table.setPlaceholder(new Label("(brak zgłoszeń)"));

        return table;
    }
}
