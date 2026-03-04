package hotel.model.filter;

import hotel.model.room.Room;
import lombok.Getter;

import java.util.Comparator;

@Getter
public enum RoomFilter {
    CAPACITY(Comparator.comparing(Room::getCapacity)),
    TYPE(Comparator.comparing(Room::getType)),
    CATEGORY(Comparator.comparing(Room::getCategory)),
    ID(Comparator.comparing(Room::getId)),
    PRICE(Comparator.comparing(Room::getPrice));

    private final Comparator<Room> comparator;

    RoomFilter(Comparator<Room> comparator) {
        this.comparator = comparator;
    }

}
