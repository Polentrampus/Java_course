package hotel.service;

import hotel.config.HotelConfiguration;
import hotel.dto.CreateBookingRequest;
import hotel.model.booking.Bookings;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Реализация через структурный паттерн декоратор:
 * base класс = BookingService - полная реализация интерфейса IBookingService
 */
public class AdvancedBookingService implements IBookingService {
    private HotelConfiguration config;
    private IBookingService baseService;

    public AdvancedBookingService(HotelConfiguration config, IBookingService baseBookingService) {
        this.config = config;
        this.baseService = baseBookingService;
    }

    public AdvancedBookingService() {
    }

    @Override
    public List<Bookings> getAllBookings() {
        return baseService.getAllBookings();
    }

    @Override
    public Optional<Bookings> getBookingById(Integer id) throws SQLException {
        return baseService.getBookingById(id);
    }

    @Override
    public Optional<Bookings> createBooking(CreateBookingRequest request) throws SQLException {
        return baseService.createBooking(request);
    }

    @Override
    public boolean deleteBookingById(Integer id) {
        baseService.deleteBookingById(id);
        return false;
    }

    @Override
    public Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) throws SQLException {
        return baseService.updateBooking(request, idBooking);
    }

    @Override
    public BigDecimal givOutCheck(int idBooking) throws SQLException {
        return baseService.givOutCheck(idBooking);
    }

    @Override
    public Optional<Bookings> findActiveByRoomId(int idRoom, LocalDate date) {
        return baseService.findActiveByRoomId(idRoom, date);
    }

    @Override
    public Optional<Bookings> findActiveByClientId(int idClient){
        return baseService.findActiveByClientId(idClient);
    }

    public List<Bookings> getRoomBookingHistory(int roomId) {
        if (!config.isBookingHistoryEnabled()) {
            throw new IllegalStateException("История бронирований отключена");
        }

        List<Bookings> allBookings = baseService.getAllBookings();
        List<Bookings> roomHistory = new ArrayList<>();

        for (Bookings booking : allBookings) {
            if (booking.getRoom() == roomId) {
                roomHistory.add(booking);
            }
        }
        int maxEntries = config.getNumberOfGuestsInRoomHistory(roomId);
        if (maxEntries > 0 && roomHistory.size() > maxEntries) {
            return roomHistory.subList(0, maxEntries);
        }

        return roomHistory;
    }

    public boolean canDeleteBookingRecords(){
        return config.isBookingDeletionAllowed();
    }
}
