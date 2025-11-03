package Furniture;

import Characteristics.Color;
import Characteristics.FurnitureMaterial;
import Characteristics.FurnitureStyle;

public abstract class Furniture {
    private FurnitureStyle style;
    private FurnitureMaterial material;
    private Color color;
    private int id = -1;
    private int price;
    private int[] dimensions = new int[3];
    private int weight;

    public Furniture(FurnitureStyle style, FurnitureMaterial material, Color color, int price, int[] dimensions, int weight) {
        this.style = style;
        this.material = material;
        this.color = color;
        this.price = price;
        this.dimensions = dimensions;
        this.weight = weight;
    }

    public int getId() {
        return this.id;
    }

    public int[] getDimensions() {
        return this.dimensions;
    }

    public int getWeight() {
        return this.weight;
    }

    public int calculateVolume() {
        int cur_valume = 1;

        for(int i = 0; i < this.dimensions.length; ++i) {
            cur_valume *= this.dimensions[i];
        }

        return cur_valume;
    }

    public void setId(int andIncrement) {
        this.id = andIncrement;
    }
}
