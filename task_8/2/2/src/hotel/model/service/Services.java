package hotel.model.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.controller.export_import.Entity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Services implements Entity {
    private int id;
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String description;
    @Setter
    @Getter
    private double price;

    public boolean isFree() {
        return price == 0.0;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}