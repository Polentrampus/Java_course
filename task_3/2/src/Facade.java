import Furniture.Furniture;

import java.io.PrintStream;
import java.util.List;

public class Facade {
    private final Warehouse warehouse;

    public Facade(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void addFurnitureUntilFull(List<Furniture> furnitureList) {
        for (Furniture furniture : furnitureList) {
            if (!this.warehouse.checkAvailableSpace() || this.warehouse.getCurrent_volume() - (double) furniture.calculateVolume() < (double) 0.0F) {
                System.out.println("Склад заполнен! '" + furniture.getClass().getSimpleName() + "' не добавлена.");
                break;
            }

            if (this.warehouse.addFurniture(furniture)) {
                System.out.println("Добавлено: " +
                        furniture.getClass().getSimpleName() +
                        " (ID: " + furniture.getId() + ")");
            } else {
                System.out.println(furniture.getClass().getSimpleName() + "не был добавлен в склад");
            }
        }

    }

    public void printTotalWeight() {
        System.out.println("Общий вес мебели на складе: " + this.warehouse.checkTotalWeight() + " кг");
    }
}
