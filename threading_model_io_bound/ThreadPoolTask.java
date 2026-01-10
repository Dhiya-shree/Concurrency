package threading_model_io_bound;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTask {

    private static final int TASKS = 10_000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println(" Running tasks ... " + TASKS);
        long start = System.currentTimeMillis();
        performTasks();
        // Thread.sleep(10000);
        System.out.println(" Time taken for task completion =======>" + (System.currentTimeMillis() - start));
    }

    private static void performTasks() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        try {
            for (int i = 0; i < TASKS; i++) {
                executorService.submit(() -> blockingIOTask());
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
        System.out.println(" Eexcuting a blocking task from " + Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(" Completed a blocking task from " + Thread.currentThread().getName());

    }

}
