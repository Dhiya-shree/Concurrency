package locks;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.StringJoiner;

// All objects have wait notify notifyAll 
// Condition Variable Vs     Object 
// No need to sync           Need to sync object to use 
// lock--unlock              Sync / lock unlock on object 
// await                     wait 
// notify                    signal 
// notifyAll                 SignalAll
// multi condt var           Single wait set
// Shared across process     Inter Thread Synchronization 
//if placed in shared memory   within single process
//  works with any mutex      Object lock + signalling 
// signal changes in obj's   Broader mech within a 
//     logical state          object monitor 
public class ObjectSignalingOnLock {
    private static Random random = new Random();
    private static final String OUTPUT_FILE = "D:\\Repos\\Concurrency\\Concurrency\\resources\\MatrixMultiplication\\matrices.txt";
    private static final int N = 10;
    private static final int NO_OF_Matric_pairs = 100000;
    private static final String RESULT_File = "D:\\Repos\\Concurrency\\Concurrency\\resources\\MatrixMultiplication\\matrices_result.txt";

    public static void main(String[] args) throws IOException {
        // createMatrixes();
        ThreadSafeQueue queue = new ThreadSafeQueue();
        File matrixOP = new File(OUTPUT_FILE);
        File matriResult = new File(RESULT_File);

        MatrixReaderProducer reader = new MatrixReaderProducer(new FileReader(matrixOP), queue);
        MatrixMultiplierConsumer consumer = new MatrixMultiplierConsumer(new FileWriter(matriResult), queue);
        reader.start();
        consumer.start();
    }

    private static class MatrixMultiplierConsumer extends Thread {
        ThreadSafeQueue queue;
        BufferedWriter writer;

        public MatrixMultiplierConsumer(FileWriter fileWriter, ThreadSafeQueue queue) {
            this.queue = queue;
            writer = new BufferedWriter(fileWriter);
        }

        @Override
        public void run() {
            while (true) {
                MatrixesPair matrixesPair = queue.remove();
                if (matrixesPair == null) {
                    System.out.println(" No more matrix to read !!!");
                    break;
                }

                float[][] result = multiplyMatrix(matrixesPair.matrix1, matrixesPair.matrix2);
                saveMatrixToFile(writer, result);

            }
            try {
                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void saveMatrixToFile(BufferedWriter writer, float[][] matrix) {
            for (int r = 0; r < N; r++) {
                StringJoiner joiner = new StringJoiner(",");
                for (int c = 0; c < N; c++) {
                    joiner.add(String.format("%.2f", matrix[r][c]));
                }
                try {
                    writer.write(joiner.toString());
                    writer.write('\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            try {
                writer.write('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private float[][] multiplyMatrix(float[][] matrix1, float[][] matrix2) {
            float[][] result = new float[N][N];
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    for (int k = 0; k < N; k++) {
                        result[r][c] = result[r][c] + matrix1[r][k] * matrix2[k][c];
                    }
                }
            }
            return result;
        }

    }

    private static class MatrixReaderProducer extends Thread {
        private ThreadSafeQueue threadSafeQueue;
        private BufferedReader reader;

        public MatrixReaderProducer(FileReader fileReader, ThreadSafeQueue queue) {
            reader = new BufferedReader(fileReader);
            threadSafeQueue = queue;
        }

        @Override
        public void run() {
            while (true) {
                float[][] matri1 = readMatrix();
                float[][] matrix2 = readMatrix();
                if (matri1 == null || matrix2 == null) {
                    threadSafeQueue.terminate();
                    System.out.println(" All matrixes read!!!");
                    return;
                }
                MatrixesPair matrixesPair = new MatrixesPair();
                matrixesPair.matrix1 = matri1;
                matrixesPair.matrix2 = matrix2;
                threadSafeQueue.add(matrixesPair);
            }
        }

        private float[][] readMatrix() {
            float[][] matrix = new float[N][N];
            for (int r = 0; r < N; r++) {
                try {
                    String line = reader.readLine();
                    if (line == null || line.isEmpty()) {
                        System.out.println("line empty ");
                        return null;
                    }
                    String val[] = line.split(",");
                    for (int c = 0; c < N; c++) {
                        matrix[r][c] = Float.valueOf(val[c]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    matrix = null;
                }
            }
            try {
                reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(Arrays.toString(matrix[0]));
            return matrix;

        }
    }

    private static class ThreadSafeQueue {
        private Queue<MatrixesPair> queue = null;
        private boolean isEmpty = true;
        private boolean isTerminate = false;
        static final int capacity = 5;

        ThreadSafeQueue() {
            queue = new LinkedList<>();
            System.out.println(" Queue Created .....");
        }

        public synchronized void add(MatrixesPair matrixesPair) {
            while (queue.size() == capacity) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.add(matrixesPair);
            isEmpty = false;
            notify();
        }

        public synchronized MatrixesPair remove() {
            MatrixesPair matrixesPair = null;
            while (isEmpty && !isTerminate) {
                try {
                    wait(90000);
                } catch (InterruptedException i) {
                    i.printStackTrace();
                }
            }

            if (queue.size() == 1) {
                isEmpty = true;
            }
            if (queue.size() == 0 && isTerminate) {
                return null;
            }

            System.out.println(" Queue size " + queue.size());

            matrixesPair = queue.remove();
            if (queue.size() == capacity - 1) {
                notifyAll();
            }
            return matrixesPair;
        }

        public synchronized void terminate() {
            isTerminate = true;
            notifyAll();
        }
    }

    private static class MatrixesPair {
        public float[][] matrix1;
        public float[][] matrix2;
    }

    @SuppressWarnings("unused")
    private static void createMatrixes() throws IOException {
        File file = new File(OUTPUT_FILE);
        try (BufferedOutputStream fileWriter = new BufferedOutputStream(new FileOutputStream(file))) {
            createMatrix(fileWriter);
            fileWriter.flush();
            fileWriter.close();
        }
    }

    private static void createMatrix(BufferedOutputStream bos) throws IOException {
        for (int i = 0; i < NO_OF_Matric_pairs; i++) {
            float[][] matrix = new float[N][N];
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    matrix[j][k] = random.nextFloat() * random.nextInt(100);
                }
            }
            saeMatrixToFile(bos, matrix);
        }
    }

    static byte[] newLine = System.lineSeparator().getBytes();

    private static void saeMatrixToFile(BufferedOutputStream bos, float[][] matrix) throws IOException {
        for (int i = 0; i < N; i++) {
            StringJoiner joiner = new StringJoiner(",");
            for (int j = 0; j < N; j++) {
                joiner.add(String.format("%.2f", matrix[i][j]));
            }
            bos.write(joiner.toString().getBytes());
            bos.write(newLine);
        }
        bos.write(newLine);
    }

}
