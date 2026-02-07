package hotel.dto;

import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.service.Services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateBookingRequest {
    private Integer clientId;
    private Integer roomId;
    private List<Services> services = new ArrayList<>();
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;

    public CreateBookingRequest(Integer clientId, Integer roomId, LocalDate checkInDate,
                                LocalDate checkOutDate, List<Services> services, BookingStatus status) {
        inicialize(clientId, roomId, checkInDate, checkOutDate, services, status);
    }

    public CreateBookingRequest(Bookings booking) {
        this.clientId = booking.getClient();
        this.roomId = booking.getRoom();
        this.checkInDate = booking.getCheckInDate();
        this.checkOutDate = booking.getCheckOutDate();
        this.status = booking.getStatus();
        this.services = booking.getServices();
    }

    public CreateBookingRequest() {
    }

    public void inicialize(Integer clientId, Integer roomId, LocalDate checkInDate,
                           LocalDate checkOutDate, List<Services> services, BookingStatus status) {
        this.clientId = clientId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.services = services;
        this.status = status;
    }

    public List<Services> getServices() {
        return services;
    }

    public void setServices(List<Services> services) {
        this.services = services;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
}