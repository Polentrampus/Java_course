package hotel.repository.booking;

import hotel.model.booking.Bookings;
import hotel.util.JsonDataManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class JsonBookingsRepository implements BookingsRepository {
    private final JsonDataManager dataManager = JsonDataManager.getInstance();

    public JsonBookingsRepository() throws IOException {
    }

    @Override
    public Optional<Bookings> findById(int id) throws SQLException {
        return Optional.of(dataManager.getBookings().get(id));
    }

    @Override
    public List<Bookings> findAll() {
        return new ArrayList<>(dataManager.getBookings().values());
    }

    @Override
    public boolean save(Bookings booking) {
        if (booking.getId() == null) {
            int maxId = dataManager.getBookings().keySet().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);
            booking.setId(maxId + 1);
        }

        dataManager.saveBooking(booking);
        return true;
    }

    @Override
    public boolean update(Bookings booking) {
        dataManager.getBookings().put(booking.getId(), booking);
        return true;
    }

    @Override
    public boolean delete(int id) {
        dataManager.deleteBooking(id);
        return true;
    }

    @Override
    public List<Bookings> findActiveBookings(LocalDate date) {
        List<Bookings> result = new ArrayList<>();
        for (Bookings booking : dataManager.getBookings().values()) {
            if (booking.isActiveOn(date)) {
                result.add(booking);
            }
        }
        return result;
    }

    @Override
    public List<Bookings> findByRoomId(int roomId) {
        List<Bookings> bookings = new ArrayList<>();
        dataManager.getBookings().values().forEach(booking -> {
            if (booking.getRoom() == roomId) {
                bookings.add(booking);
            }
        });
        return bookings;
    }

    @Override
    public List<Bookings> findByClientId(int clientId) {
        List<Bookings> bookings = new ArrayList<>();
        dataManager.getBookings().values().forEach(booking -> {
            if (booking.getClient() == clientId) {
                bookings.add(booking);
            }
        });
        return bookings;
    }

    @Override
    public Optional<Bookings> findActiveByRoomId(int roomId, LocalDate date) {
        List<Bookings> bookings = new ArrayList<>();
        dataManager.getBookings().values().forEach(curBooking -> {
            if (curBooking.getRoom() == roomId && curBooking.isActiveOn(date)) {
                bookings.add(curBooking);
            }
        });

        return bookings.isEmpty() ? Optional.empty() : Optional.of(bookings.get(0));
    }
}
