public class SystemThread extends Thread {
    private final long sec;
    public SystemThread(String name, long sec) {
        super(name);
        this.sec = sec * 1000;
    }
    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("Системное время:" + System.currentTimeMillis());
                sleep(sec);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public long getSec() {
        return sec;
    }
}
