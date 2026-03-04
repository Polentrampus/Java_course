package hotel.model.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "rooms")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Room {
    @Id
    private Integer number;
    @Enumerated(EnumType.STRING)
    private RoomCategory category;
    @Enumerated(EnumType.STRING)
    private RoomStatus status = RoomStatus.AVAILABLE;
    @Enumerated(EnumType.STRING)
    private RoomType type;
    private int capacity;
    private BigDecimal price;

    public Room(Integer number, RoomCategory category, RoomStatus status, RoomType type, int capacity, BigDecimal price) {
        this.number = number;
        this.category = category;
        this.status = status;
        this.type = type;
        this.capacity = capacity;
        this.price = price;
    }

    public Room() {
    }

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

    public Integer getId() {
        return number;
    }

    public void setId(int id) {
        this.number = id;
    }
}
