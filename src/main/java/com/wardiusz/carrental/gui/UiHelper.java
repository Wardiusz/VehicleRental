package com.wardiusz.carrental.gui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public final class UiHelper {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private UiHelper() { }

    public static void error(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText("Błąd");
        alert.setTitle("Błąd");
        alert.showAndWait();
    }

    public static void info(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Informacja");
        alert.showAndWait();
    }

    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                new ButtonType("Potwierdź", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE));
        alert.setHeaderText(null);
        alert.setTitle("Potwierdzenie");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent()
                && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
    }

    public static String money(double amount) {
        return String.format("%.2f zł", amount);
    }

    public static Label title(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("title-label");

        return label;
    }

    public static GridPane formGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));
        return grid;
    }

    public static void addRow(GridPane grid, int row, String label, Node control) {
        grid.add(new Label(label), 0, row);
        grid.add(control, 1, row);
    }
}
