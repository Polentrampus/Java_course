package Characteristics;

public enum Color {
    RED("КРАСНЫЙ"),
    YELLOW("ЖЕЛТЫЙ"),
    BLACK("ЧЕРНЫЙ"),
    WHITE("БЕЛЫЙ"),
    BLU("ГОЛУБОЙ");

    private final String color;

    private Color(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }
}
