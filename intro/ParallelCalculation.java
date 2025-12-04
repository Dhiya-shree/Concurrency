package intro;

import java.math.BigInteger;

public class ParallelCalculation {
    public static void main(String[] args) {

        BigInteger result;
        Calculation th1 = (new Calculation(new BigInteger("1000"), new BigInteger("46")));
        Calculation th2 = (new Calculation(new BigInteger("1000"), new BigInteger("46")));
        th1.start();
        th2.start();

        try {
            th1.join();
            th2.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        result = th1.getResult().add(th2.getResult());
        System.out.println("Result : " + result.toString());
    }

    private static class Calculation extends Thread {
        private BigInteger result = BigInteger.ONE;
        private BigInteger base;
        private BigInteger power;

        public Calculation(BigInteger b, BigInteger p) {
            base = b;
            power = p;
        }

        @Override
        public void run() {
            for (BigInteger i = BigInteger.ONE; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                result = result.multiply(base);
            }
        }

        public BigInteger getResult() {
            return result;
        }

    }
}
