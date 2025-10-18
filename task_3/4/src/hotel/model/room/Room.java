package hotel.model.room;

public class Room {
    private int id;
    private RoomCategory category;
    private RoomStatus status = RoomStatus.MAINTENANCE;
    private RoomType type;
    private int floor;
    private int price;

    public Room(RoomCategory category, RoomStatus status, RoomType type, int roomIndex, int price) {
        this.category = category;
        this.status = status;
        this.type = type;
        this.id = roomIndex;
        this.floor = roomIndex/100;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", category=" + category +
                ", status=" + status +
                ", type=" + type +
                ", price=" + price +
                '}';
    }
}
