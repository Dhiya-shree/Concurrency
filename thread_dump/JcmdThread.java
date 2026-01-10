package thread_dump;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Find PID using JPS
 * jcmd <pid> Thread.print
 * 
 */
public class JcmdThread {
    public static void main(String[] args) {
        ExecutorService executors = Executors.newCachedThreadPool();
        ExecutorService executor2 = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            executors.submit(new Tasks());
            executor2.submit(new Tasks());
        }
        executors.shutdown();
        executor2.shutdown();

    }

    private static class Tasks implements Callable<String> {

        @Override
        public String call() throws Exception {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Finish " + Thread.currentThread();

        }

    }
}
