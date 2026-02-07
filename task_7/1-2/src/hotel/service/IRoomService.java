package hotel.service;

import hotel.exception.room.RoomAlreadyExistsException;
import hotel.exception.room.RoomNotFoundException;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface IRoom {

    List<Room> listAvailableRooms(RoomFilter filter);
    List<Room> listAvailableRoomsByDate(RoomFilter filter, LocalDate date);
    List<Room> sortRooms(RoomFilter filter);
    void changeRoomPrice(int idRoom, int newPrice);
    void addRoom(RoomCategory category, RoomStatus status, RoomType type, int capacity, int roomNumber, int price);
    List<Room> requestListRoom(RoomFilter filter);
    void requestListRoomAndPrice(RoomFilter filter);
}
