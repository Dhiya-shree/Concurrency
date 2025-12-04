package performance;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageProcessing_Optimization {
    static final String source = "D:/Repos/Concurrency/Concurrency/resources/imageProcessing/flowersOriginal.png";
    static final String destination = "resources\\imageProcessing\\flowersProcessed.png";
    static final String destination2 = "resources\\imageProcessing\\flowersProcessedMultiThread.png";

    public static void main(String[] args) throws IOException {
        File file = new File(source);
        System.out.println(file.exists());
        BufferedImage reader = ImageIO.read(file);
        System.out.println(reader.toString());
        BufferedImage processed = new BufferedImage(reader.getWidth(), reader.getHeight(), BufferedImage.TYPE_INT_RGB);
        long start = System.currentTimeMillis();
        System.out.println(" Single thread start : ");
        recolorSingleThread(reader, processed);
        System.out.println(" Single thread ended . Latency : " + (System.currentTimeMillis() - start));
        File output = new File(destination);
        ImageIO.write(processed, "png", output);
        start = System.currentTimeMillis();
        System.out.println(" Multi thread start : ");
        recolorMultiThread(reader, processed, 4);
        System.out.println(" Multi thread ended . Latency : " + (System.currentTimeMillis() - start));
        File outputMutiThread = new File(destination2);
        ImageIO.write(processed, "png", outputMutiThread);

    }

    public static void recolorMultiThread(BufferedImage original, BufferedImage processed, int t) {
        int w = original.getWidth();
        int h = original.getHeight();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < t; i++) {
            int threadMultiplier = i;
            threads.add(new Thread(() -> recolorImage(original, processed, 0, h * threadMultiplier, w, h)));
        }

        for (Thread th : threads) {
            th.start();
        }

        for (Thread th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void recolorSingleThread(BufferedImage original, BufferedImage processed) {
        recolorImage(original, processed, 0, 0, original.getWidth(), original.getHeight());

    }

    public static void recolorImage(BufferedImage original, BufferedImage processed, int left, int top, int w, int h) {
        for (int i = left; i < left + w && i < original.getWidth(); i++) {
            for (int j = top; j < top + h && j < original.getHeight(); j++) {
                recolorPixel(original, processed, i, j);
            }
        }
    }

    public static void recolorPixel(BufferedImage reader, BufferedImage result, int x, int y) {
        int rgb = reader.getRGB(x, y);
        int red = getRed(rgb);
        int blue = getBlue(rgb);
        int green = getGreen(rgb);

        if (isNearlyWhite(red, green, blue)) {
            int newRed = Math.min(255, red + 5);
            int newBlue = Math.min(255, blue + 15);
            int newGreen = Math.max(0, green - 90);
            int newRbg = createRGB(newRed, newGreen, newBlue);
            setRBG(result, x, y, newRbg);
        } else {
            setRBG(result, x, y, rgb);
        }
    }

    private static void setRBG(BufferedImage result, int x, int y, int newRbg) {
        result.getRaster().setDataElements(x, y, result.getColorModel().getDataElements(newRbg, null));
    }

    public static boolean isNearlyWhite(int red, int green, int blue) {
        return (red > 100 && green > 100 && blue > 100);// && Math.abs(red - green) > 35 && Math.abs(red - blue) > 35
        // && Math.abs(green - blue) > 35);
    }

    public static int createRGB(int red, int green, int blue) {
        int rgb = 0;
        rgb = rgb | blue;
        rgb = rgb | green << 8;
        rgb = rgb | red << 16;
        rgb = rgb | 0xFF000000;
        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }
}
