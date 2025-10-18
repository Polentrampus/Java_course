package Factory;

import Characteristics.Color;
import Characteristics.FurnitureMaterial;
import Characteristics.FurnitureStyle;
import Furniture.Chair;
import Furniture.Sofa;
import Furniture.Table;

public class ClassicStyleFactory implements StyleFurnitureFactory {
    public ClassicStyleFactory() {
    }

    public Chair createChair() {
        return new Chair(FurnitureStyle.CLASSIC, FurnitureMaterial.ALUMINY, Color.BLACK, 5040, new int[]{4, 4, 4}, 15);
    }

    public Sofa createSofa() {
        return new Sofa(FurnitureStyle.CLASSIC, FurnitureMaterial.WOOD, Color.BLACK, 76000, new int[]{1, 1, 1}, 81);
    }

    public Table createTable() {
        return new Table(FurnitureStyle.CLASSIC, FurnitureMaterial.WOOD, Color.BLACK, 8800, new int[]{3, 3, 4}, 42);
    }
}
