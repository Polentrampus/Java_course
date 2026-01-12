package hotel.model.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class BookingServiceId implements java.io.Serializable {
    private Long bookingId;
    private Long serviceId;
}
