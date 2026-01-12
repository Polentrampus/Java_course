package hotel.dto;

import java.time.LocalDate;

public class CreateBookingRequest {
    private Integer clientId;
    private Integer roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public CreateBookingRequest(Integer clientId, Integer roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        inicialize(clientId, roomId, checkInDate, checkOutDate);
    }

    public CreateBookingRequest() {
    }

    public void inicialize(Integer clientId, Integer roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        this.clientId = clientId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
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