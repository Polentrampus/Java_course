package hotel.dto;

import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.service.Services;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
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

    public static BookingDTO from(Bookings booking) {
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
}