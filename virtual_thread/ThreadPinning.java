package virtual_thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPinning {
    private static final int NO_OF_THREAD = 10;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("""
                During sync , the critial object gets tied to the carrier thread.
                So carrier thread can not unmount blocking the virtual thread
                Temp sol: Reentrant
                """);
        virtualThreadsSync();
        virtualThreadReentrant();
    }

    private static void virtualThreadReentrant() throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NO_OF_THREAD; i++) {
            Thread virtualThread = Thread.ofVirtual().unstarted(new BlockingTaskReentrant());// Thread.ofPlatform().unstarted(runnable);
            threads.add(virtualThread);
        }
        for (int i = 0; i < NO_OF_THREAD; i++) {
            threads.get(i).start();
        }
        for (int i = 0; i < NO_OF_THREAD; i++) {
            threads.get(i).join();
        }
    }

    private static void virtualThreadsSync() throws InterruptedException {
        System.out.println("Maximum no of worker/carrier thread = no of core ");
        System.out.println(" Blocking task unmount and ount the active tread in carrier ");
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NO_OF_THREAD; i++) {
            Thread virtualThread = Thread.ofVirtual().unstarted(new BlockingTaskSync());// Thread.ofPlatform().unstarted(runnable);
            threads.add(virtualThread);
        }
        for (int i = 0; i < NO_OF_THREAD; i++) {
            threads.get(i).start();
        }
        for (int i = 0; i < NO_OF_THREAD; i++) {
            threads.get(i).join();
        }
    }

    private static class BlockingTaskSync implements Runnable {

        @Override
        public void run() {
            synchronized (this) {
                System.out.println(" inside " + Thread.currentThread() + " before blocking call ");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(" inside " + Thread.currentThread() + " after blocking call ");

            }
        }

    }

    private static class BlockingTaskReentrant implements Runnable {

        @Override
        public void run() {
            ReentrantLock lock = new ReentrantLock();
            lock.lock();
            System.out.println(" inside " + Thread.currentThread() + " before blocking call ");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(" inside " + Thread.currentThread() + " after blocking call ");
            lock.unlock();
        }

    }
}