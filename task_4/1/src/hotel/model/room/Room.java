package hotel.model.room;

public class Room {
    private int number;
    private RoomCategory category;
    private RoomStatus status;
    private RoomType type;
    private int capacity;
    private int price;

    public Room(RoomCategory category, RoomStatus status, RoomType type,
                int roomNumber, int price, int capacity) {
        this.category = category;
        this.status = status;
        this.type = type;
        this.number = roomNumber;
        this.price = price;
        this.capacity = capacity;
    }

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
}
