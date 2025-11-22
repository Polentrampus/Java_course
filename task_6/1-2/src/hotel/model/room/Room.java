package hotel.model.room;

import hotel.controller.export_import.Entity;

public class Room implements Entity {
    private int number;
    private RoomCategory category;
    private RoomStatus status;
    private RoomType type;
    private int capacity;
    private int price;

    public Room(int id, RoomCategory category, RoomStatus status, RoomType type,
                int price, int capacity) {
        this.number = id;
        this.category = category;
        this.status = status;
        this.type = type;
        this.price = price;
        this.capacity = capacity;
    }

    public Room() {}

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public RoomCategory getCategory() {
        return category;
    }

    public void setCategory(RoomCategory category) {
        this.category = category;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Room{" +
                "number=" + number +
                ", category=" + category +
                ", status=" + status +
                ", type=" + type +
                ", capacity=" + capacity +
                ", price=" + price +
                '}';
    }

    @Override
    public int getId() {
        return number;
    }

    @Override
    public void setId(int id) {
        this.number = id;
    }
}
