package hotel.dto;

import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRoomRequest {
    Integer idRoom;
    RoomCategory roomCategory;
    RoomStatus roomStatus;
    RoomType roomType;
    BigDecimal price;
    int capacity;
}
