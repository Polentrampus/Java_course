package hotel.dto;

import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.service.Services;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingRequest {
    private Integer clientId;
    private Integer roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<Services> services = new ArrayList<>();
    private BookingStatus status;

    public CreateBookingRequest(Bookings booking) {
        this.clientId = booking.getClient().getId();
        this.roomId = booking.getRoom().getId();
        this.checkInDate = booking.getCheckInDate();
        this.checkOutDate = booking.getCheckOutDate();
        this.status = booking.getStatus();
        this.services = booking.getServices();
    }
}