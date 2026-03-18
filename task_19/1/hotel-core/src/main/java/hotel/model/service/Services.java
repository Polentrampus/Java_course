package hotel.model.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.model.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@jakarta.persistence.Entity
@Table(name = "services")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Services implements Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;

    public Services(Integer id, String name, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Services() {
    }

    public boolean isFree() {
        return Objects.equals(price, BigDecimal.ZERO);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}