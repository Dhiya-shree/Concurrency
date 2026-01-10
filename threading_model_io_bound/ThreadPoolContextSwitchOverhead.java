package threading_model_io_bound;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolContextSwitchOverhead {

    private static final int TASKS = 10_000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println(" Running tasks ... " + TASKS);
        System.out.println(" Instead of 10,000 IO operation of 1000 ms == 54sec time taken");
        System.out.println(
                " Compare with 10,000 IO operation with 100 operation *10 ms operation === time taken 1518secs ");
        long start = System.currentTimeMillis();
        performTasks();
        // Thread.sleep(10000);
        System.out.println(" Time taken for task completion =======>" + (System.currentTimeMillis() - start));
    }

    private static void performTasks() throws InterruptedException {

        ExecutorService executorService = Executors.newCachedThreadPool();
        // use new fixed thread pool
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
        System.out.println(" Eexcuting a blocking task from " + Thread.currentThread().getName());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(" Completed a blocking task from " + Thread.currentThread().getName());

    }

}
