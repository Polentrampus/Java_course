package org.hotel.model.booking;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
class BookingServiceId implements java.io.Serializable {

    private Long bookingId;
    private Long serviceId;
}
