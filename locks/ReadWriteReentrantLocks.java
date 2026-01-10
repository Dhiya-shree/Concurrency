package locks;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteReentrantLocks {
    private static Integer HighestPrice = 1000;

    public static void main(String[] args) throws InterruptedException {
        InventoryDatabase inventoryDatabase = new InventoryDatabase();
        InventoryDatabaseReadWriteLock inventoryDatabaseReadWriteLock = new InventoryDatabaseReadWriteLock();
        Random rand = new Random();

        System.out.println(" Reentrant Lock : ");
        long time1 = execute(inventoryDatabase, rand);
        System.out.println(" Read Write Lock : ");
        long time2 = execute(inventoryDatabaseReadWriteLock, rand);
        System.out.println(" Reentrant locks time taken : " + time1);
        System.out.println(" Read Write Reentrant lock time taken : " + time2);
    }

    public static long execute(Inventory inventoryDatabase, Random rand) throws InterruptedException {

        for (int i = 0; i < 100000; i++) {
            inventoryDatabase.addItem(Math.abs(rand.nextInt(HighestPrice)));
        }

        Thread writer = new Thread(() -> {
            while (true) {
                inventoryDatabase.addItem(Math.abs(rand.nextInt(HighestPrice)));
                inventoryDatabase.removeItem(Math.abs(rand.nextInt(HighestPrice)));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException i) {
                    i.printStackTrace();
                }
            }
        });
        writer.setDaemon(true);
        writer.start();

        int numberOfReaderThread = 7;
        List<Thread> readers = new ArrayList<>();
        for (int i = 0; i < numberOfReaderThread; i++) {
            Thread reader = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    int upperBoundPrice = Math.abs(rand.nextInt(HighestPrice));
                    int lowerBoundPrice = upperBoundPrice > 0 ? Math.abs(rand.nextInt(upperBoundPrice)) : 0;
                    inventoryDatabase.getNoOfItemsInPriceRange(lowerBoundPrice, upperBoundPrice);
                }
            });
            reader.setDaemon(true);
            readers.add(reader);
        }
        long start = System.currentTimeMillis();
        System.out.println(" Starting time : " + start);
        for (Thread th : readers) {
            System.out.println(" Starting thread " + th.getName());

            th.start();
        }
        long start2 = System.currentTimeMillis();
        for (Thread th : readers) {
            System.out.println(" Joining thread " + th.getName() + " Time " + ((System.currentTimeMillis()) - start2));
            th.join();
        }
        long end = System.currentTimeMillis();
        long result = end - start;
        System.out.println(" Ending time : " + end);
        System.out.println("Time taken : " + (end - start));
        return result;
    }

    public static class InventoryDatabaseReadWriteLock implements Inventory {
        private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private Lock readLock = lock.readLock();
        private Lock writeLock = lock.writeLock();

        public int getNoOfItemsInPriceRange(int l, int h) {
            readLock.lock();
            try {
                Integer from = priceToCountMap.ceilingKey(l);
                Integer to = priceToCountMap.floorKey(h);
                if (from == null || to == null) {
                    return 0;
                }
                NavigableMap<Integer, Integer> rangeOfPrices = null;
                try {
                    rangeOfPrices = priceToCountMap.subMap(from, true, to, true);
                } catch (IllegalArgumentException i) {
                    System.out.println(" L " + l + " --> " + from);
                    System.out.println(" H " + h + " -> " + to);
                    i.printStackTrace();
                }
                int sum = 0;
                for (int s : rangeOfPrices.values()) {
                    sum += s;
                }
                // System.out.println(" Returning sum at " + System.currentTimeMillis());
                return sum;
            } finally {
                // System.out.println(" Unlocking get method at " + System.currentTimeMillis());
                readLock.unlock();
            }
        }

        public void addItem(int price) {
            writeLock.lock();
            try {
                priceToCountMap.putIfAbsent(price, 0);
                priceToCountMap.put(price, (priceToCountMap.get(price) + 1));
            } finally {
                writeLock.unlock();
            }
        }

        public void removeItem(int price) {
            writeLock.lock();
            try {
                priceToCountMap.computeIfPresent(price, (k, v) -> {
                    if (v <= 1) {
                        priceToCountMap.remove(k);
                    } else {
                        priceToCountMap.put(k, v - 1);
                    }
                    return v;
                });
            } finally {
                writeLock.unlock();
            }
        }
    }

    public static class InventoryDatabase implements Inventory {
        private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        private ReentrantLock lock = new ReentrantLock();

        public int getNoOfItemsInPriceRange(int l, int h) {
            lock.lock();
            try {
                Integer from = priceToCountMap.ceilingKey(l);
                Integer to = priceToCountMap.floorKey(h);
                if (from == null || to == null) {
                    return 0;
                }
                NavigableMap<Integer, Integer> rangeOfPrices = null;
                try {
                    rangeOfPrices = priceToCountMap.subMap(from, true, to, true);
                } catch (IllegalArgumentException i) {
                    System.out.println(" L " + l + " --> " + from);
                    System.out.println(" H " + h + " -> " + to);
                    i.printStackTrace();
                }
                int sum = 0;
                for (int s : rangeOfPrices.values()) {
                    sum += s;
                }
                // System.out.println(" Returning sum at " + System.currentTimeMillis());
                return sum;
            } finally {
                // System.out.println(" Unlocking get method at " + System.currentTimeMillis());
                lock.unlock();
            }
        }

        public void addItem(int price) {
            lock.lock();
            try {
                priceToCountMap.putIfAbsent(price, 0);
                priceToCountMap.put(price, (priceToCountMap.get(price) + 1));
            } finally {
                lock.unlock();
            }
        }

        public void removeItem(int price) {
            lock.lock();
            try {
                priceToCountMap.computeIfPresent(price, (k, v) -> {
                    if (v <= 1) {
                        priceToCountMap.remove(k);
                    } else {
                        priceToCountMap.put(k, v - 1);
                    }
                    return v;
                });
            } finally {
                lock.unlock();
            }
        }
    }

    public static interface Inventory {

        void addItem(int p);

        void removeItem(int p);

        int getNoOfItemsInPriceRange(int l, int p);

    }
}
