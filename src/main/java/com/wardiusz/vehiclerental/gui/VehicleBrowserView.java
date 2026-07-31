package com.wardiusz.vehiclerental.gui;

import com.wardiusz.vehiclerental.model.Branch;
import com.wardiusz.vehiclerental.model.Vehicle;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class VehicleBrowserView {

    private final MainApp app;
    private final TableView<Vehicle> table = new TableView<>();
    private final ComboBox<Branch> branchFilter = new ComboBox<>();
    private final CheckBox onlyAvailable = new CheckBox("Tylko dostępne");
    private final Label details = new Label();

    public VehicleBrowserView(MainApp app) {
        this.app = app;
    }

    @SuppressWarnings("unchecked")
    public Node getView() {
        branchFilter.getItems().add(null);
        branchFilter.getItems().addAll(MainApp.getService().getBranches());
        branchFilter.setPromptText("Wszystkie oddziały");
        branchFilter.setOnAction(e -> refresh());
        onlyAvailable.setSelected(true);
        onlyAvailable.setOnAction(e -> refresh());

        TableColumn<Vehicle, String> typeCol = new TableColumn<>("Rodzaj");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("typeName"));

        TableColumn<Vehicle, String> plateCol = new TableColumn<>("Rejestracja");
        plateCol.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));

        TableColumn<Vehicle, String> brandCol = new TableColumn<>("Marka");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));

        TableColumn<Vehicle, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Vehicle, Integer> yearCol = new TableColumn<>("Rok");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("productionYear"));

        TableColumn<Vehicle, Double> rateCol = new TableColumn<>("Stawka/doba [zł]");
        rateCol.setCellValueFactory(new PropertyValueFactory<>("dailyRate"));

        TableColumn<Vehicle, Object> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(typeCol, plateCol, brandCol, modelCol, yearCol, rateCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->
                details.setText(sel == null ? "" : sel.getSpecificInfo() + (sel.getBranch() != null ? "\n" + "Oddział:\t\t" + sel.getBranch() : "")));

        refresh();

        Button back = new Button("Powrót");
        back.setOnAction(e -> app.showWelcome());

        HBox filters = new HBox(12, new Label("Oddział:"), branchFilter, onlyAvailable);
        VBox box = new VBox(10, UiHelper.title("Katalog pojazdów"), filters, table, new HBox(10, back), details);
        box.setPadding(new Insets(16));

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);

        return scroll;
    }

    private void refresh() {
        Branch branch = branchFilter.getValue();
        List<Vehicle> vehicles;

        if (onlyAvailable.isSelected()) {
            vehicles = MainApp.getService().getAvailableVehicles(branch);
        } else {
            vehicles = branch == null ? MainApp.getService().getAllVehicles() : new ArrayList<>(branch.getFleet());
        }

        table.setItems(FXCollections.observableArrayList(vehicles));
    }
}
