package Characteristics;

public enum FurnitureMaterial {
    METALL("МЕТАЛЛ"),
    ALUMINY("АЛЮМИНИЙ"),
    WOOD("ДЕРЕВО"),
    GLASS("СТЕКЛО");

    private final String material;

    private FurnitureMaterial(String material) {
        this.material = material;
    }

    public String getMaterial() {
        return this.material;
    }
}
