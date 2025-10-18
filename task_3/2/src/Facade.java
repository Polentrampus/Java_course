import Furniture.Furniture;
import java.io.PrintStream;
import java.util.List;

public class Facade {
    private final Warehouse warehouse;

    public Facade(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void addFurnitureUntilFull(List<Furniture> furnitureList) {
        for(Furniture furniture : furnitureList) {
            if (!this.warehouse.check_available_space() || this.warehouse.getCurrent_volume() - (double)furniture.calculateVolume() < (double)0.0F) {
                System.out.println("Склад заполнен! '" + furniture.getClass().getSimpleName() + "' не добавлена.");
                break;
            }

            this.warehouse.add_furniture(furniture);
            PrintStream var10000 = System.out;
            String var10001 = furniture.getClass().getSimpleName();
            var10000.println("Добавлено: " + var10001 + " (ID: " + furniture.getId() + ")");
        }

    }

    public void printTotalWeight() {
        System.out.println("Общий вес мебели на складе: " + this.warehouse.check_total_weight() + " кг");
    }
}
