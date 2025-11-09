package hotel.model.service;


public class Services {
    private final String name;
    private final String description;
    private double price;

    public Services(String name, String description, double price) {
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

    @Override
    public String toString() {
        return name + " - " + price + " руб." + (isFree() ? " (бесплатно)" : "");
    }

    public void setPrice(int newPrice) {
        this.price = newPrice;
    }
}