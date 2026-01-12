package hotel.service;

import hotel.config.HotelConfiguration;
import hotel.dto.CreateBookingRequest;
import hotel.model.booking.Bookings;
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
    public Optional<Bookings> getBookingById(Integer id) {
        return baseService.getBookingById(id);
    }

    @Override
    public Optional<Bookings> createBooking(CreateBookingRequest request) {
        return baseService.createBooking(request);
    }

    @Override
    public void deleteBookingById(Integer id) {
        baseService.deleteBookingById(id);
    }

    @Override
    public Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) {
        return baseService.updateBooking(request, idBooking);
    }

    public List<Bookings> getRoomBookingHistory(int roomId) {
        if (!config.isBookingHistoryEnabled()) {
            throw new IllegalStateException("История бронирований отключена");
        }

        List<Bookings> allBookings = baseService.getAllBookings();
        List<Bookings> roomHistory = new ArrayList<>();

        for (Bookings booking : allBookings) {
            if (booking.getRoom().getNumber() == roomId) {
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
