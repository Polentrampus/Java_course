package hotel.model.filter;

import hotel.model.room.Room;

import java.util.Comparator;

public enum FilterRoom {
    CAPACITY(Comparator.comparing(Room::getCapacity)),
    TYPE(Comparator.comparing(Room::getType)),
    CATEGORY(Comparator.comparing(Room::getCategory)),
    ID(Comparator.comparing(Room::getNumber)),
    PRICE(Comparator.comparing(Room::getPrice));

    private final Comparator<Room> comparator;

    FilterRoom(Comparator<Room> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Room> getComparator() {
        return comparator;
    }
}
