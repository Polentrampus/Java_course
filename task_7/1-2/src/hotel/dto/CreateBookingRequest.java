package org.hotel.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBookingRequest {

    @NotNull(message = "ID клиента обязательно")
    private Long clientId;

    @NotNull(message = "ID комнаты обязательно")
    private Long roomId;

    @NotNull(message = "Дата заезда обязательна")
    @Future(message = "Дата заезда должна быть в будущем")
    private LocalDate checkInDate;

    @NotNull(message = "Дата выезда обязательна")
    @Future(message = "Дата выезда должна быть в будущем")
    private LocalDate checkOutDate;
}