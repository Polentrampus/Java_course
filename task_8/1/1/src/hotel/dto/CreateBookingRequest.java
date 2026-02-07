package hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingRequest {
    private Integer clientId;
    private Integer roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}