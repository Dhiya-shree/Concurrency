package semaphore;

import java.util.Random;
import java.util.concurrent.Semaphore;

// Semaphore is not lock 
// It has permit and any threadcan acquire it 
// Any thread can releasea permit without acquiringmaking it a bug 
// Binary Semaphore is not reentrant 
// Semaphore is best for producer consumer 
public class SingleProducerConsumer {
    static Semaphore full = new Semaphore(0);
    static Semaphore empty = new Semaphore(1);
    static Item item = null;

    public static void main(String[] args) throws InterruptedException {
        // Consumer try to acquire full with 0 permit and stops
        // Producer acquire empty with 1 permit successfully
        // producer release un-acquired Full --->
        // Consumer finally gets the full permit
        // Consumer release empty permit
        // Producer again gets the empty permit .........
        item = new Item();
        Producer producer = new Producer();
        Thread consumer = new Thread(new Consumer());
        consumer.start();
        producer.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        producer.triggerSleep(100);
        producer.interrupt();
        Thread.sleep(100);
        consumer.interrupt();

    }

    public static class Producer extends Thread {
        Random rand = new Random();

        @Override
        public void run() {
            while (true) {
                try {
                    empty.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                item.setData(rand.nextInt(0, 1000));
                System.out.println(
                        "Producer set data " + item.getData());
                full.release();
            }
        }

        public void triggerSleep(int i) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static class Consumer implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    full.acquire();
                    // Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                int d = item.getData();
                System.out.println("Consumer get data " + d);
                empty.release();
            }
        }

    }

    public static class Item {
        int data;

        void setData(int d) {
            data = d;
        }

        int getData() {
            return data;
        }

    }
}
