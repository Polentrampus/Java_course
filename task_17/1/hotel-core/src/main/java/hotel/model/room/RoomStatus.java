package hotel.model.room;

public enum RoomStatus {
    AVAILABLE("ДОСТУПНО"),
    OCCUPIED("ЗАНЯТО"),
    CLEANING("УБОРКА"),
    MAINTENANCE("РЕМОНТИРУЕМЫЙ");

    private final String description;

    RoomStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}