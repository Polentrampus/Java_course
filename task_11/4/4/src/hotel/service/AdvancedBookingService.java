package hotel.service;

import hotel.config.HotelConfiguration;
import hotel.dto.CreateBookingRequest;
import hotel.model.booking.Bookings;
import hotel.model.service.Services;

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
    public Optional<Bookings> getBookingByIdClient(Integer idClient) {
        return Optional.empty();
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

    @Override
    public double givOutCheck(int idClient) {
        Bookings bookings = getBookingByIdClient(idClient).get();
        double sum = 0;
        sum += bookings.getServices().stream().mapToDouble(Services::getPrice).sum();
        sum += bookings.getRoom().getPrice();
        long daysBetween = bookings.getCheckOutDate().toEpochDay()
                - bookings.getCheckInDate().toEpochDay();
        sum *= daysBetween;
        return sum;
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
