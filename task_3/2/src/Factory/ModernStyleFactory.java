package Factory;

import Characteristics.Color;
import Characteristics.FurnitureMaterial;
import Characteristics.FurnitureStyle;
import Furniture.Chair;
import Furniture.Sofa;
import Furniture.Table;

public class ModernStyleFactory implements StyleFurnitureFactory {
    public ModernStyleFactory() {
    }

    public Chair createChair() {
        return new Chair(FurnitureStyle.MODERN, FurnitureMaterial.METALL, Color.BLACK, 10000, new int[]{5, 4, 3}, 1);
    }

    public Sofa createSofa() {
        return new Sofa(FurnitureStyle.MODERN, FurnitureMaterial.METALL, Color.BLACK, 10000, new int[]{4, 1, 1}, 54);
    }

    public Table createTable() {
        return new Table(FurnitureStyle.MODERN, FurnitureMaterial.GLASS, Color.BLACK, 10000, new int[]{5, 3, 1}, 65);
    }
}
