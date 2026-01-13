//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static boolean threadOrder = true; // true - для потока 1
    private static final Object lock = new Object();
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    try {
                        while(!threadOrder) {
                            lock.wait(); // если поток 2 активен
                            // текущий поток обращается в состояние ожидания
                        }
                        System.out.println("Thread 1");
                        Thread.sleep(1000);
                        threadOrder = false; //передаем очередь потоку 2
                        lock.notifyAll();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        Thread thread2 = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    try {
                        //ждем пока не наша очередь
                        while(threadOrder) {
                            lock.wait();
                        }
                        System.out.println("Thread 2");
                        Thread.sleep(1000);
                        threadOrder = true;
                        lock.notifyAll();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}