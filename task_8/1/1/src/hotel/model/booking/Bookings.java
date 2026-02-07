package hotel.model.booking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.model.room.Room;
import hotel.model.users.client.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import hotel.model.service.Services;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bookings {
    private Integer id;

    @JsonIgnore
    private Client client;

    @JsonIgnore
    private List<Services> services;

    @JsonIgnore
    private Room room;

    ///Дата заезда
    private LocalDate checkInDate;

    ///Дата выезда
    private LocalDate checkOutDate;

    private double totalPrice;
    private LocalDateTime createdAt = LocalDateTime.now();

    @Override
    public String toString() {
        return "Bookings{" +
                "id=" + id +
                ", client=" + client +
                ", room=" + room +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
