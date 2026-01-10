package virtual_thread;

import java.util.ArrayList;
import java.util.List;

public class VirtualThreadmpl {
    static private Runnable printName = () -> {
        System.out.println(" Inside thread " + Thread.currentThread());
    };
    private static final int NO_OF_THREAD = 5;

    public static void main(String[] args) throws InterruptedException {
        platformThread();
        virtualThread();
        virtualThreads();
        platformThreads();

    }

    private static void platformThreads() throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NO_OF_THREAD; i++) {
            Thread platformThread = new Thread(new BlockingTask());// Thread.ofPlatform().unstarted(runnable);
            threads.add(platformThread);
        }
        for (int i = 0; i < NO_OF_THREAD; i++) {
            threads.get(i).start();
        }
        for (int i = 0; i < NO_OF_THREAD; i++) {
            threads.get(i).join();
        }
    }

    private static void virtualThreads() throws InterruptedException {
        System.out.println("Maximum no of worker/carrier thread = no of core ");
        System.out.println(" Blocking task unmount and ount the active tread in carrier ");
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NO_OF_THREAD; i++) {
            Thread virtualThread = Thread.ofVirtual().unstarted(new BlockingTask());
            threads.add(virtualThread);
        }
        for (int i = 0; i < NO_OF_THREAD; i++) {
            threads.get(i).start();
        }
        for (int i = 0; i < NO_OF_THREAD; i++) {
            threads.get(i).join();
        }
    }

    private static void platformThread() throws InterruptedException {
        Thread platformThread = Thread.ofPlatform().unstarted(printName);
        platformThread.start();
        platformThread.join();
    }

    private static void virtualThread() throws InterruptedException {
        Thread virtualThread = Thread.ofVirtual().unstarted(printName);// Thread.ofVirtual().unstarted(runnable);
        virtualThread.start();
        virtualThread.join();
    }

    private static class BlockingTask implements Runnable {

        @Override
        public void run() {
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