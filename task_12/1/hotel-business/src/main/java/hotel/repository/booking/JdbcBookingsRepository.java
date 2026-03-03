package hotel.repository.booking;

import hotel.dao.BookingDAO;
import hotel.model.booking.Bookings;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public class JdbcBookingsRepository implements BookingsRepository {
    private BookingDAO bookingDAO =  BookingDAO.getInstance();

    @Override
    public Optional<Bookings> findById(int id) throws SQLException {
        return bookingDAO.findById(id);
    }

    @Override
    public List<Bookings> findAll() {
        return bookingDAO.findAll();
    }

    @Override
    public boolean save(Bookings bookings) {
        bookingDAO.save(bookings);
        return true;
    }

    @Override
    public boolean update(Bookings bookings) {
        bookingDAO.update(bookings);
        return true;
    }

    @Override
    public boolean delete(int id) {
        bookingDAO.delete(id);
        return true;
    }

    @Override
    public List<Bookings> findActiveBookings(LocalDate date) {
        return bookingDAO.findActiveBookings(date);
    }

    @Override
    public List<Bookings> findByRoomId(int roomId) {
        return bookingDAO.findByRoomId(roomId);
    }

    @Override
    public List<Bookings> findByClientId(int clientId) {
        return bookingDAO.findByClientId(clientId);
    }

    @Override
    public Optional<Bookings> findActiveByRoomId(int roomId, LocalDate date) {
        return bookingDAO.findActiveByRoomId(roomId, date);
    }
}
