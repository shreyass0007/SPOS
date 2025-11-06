import java.util.concurrent.Semaphore;

public class ReadersWritersProblem {
    static Semaphore readLock = new Semaphore(1);
    static Semaphore writeLock = new Semaphore(1);
    static int readers = 0;

    static class Reader implements Runnable {
        @Override
        public void run() {
            try {
                // Entry Section
                readLock.acquire();
                readers++;
                if (readers == 1) {
                    writeLock.acquire(); // first reader locks writers out
                }
                readLock.release();

                // Reading Section
                System.out.println(Thread.currentThread().getName() + " is READING");
                Thread.sleep(500); // simulate reading
                System.out.println(Thread.currentThread().getName() + " has FINISHED READING");

                // Exit Section
                readLock.acquire();
                readers--;
                if (readers == 0) {
                    writeLock.release(); // last reader lets writers in
                }
                readLock.release();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static class Writer implements Runnable {
        @Override
        public void run() {
            try {
                writeLock.acquire();
                System.out.println(Thread.currentThread().getName() + " is WRITING");
                Thread.sleep(1000); // simulate writing
                System.out.println(Thread.currentThread().getName() + " has FINISHED WRITING");
                writeLock.release();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Thread r1 = new Thread(new Reader(), "Reader-1");
        Thread r2 = new Thread(new Reader(), "Reader-2");
        Thread w1 = new Thread(new Writer(), "Writer-1");
        Thread r3 = new Thread(new Reader(), "Reader-3");

        r1.start();
        r2.start();
        w1.start();
        r3.start();
    }
}
