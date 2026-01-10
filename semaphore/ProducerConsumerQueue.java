package semaphore;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerQueue {
    static int CAPACITY = 5;
    static Semaphore full = new Semaphore(0);
    static Semaphore empty = new Semaphore(CAPACITY);
    static Queue<Item> queue = new ArrayDeque<>();
    static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        // Consumer try to acquire full with 0 permit and stops
        // Producer acquire empty with 1 permit successfully
        // producer release un-acquired Full --->
        // Consumer finally gets the full permit
        // Consumer release empty permit
        // Producer again gets the empty permit .........
        Producer producer = new Producer();
        List<Consumer> consumers = new ArrayList<>();
        int consumerCount = 5;
        for (int i = 0; i < consumerCount; i++) {
            Consumer consumer = new Consumer();
            consumer.start();
            consumers.add(consumer);
        }
        producer.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        producer.interrupt();
        while (!queue.isEmpty()) {
            System.out.println(" Waitng for consumer to finish .....");
        }
        for (int i = 0; i < consumerCount; i++) {
            consumers.get(i).interrupt();
        }
    }

    public static class Producer extends Thread {
        Random rand = new Random();

        @Override
        public void run() {
            while (true) {
                try {
                    empty.acquire();
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    break;
                }
                lock.lock();
                int data = rand.nextInt(0, 1000);
                queue.offer(new Item(data));
                System.out.println("Producer set data " + data);
                lock.unlock();
                full.release();
            }
        }

    }

    public static class Consumer extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    full.acquire();
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    break;
                }
                lock.lock();
                int d = queue.poll().getData();
                System.out.println("Consumer get data " + d + " --> " + Thread.currentThread().getName());
                lock.unlock();
                empty.release();
            }
        }

    }

    public static class Item {
        int data;

        public Item(int nextInt) {
            data = nextInt;
        }

        void setData(int d) {
            data = d;
        }

        int getData() {
            return data;
        }

    }
}
