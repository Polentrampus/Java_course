import Factory.ClassicStyleFactory;
import Factory.LoftStyleFactory;
import Factory.ModernStyleFactory;
import Factory.StyleFurnitureFactory;
import Furniture.Furniture;
import java.util.List;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse();
        Facade facade = new Facade(warehouse);
        StyleFurnitureFactory factory = new ClassicStyleFactory();
        Furniture classicalChair = factory.createChair();
        factory = new LoftStyleFactory();
        Furniture loftTable = factory.createTable();
        Furniture loftChair = factory.createChair();
        factory = new ModernStyleFactory();
        Furniture modernSofa = factory.createSofa();
        facade.addFurnitureUntilFull(List.of(classicalChair, loftTable, modernSofa, loftChair));
        facade.printTotalWeight();
    }
}