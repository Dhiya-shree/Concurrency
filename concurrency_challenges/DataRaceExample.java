package concurrency_challenges;

public class DataRaceExample {

    /*
     * 
     * Race condt -> concurrent executions on non atomic shared operations
     * Data race -> out of order executions when multiple thread access same
     * operations
     * 
     * Data race avaoided by :
     * Synchronization
     * Volatile keyword
     * Other happens - before mechanism
     * 
     */
    public static void main(String[] args) {
        SharedClass sc = new SharedClass();
        Thread th1 = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                sc.increment();
            }
        });

        Thread th2 = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                sc.checkNow();
            }
        });
        th1.start();
        th2.start();
    }

    private static class SharedClass {
        private int x;
        private int y;

        public void increment() {
            x++;
            y++;
        }

        public void checkNow() {
            if (y > x) {
                System.out.println(" Data race condition !!!! ");
            }
        }
    }
}