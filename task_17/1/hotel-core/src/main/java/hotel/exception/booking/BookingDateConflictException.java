package hotel.exception.booking;

import hotel.exception.ErrorCode;
import hotel.exception.HotelException;

import java.time.LocalDate;

public class BookingDateConflictException extends HotelException {
    public BookingDateConflictException(Integer roomId, LocalDate checkIn, LocalDate checkOut) {
        super(ErrorCode.BOOKING_DATE_CONFLICT,
                "Комната " + roomId + " уже забронирована на указанные даты");
        addDetail("roomId", roomId);
        addDetail("checkIn", checkIn);
        addDetail("checkOut", checkOut);
    }
}