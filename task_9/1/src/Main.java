import java.util.concurrent.locks.ReentrantLock;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // ===========NEW==============
        Thread thread1 = new Thread();
        System.out.println("Создан поток: " + thread1.getState());
        // =========RUNNABLE===========
        thread1.start();
        System.out.println("Поток запущен: " + thread1.getState());
        thread1.join();
        // ======TIMED_WAITING=========
        // =========BLOCKED============
        Object lock_1 = new Object();
        Thread thread2 = new Thread(()->{
            synchronized (lock_1) {
                try {
                    System.out.println("Поток 2 получил блокировку");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread thread3 = new Thread(()->{
            System.out.println("Поток 3 пытается получить блокировку");
            synchronized (lock_1) {
                System.out.println("Поток 3 получил блокировку");
            }
        });

        thread2.start();
        Thread.sleep(100);

        thread3.start();
        Thread.sleep(100);

        System.out.println("Поток 2 в состоянии: " + thread2.getState());
        System.out.println("Поток 3 в состоянии: " + thread3.getState());

        thread2.join();
        thread3.join();
        // ======TIMED_WAITING=========
        // =========WAITING============
        // очередь, а не нативная блокировка
        ReentrantLock lock_2 = new ReentrantLock();
        Thread thread4 = new Thread(() -> {
            lock_2.lock();
            try {
                System.out.println("Поток 4 получил блокировку");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock_2.unlock();
                System.out.println("Поток 4 отпустил блокировку");
            }});
        Thread thread5 = new Thread(() -> {
            System.out.println("Поток 5 пытается захватить блокировку");
            lock_2.lock();
            try {
                System.out.println("Поток 5 получил блокировку");
            } finally {
                lock_2.unlock();
                System.out.println("Поток 5 отпустил блокировку");
            }});
        thread4.start();
        Thread.sleep(100);
        thread5.start();
        Thread.sleep(100); // даем время захватить

        System.out.println("Поток 4 в состоянии: " + thread4.getState());
        System.out.println("Поток 5 в состоянии: " + thread5.getState());

        thread4.join();
        thread5.join();

        // =======TERMINATED===========
        Thread thread6 = new Thread();
        thread6.start();
        Thread.sleep(1000);
        System.out.println("Поток 6 в состоянии: " + thread6.getState());
    }
}