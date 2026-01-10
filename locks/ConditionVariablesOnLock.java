package locks;

import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Semaphore also condt variable with only condt to check permits 
// Adding custom condition to locks 
// Any objects can be used as lock 
//  locks can have multiple conditions 
//  Same condt causing await at 
//     multiple places -> one signal NOT enough => Use signal All
// Multiple condt on same lock possible 
// Each  condt needs to be signalled for it to continue 
// If signal was never made and the lock is unlocked by all the threads 
//   condition.await waits forever 
public class ConditionVariablesOnLock {
    static Lock lock = new ReentrantLock();
    static Condition condition1 = lock.newCondition();
    static Condition condition2 = lock.newCondition();
    static String userName = null;
    static String password = null;

    public static void main(String[] args) throws InterruptedException {
        System.out.println(
                "C1 " + condition1
                        + "C2 " + condition2);
        UIThread UI = new UIThread();
        Thread usernameVerify = new Thread(new AuthenticationUserNameVerify());
        Thread passwordVerify = new Thread(new AuthenticationPasswordVerify());
        usernameVerify.start();
        passwordVerify.start();
        Thread.sleep(2000);
        UI.start();

    }

    public static class UIThread extends Thread {
        @Override
        public void run() {
            lock.lock();
            System.out.println(" Lock acquired by UIT");

            try (Scanner sc = new Scanner(System.in)) {
                userName = sc.next();
                password = sc.next();
                condition1.signal();
                try {
                    System.out.println(" Condition 1 signalled ");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {

                }
                // condition2.signal();
                try {
                    System.out.println(" Condition 2 signalled ");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {

                }
            } finally {
                System.out.println(" Lock unlocked ");
                lock.unlock();
                // condition2.signal();
            }

        }
    }

    public static class AuthenticationUserNameVerify implements Runnable {
        @Override
        public void run() {
            lock.lock();
            System.out.println(" Lock acquired by UNV");
            try {
                while (userName == null) {
                    try {
                        System.out.println(" Await called for condt1");
                        condition1.await();
                        System.out.println(" Condt 1 started " + System.currentTimeMillis());
                        // condition2.signal();
                        System.out.println("Condt 2 signalled ");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                lock.unlock();
                System.out.println(" Lock unlocked UN ");

            }
            if (userName.length() > 5) {
                System.out.println(" Username successfully authenticated ");
            } else {
                throw new RuntimeException(" Wrong username length ");
            }
        }
    }

    public static class AuthenticationPasswordVerify implements Runnable {
        @Override
        public void run() {
            lock.lock();
            System.out.println(" Lock acquired by PWV");

            try {
                while (password == null) {
                    try {
                        System.out.println(" Await called for condt2");
                        condition1.await();
                        System.out.println(" Condt 2 started " + System.currentTimeMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                lock.unlock();
                System.out.println(" Lock unlocked PWD");

            }
            if (password.length() > 5) {
                System.out.println(" Password successfully authenticated ");
            } else {
                throw new RuntimeException(" Wrong password length ");
            }
        }
    }
}
