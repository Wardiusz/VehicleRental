package com.wardiusz.carrental.model;

import java.io.Serial;
import java.util.Optional;

public class AdditionalService extends ObjectExtent {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private double price;

    public AdditionalService(String name, String description, double price) {
        try {
            setName(name);
            setDescription(description);
            setPrice(price);
        } catch (ValidationException e) {
            removeFromExtent(this);
            throw e;
        }
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nazwa usługi jest wymagana.");
        }

        this.name = name.trim();
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public final void setDescription(String description) {
        this.description = (description == null || description.trim().isEmpty()) ? null : description.trim();
    }

    public double getPrice() {
        return price;
    }

    public final void setPrice(double price) {
        if (price < 0) {
            throw new ValidationException("Cena usługi nie może być ujemna.");
        }

        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f zł)", name, price);
    }
}
