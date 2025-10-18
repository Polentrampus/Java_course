import java.util.Random;

public class IntDataGenerator {
    private static final Random random = new Random();
    private final int value;

    public IntDataGenerator() {
        this.value = random.nextInt(100, 1000);
        System.out.println("Сгенерировано число: " + this.value);
    }

    public int maxNumber() {
        int num = -1;

        for(int i = 0; i < 3; ++i) {
            if (num < this.value / 100) {
                num = this.value / 100;
            } else if (num < this.value / 10 - this.value / 100 * 10) {
                num = this.value / 10 - this.value / 100 * 10;
            } else if (num < this.value - this.value / 10 * 10) {
                num = this.value - this.value / 10 * 10;
            }
        }

        return num;
    }

    public Integer getValue() {
        return this.value;
    }
}