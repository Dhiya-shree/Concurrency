package virtual_thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VirtualThreadNoContextSwitchOverhead {

    private static final int TASKS = 10_000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println(" Running tasks ... " + TASKS);
        System.out.println(" Phyical thread - 10,000 IO operation of 1000 ms == 54sec time taken");
        System.out.println(
                " Phyical thread - 10,000 IO operation with 100 operation *10 ms operation === time taken 151secs (Reason context switch)");
        System.out.println("  Virtual Thread - 10,000 IO operation of 1000 ms == 37sec time taken");
        System.out.println(
                " Virtual thread - 10,000 IO operation with 100 operation *10 ms operation === time taken 40 secs ");

        long start = System.currentTimeMillis();
        performTasks();
        // Thread.sleep(10000);
        System.out.println(" Time taken for task completion =======>" + (System.currentTimeMillis() - start));
    }

    private static void performTasks() throws InterruptedException {

        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

        /*
         * Executors.newCachedThreadPool(new ThreadFactory() {
         * 
         * @Override
         * public Thread newThread(Runnable r) {
         * return new Thread();// Thread.ofVirtual().unstarted();
         * }
         * 
         * });
         * // Executors.newVirtualThreadPerTaskExecutor
         */
        try {
            for (int i = 0; i < TASKS; i++) {
                for (int j = 0; j < 100; j++) {
                    executorService.submit(() -> blockingIOTask());
                }
            }
            executorService.shutdown();

            boolean task = executorService.awaitTermination(TASKS, TimeUnit.SECONDS);
            if (!task) {
                System.out.println(" Not completed task ");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void blockingIOTask() {
        // System.out.println(" Eexcuting a blocking task from " +
        // Thread.currentThread());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // System.out.print(".");
        // System.out.println(" Completed a blocking task from " +
        // Thread.currentThread());

    }

}
