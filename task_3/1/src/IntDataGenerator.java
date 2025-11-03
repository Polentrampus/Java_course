import java.util.Random;

public class IntDataGenerator {
    private static final Random random = new Random();
    private final int value;

    public IntDataGenerator() {
        this.value = random.nextInt(100, 1000);
        System.out.println("Сгенерировано число: " + this.value);
    }

    //заменила цикл for
    public int getMaxDigit() {
        int num = this.value % 100;
        int max_num = -1;

        if (this.value/100 > max_num){
            max_num = this.value/100;
        }
        if(num/10 > max_num)
        {
            max_num = num/10;
        }
        if (num%10>max_num) {
            max_num = num % 10;
        }

        return max_num;
    }

    public int getValue() {
        return this.value;
    }
}