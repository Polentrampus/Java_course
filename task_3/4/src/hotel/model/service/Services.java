package hotel.model.service;


public enum Services {
    BAGGAGE_STORAGE("Хранение багажа", "Услуга хранения вашего багажа", 500.0),
    FOOD_DELIVERY("Заказ питания", "Доставка еды в номер", 1200.0),
    TRANSFER("Трансфер", "Трансфер из/в аэропорт", 2500.0),
    FITNESS_CENTER("Фитнес-центр", "Доступ в фитнес-центр", 800.0),
    MINI_BAR("Мини-бар", "В стоимость номера будет включен мини бар", 100.3),
    SPA("SPA", "Возможность посетить спа 3 раза за все время пребывание в отеле бесплатно", 0.0),
    CONCIERGE("Консьерж-сервис", "Консьерж-сервис", 10.0),
    LAUNDRY("Прачечная", "Использование прачечной", 200.0);

    // Поля класса
    private final String name;
    private final String description;
    private double price;

    // Конструктор enum
    Services(String name, String description, double price) {
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

    // Дополнительные полезные методы
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