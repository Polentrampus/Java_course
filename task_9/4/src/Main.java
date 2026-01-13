
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new SystemThread("Thread1", 1);
        thread.start();
        thread.join();
    }
}