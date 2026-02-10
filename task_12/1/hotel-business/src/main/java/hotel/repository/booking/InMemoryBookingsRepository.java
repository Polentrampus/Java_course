package hotel.repository.booking;


import hotel.model.booking.Bookings;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryBookingsRepository implements BookingsRepository {
    private Map<Integer, Bookings> bookingMap = new HashMap<>();
    private int nextId = 1;

    @Override
    public Optional<Bookings> findById(int id) {
        return Optional.ofNullable(bookingMap.get(id));
    }

    @Override
    public List<Bookings> findAll() {
        return List.copyOf(bookingMap.values());
    }

    @Override
    public boolean save(Bookings booking) {
        if (booking.getId() == 0) {
            booking.setId(nextId++);
        }
        bookingMap.put(booking.getId(), booking);
        return true;
    }

    @Override
    public boolean update(Bookings booking) {
        if (bookingMap.containsKey(booking.getId())) {
            bookingMap.put(booking.getId(), booking);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        return bookingMap.remove(id) != null;
    }

    @Override
    public List<Bookings> findActiveBookings(LocalDate date) {
        return bookingMap.values().stream()
                .filter(booking -> booking.isActiveOn(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Bookings> findByRoomId(int roomId) {
        return bookingMap.values().stream()
                .filter(booking -> booking.getRoom() == roomId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Bookings> findByClientId(int clientId) {
        return bookingMap.values().stream()
                .filter(booking -> booking.getClient() == clientId)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Bookings> findActiveByRoomId(int roomId, LocalDate date) {
        return bookingMap.values().stream()
                .filter(booking -> booking.getRoom() == roomId)
                .filter(booking -> booking.isActiveOn(date))
                .findFirst();
    }
}
