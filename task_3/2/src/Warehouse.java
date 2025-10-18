import Furniture.Furniture;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Warehouse {
    private String location;
    private final double capacity = (double)95.0F;
    private double current_volume = (double)0.0F;
    private List<Furniture> furniture_list = new ArrayList();
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    public Warehouse() {
    }

    public double getCurrent_volume() {
        return (double)95.0F - this.current_volume;
    }

    public void add_furniture(Furniture furniture) {
        double volume = (double)furniture.calculateVolume();
        if (this.current_volume + volume > (double)95.0F) {
            throw new IllegalStateException("Недостаточно места на складе!");
        } else {
            furniture.setId(idGenerator.getAndIncrement());
            this.furniture_list.add(furniture);
            this.current_volume += volume;
        }
    }

    public void remove_furniture(int id) {
        Optional<Furniture> furniture = Optional.ofNullable(this.find_furniture_by_id(id));
        if (furniture.isPresent()) {
            this.current_volume -= (double)((Furniture)furniture.get()).calculateVolume();
            this.furniture_list.remove(furniture.get());
        } else {
            throw new IllegalArgumentException("Мебель с ID " + id + " не найдена!");
        }
    }

    public Furniture find_furniture_by_id(int id) {
        for(Furniture furniture : this.furniture_list) {
            if (furniture.getId() == id) {
                return furniture;
            }
        }

        return null;
    }

    public boolean check_available_space() {
        return this.current_volume < (double)95.0F;
    }

    public int check_total_weight() {
        int total_weight = 0;

        for(Furniture cur_fur : this.furniture_list) {
            total_weight += cur_fur.getWeight();
        }

        return total_weight;
    }
}
