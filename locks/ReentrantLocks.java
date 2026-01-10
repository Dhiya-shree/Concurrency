package locks;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLocks {
    public static void main(String[] args) throws InterruptedException {
        // Reentrant lock
        // Same thread can acquire same lock multiple times
        // If a thread already acquire a lock , it can reacquire
        // lockInterruptibly -> thread that was waiting to acquire lock can be
        // interrupted
        // tryLock -> returns immediately , no waiting
        // Has good testing methods
        // Fairness -> allow fair chance of acquiring lock , has a performance overhead
        Thread th1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + " has " + i + " start   ");
                // Once a thread is interrupted it is always interrupted
                // Interrupted threadcan lock again
                lockMethod();
            }
        }, "Thread1---");
        Thread th2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(" Current +++ " + i + " : " + Thread.currentThread().getName());

                lockMethod();
            }
        }, "Thread2+++");
        Thread th3 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(" Current === " + i + " : " + Thread.currentThread().getName());

                lockMethod();
            }
        }, "Thread3===");

        th1.start();
        th2.start();

        th3.start();
        Thread.sleep(2000);
        th1.interrupt();
        th2.interrupt();

    }

    private static ReentrantLock lock = new ReentrantLock();

    public static void lockMethod() {

        try {
            lock.lockInterruptibly();

            System.out.println(" Critical section 1 " + Thread.currentThread().getName());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
            try {

                lock.lockInterruptibly();
                System.out.println(" In the second lock " + Thread.currentThread().getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("===>Sleep Interrupted  " + Thread.currentThread().getName());
                    // e.printStackTrace();

                }
                lock.unlock();

            } catch (InterruptedException e) {
                System.out.println("===> thread lock interrupitbly 2" + Thread.currentThread().getName());

                // e.printStackTrace();
            }

            System.out.println(" No of threads in queue " + lock.getQueueLength());
            lock.unlock();
        } catch (InterruptedException e) {
            System.out.println("===> thread lock interrupitbly 1" + Thread.currentThread().getName());
        }
    }

}
