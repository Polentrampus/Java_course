package hotel.dto;

import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.service.Services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BookingDTO {
    private Integer id;
    private Integer clientId;
    private String clientName;
    private String clientSurname;
    private Integer roomId;
    private Integer roomNumber;
    private List<Services> services;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;

    public static BookingDTO fromEntity(Bookings booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());

        if (booking.getClient() != null) {
            dto.setClientId(booking.getClient().getId());
            dto.setClientName(booking.getClient().getName());
            dto.setClientSurname(booking.getClient().getSurname());
        }

        if (booking.getRoom() != null) {
            dto.setRoomId(booking.getRoom().getId());
            dto.setRoomNumber(booking.getRoom().getNumber());
        }

        if (booking.getServices() != null) {
            dto.setServices(booking.getServices());
        }

        if (booking.getCheckInDate() != null) {
            dto.setCheckInDate(booking.getCheckInDate());
        }

        if (booking.getCheckOutDate() != null) {
            dto.setCheckOutDate(booking.getCheckOutDate());
        }

        if (booking.getStatus() != null) {
            dto.setStatus(booking.getStatus());
        }

        if (booking.getTotalPrice() != null) {
            dto.setTotalPrice(booking.getTotalPrice());
        }

        if (booking.getCreatedAt() != null) {
            dto.setCreatedAt(booking.getCreatedAt());
        }

        return dto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientSurname() {
        return clientSurname;
    }

    public void setClientSurname(String clientSurname) {
        this.clientSurname = clientSurname;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public List<Services> getServices() {
        return services;
    }

    public void setServices(List<Services> services) {
        this.services = services;
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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}