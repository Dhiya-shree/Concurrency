package concurrency_challenges;

import java.util.concurrent.ThreadLocalRandom;

public class Metrics_Capture_Example {

    public static void main(String[] args) {

        Metrics metrics = new Metrics();
        BusinessModule module1 = new BusinessModule(metrics);
        BusinessModule module2 = new BusinessModule(metrics);
        Printer printer = new Printer(metrics);
        printer.start();
        module1.start();
        module2.start();

    }

    private static class BusinessModule extends Thread {
        private Metrics metrics;
        private ThreadLocalRandom random = ThreadLocalRandom.current();

        BusinessModule(Metrics met) {
            metrics = met;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            try {
                long sec = random.nextInt(5) * 1000;
                System.out.println(Thread.currentThread().getName() + " adding " + sec);
                Thread.sleep(sec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            metrics.addInMetrics((System.currentTimeMillis() - start));
        }

    }

    private static class Printer extends Thread {
        private Metrics metrics;

        public Printer(Metrics met) {
            metrics = met;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Current average is " + metrics.getAverage());
            }
        }
    }

    private static class Metrics {
        private long count = 0;
        private volatile double average = 0.0;

        public synchronized void addInMetrics(long time) {
            double currentSum = average * count;
            count++;
            average = (currentSum + time) / count;
        }

        public double getAverage() {
            return average;
        }

    }

}