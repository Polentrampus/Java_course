package hotel.model.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.annotation.Component;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class Room {
    @Id
    private Integer number;
    @Enumerated(EnumType.STRING)
    private RoomCategory category;
    @Enumerated(EnumType.STRING)
    private RoomStatus status;
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public RoomCategory getCategory() {
        return category;
    }

    public void setCategory(RoomCategory category) {
        this.category = category;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
