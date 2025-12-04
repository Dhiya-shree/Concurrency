package intro;

import java.util.List;
import java.util.Random;

public class Multithread_hacker_example {
    public static final int MAX_PASSWORD = 20;

    public static void main(String[] args) {
        Vault vault = new Vault(new Random().nextInt(MAX_PASSWORD));
        List<Thread> threads = List.of(new AscendingHackerThread(vault),
                new DescendingHackerThread(vault), new PoliceThread());
        for (Thread thread : threads) {
            thread.start();
        }

    }

    private static class Vault {
        private int password;

        public Vault(int password) {
            this.password = password;
        }

        public boolean isCorrectPassword(int value) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return this.password == value;
        }
    }

    private static abstract class HackerThread extends Thread {
        private Vault vault;

        public HackerThread(Vault vault) {
            this.vault = vault;
            this.setName(this.getClass().getSimpleName());
            this.setPriority(MAX_PRIORITY);
        }

        @Override
        public synchronized void start() {
            System.out.println("Starting thread " + this.getName());
            super.start();
        }
    }

    private static class AscendingHackerThread extends HackerThread {

        public AscendingHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int guess = 0; guess < MAX_PASSWORD; guess++) {
                System.out.println(" Value from asc ..... " + guess);
                if (super.vault.isCorrectPassword(guess)) {
                    System.out.println(this.getName() + " Guessed correctly " + guess);
                    System.exit(0);
                }
            }
        }

    }

    private static class DescendingHackerThread extends HackerThread {

        public DescendingHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int guess = MAX_PASSWORD; guess > 0; guess--) {
                System.out.println(" Value from desc ..... " + guess);
                if (super.vault.isCorrectPassword(guess)) {
                    System.out.println(this.getName() + " Guessed correctly " + guess);
                    System.exit(0);
                }
            }
        }

    }

    private static class PoliceThread extends Thread {
        @Override
        public void run() {
            for (int i = 10; i > 0; i--) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(" Current chase ........." + i);
            }
            System.out.println(" Over !!! ");
            System.exit(0);
        }
    }
}
