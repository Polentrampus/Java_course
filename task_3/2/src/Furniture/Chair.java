package Furniture;

import Characteristics.Color;
import Characteristics.FurnitureMaterial;
import Characteristics.FurnitureStyle;

public class Chair extends Furniture {
    public Chair(FurnitureStyle style, FurnitureMaterial material, Color color, int price, int[] dimensions, int weight) {
        super(style, material, color, price, dimensions, weight);
    }
}
