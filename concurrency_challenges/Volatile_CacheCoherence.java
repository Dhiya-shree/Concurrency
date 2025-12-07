package concurrency_challenges;

public class Volatile_CacheCoherence {
    private static int number; // Piggybacks by Happens before character of volatile of ready
    private static boolean ready;

    public static void main(String[] args) {
        Reader read = new Reader();
        read.start();
        number = 50;
        ready = true;
    }

    private static class Reader extends Thread {

        @Override
        public void run() {
            while (!ready) {
                Thread.yield();
            }

            System.out.println(number);
        }
    }
}