package hotel.model.service;

import java.util.HashMap;
import java.util.Map;

public class Services {
    private final String name;
    private final String description;
    private double price;

    private Services(String name, String description, double price) {
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

    @Override
    public String toString() {
        return String.format("%s: %s (Цена: %.2f руб.)", name, description, price);
    }

    private static final Map<String, Services> SERVICES = new HashMap<>();

    static {
        addService("Хранение багажа", "Услуга хранения вашего багажа", 500.0);
        addService("Заказ питания", "Доставка еды в номер", 1200.0);
        addService("Трансфер", "Трансфер из/в аэропорт", 2500.0);
        addService("Фитнес-центр", "Доступ в фитнес-центр", 800.0);
    }

    public static void addService(String nameService, String description, double price){
        SERVICES.put(nameService, new Services(nameService, description, price));
    }

    public void removeService(String nameService){
        SERVICES.remove(nameService);
    }

    public static Services findService(String nameService){
        if(SERVICES.containsKey(nameService)){
            return SERVICES.get(nameService);
        }
        System.out.println("Такой услуги не существует!");
        return null;
    }

    public void setPrice(int newPrice) {
        this.price = newPrice;
    }
}
