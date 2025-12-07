package concurrency_challenges;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncronizationLock {
    // Implements Reentrant lock
    // Same thread can acquire both inc and dec method

    public static void main(String[] args) throws InterruptedException {

        IncrementData data = new IncrementData();
        data.setData(10);
        System.out.println(" Data set at : " + data.getData());
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Long start = System.currentTimeMillis();
        executorService.submit(() -> {
            System.out.println(" Decrement started ");
            for (int i = 0; i < 10000; i++) {
                data.decrement();
            }
            System.out.println("Decrement completed!!!");
        });
        executorService.submit(() -> {
            System.out.println(" Increment started ....");
            for (int i = 0; i < 10000; i++) {
                data.increment();
            }
            System.out.println("Increment completed!!!");

        });
        System.out.println("Data is " + data.getData() + " Time taken : " + (System.currentTimeMillis() - start));
        executorService.shutdown();

    }

    public static class IncrementData {
        private Integer data;

        public Integer getData() {
            return data;
        }

        public synchronized void increment() {
            data++;
        }

        public synchronized void decrement() {
            data--;
        }

        public void setData(Integer data) {
            this.data = data;
        }

    }
}
