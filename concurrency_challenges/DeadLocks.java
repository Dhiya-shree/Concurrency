package concurrency_challenges;

import java.util.Random;

public class DeadLocks {
    public static void main(String[] args) {

        Intersection intersection = new Intersection();
        Thread t1 = new Thread(new TrainA(intersection), "Train A");
        Thread t2 = new Thread(new TrainB(intersection), "Train B");
        t1.start();
        t2.start();
    }

    private static class TrainA implements Runnable {
        private Intersection intersection;
        private Random random = new Random();

        TrainA(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleep = random.nextInt(50);
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                intersection.takeRoadA();
            }
        }

    }

    private static class TrainB implements Runnable {
        private Intersection intersection;
        private Random random = new Random();

        TrainB(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleep = random.nextInt(50);
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                intersection.takeRoadB();
            }
        }

    }

    private static class Intersection {
        private Object roadA = new Object();
        private Object roadB = new Object();

        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("A: Road A is blocked by thread " + Thread.currentThread().getName());
                synchronized (roadB) {
                    System.out.println("A: Road B is blocked by thread " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void takeRoadB() {
            synchronized (roadB) {
                System.out.println("B: Road B is blocked by thread " + Thread.currentThread().getName());
                synchronized (roadA) {
                    System.out.println("B: Road A is blocked by thread " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
