package hotel.model.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.annotation.Component;
import hotel.controller.export_import.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Room implements Entity {
    private int number;
    private RoomCategory category;
    private RoomStatus status;
    private RoomType type;
    private int capacity;
    private int price;

    @Override
    public String toString() {
        return "Room{" +
                "number=" + number +
                ", category=" + category +
                ", status=" + status +
                ", type=" + type +
                ", capacity=" + capacity +
                ", price=" + price +
                '}';
    }

    @Override
    public int getId() {
        return number;
    }

    @Override
    public void setId(int id) {
        this.number = id;
    }
}
