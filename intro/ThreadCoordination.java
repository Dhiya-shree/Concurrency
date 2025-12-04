package intro;

public class ThreadCoordination {

    public static void main(String[] args) throws InterruptedException {
        // Thread Termination
        // Thread consume memeory + kernel resources , cpu cycles , cache memory
        // Thus important to clean after thread

        /*
         * Thread can be interrupted under following condt.
         * Thread executing method that throws interruption
         * Thread code handling interrupt signal
         */

        Thread th1 = new NewThreadTC();
        th1.interrupt();// un started thread can be interrupt
        // Thread.currentThread().interrupt();
        th1.start();// Interrupted thread can restart
        // But for started , interrupted will always be true

        th1.interrupt();// same thread can be interrupted

        Thread th2 = new LongThreadTC();
        // th2.interrupt();
        th2.start();
        System.out.println(" Thread sleep start ");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(" Thread Co ordination Main sleep error ");
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
        System.out.println(" Thread sleep end ");

        th2.interrupt();
        // Thread Interrupt
        // One thread got interrupted , interrupt is true
        // Sleep after interrupt cause interrupt exception
        // Other methods , thread can be interruoted again and again
        // interrupted threadcan restart

        // Daemon Threads
        // Backgroud task , does not block app from terminating
        // Some worker not controllable , not blacking app from terminating
        // Termitates with jvm
        Thread th3 = new LongThreadTC();
        th3.setDaemon(true);// terminated without interrupt
        th3.start();

        // Thread Join
        // Diff thread run independently ,with various order of execution
        // For thread depending on other use join
        Thread.currentThread().join(5000);

    }

    static class NewThreadTC extends Thread {

        @Override
        public void run() {
            System.out.println(" Run ");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(" Th1 already interrupted once , thus sleep causes interruption ");

                e.printStackTrace();
            }
            System.out.println(" Run Completed ");

        }

        @Override
        public void interrupt() {
            System.out.println(" Interrupt started " + Thread.currentThread().getName());
            super.interrupt();
            System.out.println(" Interrupt ompleted ");
        }
    }

    static class LongThreadTC extends Thread {

        @Override
        public void run() {
            System.out.println(" Run ");
            while (true) {

                if (Thread.interrupted()) {
                    System.out.println(" Interrupted ");
                    return;
                }
                System.out.print("-");
            }
        }

        @Override
        public void interrupt() {
            System.out.println(" Interrupt started Long" + Thread.currentThread().getName());
            super.interrupt();
            System.out.println(" Interrupt ompleted Long ");
        }
    }
}
