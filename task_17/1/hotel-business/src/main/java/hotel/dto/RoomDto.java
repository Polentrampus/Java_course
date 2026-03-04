package hotel.dto;

import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {
    private Integer number;
    private RoomCategory category;
    private RoomStatus status = RoomStatus.AVAILABLE;
    private RoomType type;
    private int capacity;
    private BigDecimal price;

    public static RoomDto from(Room room) {
        return RoomDto.builder()
                .number(room.getNumber())
                .category(room.getCategory())
                .status(room.getStatus())
                .type(room.getType())
                .capacity(room.getCapacity())
                .price(room.getPrice())
                .build();
    }
}
