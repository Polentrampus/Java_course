package hotel.model.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.service.export_import.Entity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Services implements Entity {
    private int id;
    private String name;
    private String description;
    private double price;

    public Services(int id, String name, String description, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isFree() {
        return price == 0.0;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}