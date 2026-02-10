package hotel.service;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.SqlException;
import hotel.model.booking.Bookings;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.room.RoomRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public abstract class RoomService implements IRoomService {
    protected RoomRepository roomRepository;
    protected BookingsRepository bookingsRepository;

    public void setHotelRepository(RoomRepository roomRepository, BookingsRepository bookingsRepository) {
        this.roomRepository = roomRepository;
        this.bookingsRepository = bookingsRepository;
    }

    public RoomService() {
    }

    @Override
    public List<Room> listAvailableRooms(RoomFilter filter) {
        try {
            return roomRepository.listAvailableRooms(filter);
        }catch (Exception e){
            throw new HotelException(ErrorCode.ROOM_NOT_AVAILABLE, e.getMessage());
        }
    }

    @Override
    public List<Room> listAvailableRoomsByDate(RoomFilter filter, LocalDate date) {
        try{
            List<Room> rooms = roomRepository.findAll();
            for (Bookings bookings : bookingsRepository.findAll()) {
                if (bookings.getCheckInDate().isBefore(date) && bookings.getCheckOutDate().isAfter(date)) {
                    rooms.remove(bookings.getRoom());
                }
            }
            return rooms;
        }catch(SqlException e){
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR, e.getMessage());
        }
    }

    @Override
    public List<Room> sortRooms(RoomFilter filter) {
        try {
            return roomRepository.sortRooms(filter);
        }catch (Exception e){
            throw new HotelException(ErrorCode.ROOM_NOT_AVAILABLE, e.getMessage());
        }
    }

    public void requestListRoomAndPrice(RoomFilter filter) {
        roomRepository.requestListRoomAndPrice(filter);
    }

    public void setTotalPrice(int roomNumber, BigDecimal newPrice) throws SQLException {
        Room room = roomRepository.findById(roomNumber).get();
        room.setPrice(newPrice);
    }

    @Override
    public Optional<Room> findById(int id) throws SQLException {
        return roomRepository.findById(id);
    }

    @Override
    public List<Room> findAll() {
        try {
            return roomRepository.findAll();
        }catch (Exception e){
            throw new HotelException(ErrorCode.ROOM_NOT_AVAILABLE, e.getMessage());
        }
    }

    @Override
    public boolean save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public boolean update(Room room) {
        return roomRepository.update(room);
    }

    @Override
    public boolean delete(int id) {
        return roomRepository.delete(id);
    }
}
