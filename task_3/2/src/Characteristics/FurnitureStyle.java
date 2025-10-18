package Characteristics;

public enum FurnitureStyle {
    CLASSIC("Classic"),
    LOFT("Loft"),
    MODERN("Modern");

    private final String style;

    private FurnitureStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return this.style;
    }
}