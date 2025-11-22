package hotel.model.service;


import hotel.controller.export_import.Entity;

public class Services implements Entity {
    private int id;
    private final String name;
    private final String description;
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

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public boolean isFree() {
        return price == 0.0;
    }
//
//    @Override
//    public String toString() {
//        return name + " - " + price + " руб." + (isFree() ? " (бесплатно)" : "");
//    }


    @Override
    public String toString() {
        return name;
    }

    public void setPrice(int newPrice) {
        this.price = newPrice;
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