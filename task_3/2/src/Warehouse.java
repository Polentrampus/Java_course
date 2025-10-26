import Furniture.Furniture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Warehouse {
    private final double capacity = 100.0F;
    private double currentVolume = 0.0F;
    private List<Furniture> furnitureList = new ArrayList();
    private static int currentId = 0;

    public double getCurrent_volume() {
        return capacity - this.currentVolume;
    }

    public boolean addFurniture(Furniture furniture) {
        double volume = furniture.calculateVolume();
        if (this.currentVolume + volume > 95.0F) {
            System.out.println("Недостаточно места на складе!");
            return false;
        } else {
            furniture.setId(currentId++);
            this.furnitureList.add(furniture);
            this.currentVolume += volume;
            return true;
        }
    }

    public void removeFurniture(int id) {
        Optional<Furniture> furniture = findFurnitureById(id);
        if (furniture.isPresent()) {
            this.currentVolume -= (furniture.get()).calculateVolume();
            this.furnitureList.remove(furniture.get());
        } else {
            throw new IllegalArgumentException("Мебель с ID " + id + " не найдена!");
        }
    }

    //использую find, потому что в реальности у каждого товара будет свой уникальный айди
    //не порядковый, а хэшрованный, и вероятность return null существует
    public Optional<Furniture> findFurnitureById(int id) {
        for (Furniture furniture : this.furnitureList) {
            if (furniture.getId() == id) {
                return Optional.of(furniture);
            }
        }
        return Optional.empty();
    }

    public boolean checkAvailableSpace() {
        return this.currentVolume < 95.0F;
    }

    //мы можем проверять/рассчитывать вес для разных задач, считаю уместным любое имя метода
    public int checkTotalWeight() {
        int total_weight = 0;

        for (Furniture cur_fur : this.furnitureList) {
            total_weight += cur_fur.getWeight();
        }

        return total_weight;
    }
}
