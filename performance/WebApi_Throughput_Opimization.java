package performance;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.*;

public class WebApi_Throughput_Opimization {

    private static final String InputFile = "C:\\Users\\shree\\OneDrive\\Documents\\Sample.txt";
    private static final int NUMBER_OF_THREADS = 4;

    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(InputFile)));
        startServer(text);
    }

    private static void startServer(String text) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/search", new WordCountHandler(text));
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        server.setExecutor(executor);
        server.start();
    }

    private static class WordCountHandler implements HttpHandler {

        private String text;

        public WordCountHandler(String text) {
            this.text = text;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String[] keyvalue = query.split("=");
            String action = keyvalue[0];
            String word = keyvalue[1];

            byte[] response;
            OutputStream os = exchange.getResponseBody();
            if (!action.equals("word")) {
                response = ("Word not found !!!").getBytes();
                exchange.sendResponseHeaders(400, response.length);
                os.write(response);
                os.close();
                return;
            }

            long count = countWord(word);
            if (count == Long.MIN_VALUE) {
                System.out.println(" Error for request word : " + word);
                response = ("Not able to process !!!").getBytes();
                exchange.sendResponseHeaders(400, response.length);
                os.write(response);
                os.close();
                return;
            }
            System.out.println(" Word " + word + " : " + count);
            response = Long.toString(count).getBytes();
            exchange.sendResponseHeaders(200, response.length);
            os.write(response);
            os.close();

        }

        private long countWord(String word) {
            long count = 0;
            int index = 0;
            System.out.println(" Checking count of word for " + word);
            while (index >= 0) {
                index = text.indexOf(word, index + 1);
                if (index >= 0) {
                    count++;
                    index++;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                return Long.MIN_VALUE;
            }
            return count;
        }

    }

}
