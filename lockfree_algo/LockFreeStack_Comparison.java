package lockfree_algo;

import java.lang.Thread.State;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class LockFreeStack_Comparison {
    private static Random rand = new Random();

    public static void main(String[] args) throws InterruptedException {

        StandardStack<Integer> stack = new StandardStack<>();
        LockFreeStack<Integer> lockFreeStack = new LockFreeStack<>();
        ConcurrentLinkedQueue<Integer> concurrentQueue = new ConcurrentLinkedQueue<>();

        /// Add data in stack
        for (int i = 0; i < 100000; i++) {
            int r = rand.nextInt();
            stack.push(r);
            lockFreeStack.push(r);
            concurrentQueue.add(r);
        }
        long standard = StandardStackComputation(stack);
        long lockfree = LockFreeStackComputation(lockFreeStack);
        long concu = ConcurrentQueueComputation(concurrentQueue);
        System.out.println("Standard time " + standard);
        System.out.println(" Lock free stack time " + lockfree);
        System.out.println(" Concurrent non blocking queue " + concu);
    }

    private static long ConcurrentQueueComputation(ConcurrentLinkedQueue<Integer> stack) {
        long start = System.currentTimeMillis();
        System.out.println("Concurrent stack time start ....");
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                // System.out.println(" Add T1 " + i);
                stack.add(rand.nextInt());
            }
            System.out.println(" Thread1 add completed at " + (System.currentTimeMillis() - start));
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                // System.out.println(" Add T2 " + i);

                stack.add(rand.nextInt());
            }
            System.out.println(" Thread2 add completed at " + (System.currentTimeMillis() - start));

        });
        Thread t3 = new Thread(() -> {
            while (true) {
                if (stack.poll() == null) {
                    break;
                }
            }
            System.out.println(" Thread3 remove completed " + (System.currentTimeMillis() - start));

        });
        Thread t4 = new Thread(() -> {
            while (true) {
                if (stack.poll() == null) {
                    break;
                }
            }
            System.out.println(" Thread4 remove completed " + (System.currentTimeMillis() - start));

        });
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        long end1;
        while (true) {
            if (t1.getState() == State.TERMINATED && t2.getState() == State.TERMINATED
                    && t3.getState() == State.TERMINATED && t4.getState() == State.TERMINATED) {
                break;
            }
        }
        end1 = System.currentTimeMillis();
        System.out.println(" Concurrent syncronised time " + (end1 - start));
        return end1 - start;

    }

    private static long StandardStackComputation(StandardStack<Integer> stack) {
        long start = System.currentTimeMillis();
        System.out.println("Standard stack time start ....");
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                // System.out.println(" Add T1 " + i);

                stack.push(rand.nextInt());
            }
            System.out.println(" Thread1 add completed at " + (System.currentTimeMillis() - start));

        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                // System.out.println(" Add T2 " + i);

                stack.push(rand.nextInt());
            }
            System.out.println(" Thread2 add completed at " + (System.currentTimeMillis() - start));

        });
        Thread t3 = new Thread(() -> {
            while (stack.getCounter() > 0) {
                // System.out.println(" Remove T3");

                stack.pop();
            }
            System.out.println(" Thread3 remove completed " + (System.currentTimeMillis() - start));

        });
        Thread t4 = new Thread(() -> {
            while (stack.getCounter() > 0) {
                // System.out.println(" Remove T4");

                stack.pop();
            }
            System.out.println(" Thread 4 remove completed " + (System.currentTimeMillis() - start));

        });
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        long end1;
        while (true) {
            if (t1.getState() == State.TERMINATED && t2.getState() == State.TERMINATED
                    && t3.getState() == State.TERMINATED && t4.getState() == State.TERMINATED) {
                break;
            }
        }
        end1 = System.currentTimeMillis();
        System.out.println(" Stack syncronised time " + (end1 - start));
        return end1 - start;

    }

    private static long LockFreeStackComputation(LockFreeStack<Integer> stack) {
        System.out.println("Lock free stack time start ....");
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                // System.out.println(" Add T1 " + i);

                stack.push(rand.nextInt());
            }
            System.out.println(" Thread1 add completed at " + (System.currentTimeMillis() - start));

        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                // System.out.println(" Add T2 " + i);

                stack.push(rand.nextInt());
            }
            System.out.println(" Thread2 add completed at " + (System.currentTimeMillis() - start));

        });
        Thread t3 = new Thread(() -> {
            while (stack.getCounter() > 0) {
                // System.out.println(" Remove T3");

                stack.pop();
            }
            System.out.println(" Thread3 remove completed " + (System.currentTimeMillis() - start));

        });
        Thread t4 = new Thread(() -> {
            while (stack.getCounter() > 0) {
                // System.out.println(" Remove T4");

                stack.pop();
            }
            System.out.println(" Thread4 remove completed " + (System.currentTimeMillis() - start));

        });
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        long end1;
        while (true) {
            if (t1.getState() == State.TERMINATED && t2.getState() == State.TERMINATED
                    && t3.getState() == State.TERMINATED && t4.getState() == State.TERMINATED) {
                break;
            }
        }
        end1 = System.currentTimeMillis();

        System.out.println(" Stack Lock free  time " + (end1 - start));
        return end1 - start;

    }

    public static class LockFreeStack<T> {
        private AtomicReference<StackNode<T>> head = new AtomicReference<>();
        private AtomicInteger counter = new AtomicInteger(0);

        public void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            while (true) {
                StackNode<T> currentHead = head.get();
                newHead.next = currentHead;
                if (head.compareAndSet(currentHead, newHead)) {
                    // System.out.println(" Item added ");
                    break;
                } else {
                    LockSupport.parkNanos(1);
                }
            }
            counter.incrementAndGet();
        }

        public T pop() {
            StackNode<T> newHead;
            StackNode<T> currentHead = head.get();
            T value = null;
            while (currentHead != null) {
                value = currentHead.value;
                newHead = currentHead.next;
                if (head.compareAndSet(currentHead, newHead)) {
                    // System.out.println(" Item removed ");
                    break;
                } else {
                    LockSupport.parkNanos(1);
                    currentHead = head.get();
                }
            }
            counter.decrementAndGet();
            return value;
        }

        public int getCounter() {
            return counter.get();
        }
    }

    public static class StandardStack<T> {
        private StackNode<T> head;
        private int counter = 0;

        StandardStack() {
            head = null;
        }

        public synchronized void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            newHead.next = head;
            head = newHead;
            counter++;
        }

        public synchronized T pop() {
            if (head == null) {
                counter--;
                return null;
            }
            T value = head.value;
            head = head.next;
            counter--;
            return value;
        }

        public int getCounter() {
            return counter;
        }
    }

    public static class StackNode<T> {
        public T value;
        public StackNode<T> next;

        public StackNode(T value) {
            this.value = value;
            next = null;
        }
    }
}
