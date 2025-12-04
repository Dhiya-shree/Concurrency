package intro;

class ThreadBaiscs {
    public static void main(String[] args) {
        // Thread creation - passing a runnable method
        Thread th1 = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("Running ....." + Thread.currentThread().getName() + " Priority "
                        + Thread.currentThread().getPriority());
                throw new RuntimeException(" New exp ");
            }
        });
        th1.setName("NewThread");
        th1.setPriority(10);
        th1.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(" Error ...." + e.getMessage());
            }

        });
        System.out.println(Thread.currentThread().getName());
        th1.start();
        System.out.println(Thread.currentThread().getName());

        // Thread Creation , extend Thread.. extends Runnable
        // Access all sing this insteadof static Thread methods
        Thread th2 = new NewThreadTB();
        th2.start();

    }
}

class NewThreadTB extends Thread {
    @Override
    public void run() {
        System.out.println("Running ....." + this.getName() + " Priority "
                + this.getPriority());
    }
}