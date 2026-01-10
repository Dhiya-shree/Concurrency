package threading_model_io_bound;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadperTaskModel {
    private static final int TASKS = 1000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println(" Running tasks ... " + TASKS);
        long start = System.currentTimeMillis();
        boolean val = performTasks();

        // Thread.sleep(10000);
        System.out.println(val + " Time taken for task completion =======>" + (System.currentTimeMillis() - start));
    }

    private static boolean performTasks() throws InterruptedException {

        AtomicInteger counter = new AtomicInteger(0);
        List<Thread> lit = new ArrayList<>();
        for (int i = 0; i < TASKS; i++) {
            Thread t1 = new Thread(() -> blockingIOTask(counter));
            lit.add(t1);
        }
        for (int i = 0; i < TASKS; i++) {
            lit.get(i).start();
        }
        for (int i = 0; i < TASKS; i++) {
            lit.get(i).join();
        }

        while (counter.get() < TASKS) {
            // System.out.println(" Eex no shut down ");
        }
        return true;
    }

    private static void blockingIOTask(AtomicInteger counter) {
        System.out.println(" Eexcuting a blocking task from " + Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
            counter.incrementAndGet();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(" Completed  a blocking task from " + Thread.currentThread().getName());

    }

}
