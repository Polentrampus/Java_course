package Furniture;

import Characteristics.Color;
import Characteristics.FurnitureMaterial;
import Characteristics.FurnitureStyle;

public class Table extends Furniture {
    public Table(FurnitureStyle style, FurnitureMaterial material, Color color, int price, int[] dimensions, int weight) {
        super(style, material, color, price, dimensions, weight);
    }
}
