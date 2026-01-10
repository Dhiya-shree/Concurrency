package lockfree_algo;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class CompareAndSet {
    static Random rand = new Random();

    public static void main(String[] args) {
        Student student = new Student();
        AtomicReference<Student> atomicReference = new AtomicReference<>(student);

        ChangeM1 changem1 = new ChangeM1();
        changem1.getValue(atomicReference);
        ChangeM1 changem12 = new ChangeM1();
        changem1.getValue(atomicReference);
        ChangeM2 changem2 = new ChangeM2();
        changem2.getValue(atomicReference);
        ChangeM2 changem22 = new ChangeM2();
        changem2.getValue(atomicReference);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(changem2);
        executor.submit(changem1);
        executor.submit(changem22);
        executor.submit(changem12);
    }

    private static class ChangeM1 implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                Student studentm1 = student.get();
                Student copy = student.get();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                studentm1.m1 = rand.nextInt(100);
                System.out.println(student.get().m1 + "..." + copy.m1 + " " + copy.m2 + "..." + student.get().m2);
                if (student.compareAndSet(copy, studentm1)) {
                    System.out.println(" Student M1 marks updated !!!!");
                } else {
                    System.out.println(" Mark not updated " + i);
                }
            }
        }

        AtomicReference<Student> student;

        void getValue(AtomicReference<Student> student) {
            this.student = student;
        }

    }

    private static class ChangeM2 implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                Student studentm2 = student.get();
                Student copy = student.get();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                studentm2.m2 = rand.nextInt(100);
                ;
                System.out.println(student.get().m1 + "..." + copy.m1 + " " + copy.m2 + "..." + student.get().m2);
                if (student.compareAndSet(copy, studentm2)) {
                    System.out.println(" Student M2 marks updated !!!!");
                    // System.out.println(s);
                } else {
                    System.out.println(" Mark2 not updated " + i);
                }
            }
        }

        AtomicReference<Student> student;

        void getValue(AtomicReference<Student> student) {
            this.student = student;
        }

    }

    private static class Student {
        int m1;
        int m2;

        Student() {

        }
    }
}
