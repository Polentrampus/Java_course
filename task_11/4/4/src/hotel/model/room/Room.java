package hotel.model.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.annotation.Component;
import hotel.service.export_import.Entity;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class Room implements Entity {
    private int number;
    private RoomCategory category;
    private RoomStatus status;
    private RoomType type;
    private int capacity;
    private int price;

    public Room(int number, RoomCategory category, RoomStatus status, RoomType type, int capacity, int price) {
        this.number = number;
        this.category = category;
        this.status = status;
        this.type = type;
        this.capacity = capacity;
        this.price = price;
    }

    public Room() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public RoomCategory getCategory() {
        return category;
    }

    public void setCategory(RoomCategory category) {
        this.category = category;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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
