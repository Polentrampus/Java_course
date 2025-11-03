package Factory;

import Characteristics.Color;
import Characteristics.FurnitureMaterial;
import Characteristics.FurnitureStyle;
import Furniture.Chair;
import Furniture.Sofa;
import Furniture.Table;

public class LoftStyleFactory implements StyleFurnitureFactory {
    public LoftStyleFactory() {
    }

    public Chair createChair() {
        return new Chair(FurnitureStyle.LOFT, FurnitureMaterial.WOOD, Color.BLACK, 50, new int[]{1, 1, 1}, 5);
    }

    public Sofa createSofa() {
        return new Sofa(FurnitureStyle.LOFT, FurnitureMaterial.GLASS, Color.RED, 500, new int[]{2, 2, 2}, 50);
    }

    public Table createTable() {
        return new Table(FurnitureStyle.LOFT, FurnitureMaterial.ALUMINY, Color.WHITE, 200, new int[]{3, 3, 3}, 20);
    }
}
