import java.util.ArrayList;
import java.util.Random;

public class Main {
    private static final Object lock = new Object();
    private static final int BUFFER_CAPACITY = 10;
    private static boolean running = true;
    private static final int WORK_TIME = 1000;

    public static void main(String[] args) throws InterruptedException {
        ArrayList<Integer> bufferPublic = new ArrayList<>();
        Random rand = new Random();

        Thread consumer = new Thread(() -> {
            synchronized (lock) {
                try {
                    while (running) {
                        while (bufferPublic.isEmpty()) {
                            lock.wait(); // Пока буфер пуст ожидаем его пополнения
                        }

                        if(!running) {
                            break;
                        }

                        int number = bufferPublic.remove(0);
                        System.out.println("Потребитель: забрал " + number +
                                " (размер буфера: " + bufferPublic.size() + ")");
                        lock.notifyAll(); // Пробуждаем все потоки
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }); //потребитель

        Thread producer = new Thread(()->{
            synchronized (lock) {
                try {
                    while (running) {
                        // если буфер меньше размером чем его вместимость,
                        // то продолжаем добавлять объекты
                        while(bufferPublic.size() >= BUFFER_CAPACITY && running) {
                            lock.wait();
                        }

                        if (!running) break;

                        int number = rand.nextInt(1, 101);
                        bufferPublic.add(number);
                        System.out.println("Производитель: добавил " + number +
                                " (размер буфера: " + bufferPublic.size() + ")");

                        lock.notifyAll();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }); //производитель

        producer.start();
        consumer.start();

        Thread.sleep(WORK_TIME);

        synchronized (lock) {
            running = false;
            lock.notifyAll();
        }

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Программа завершена");
    }
}