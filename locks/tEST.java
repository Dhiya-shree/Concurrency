package locks;

public class tEST {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(
                    "Thread ");
        });
        System.out.println(t1.getState());
        t1.start();
        Thread.sleep(1000);
        System.out.println(" Main ");
    }
}
