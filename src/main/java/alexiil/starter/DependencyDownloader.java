package alexiil.starter;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class DependencyDownloader {
    public static File tryThrice(String url, MutableDouble mutable, double min, double max) throws IOException {
        System.out.println("Attempting to download " + url);
        IOException except = null;
        for (int i = 0; i < 3; i++) {
            try {
                return download(url, mutable, min, max);
            }
            catch (IOException io) {
                System.out.println("Attempt #" + (i + 1) + "failed!");
                except = new IOException(io);
            }
        }
        throw except;
    }

    public static File download(String url, MutableDouble mutable, double min, double max) throws IOException {
        String tempFolder = System.getProperty("java.io.tmpdir");
        File folder = new File(tempFolder);
        File tempFile = new File(folder, "dependency-" + url.hashCode() + "-" + System.currentTimeMillis());
        // We don't want this afterwards
        tempFile.deleteOnExit();

        FileOutputStream fos = null;

        try {
            URL urlActual = new URL(url);
            URLConnection connect = urlActual.openConnection();
            long size = connect.getContentLengthLong();
            ReadableByteChannel rbc = Channels.newChannel(connect.getInputStream());
            fos = new FileOutputStream(tempFile);
            MutableBoolean reading = new MutableBoolean();
            reading.value = true;
            FileChannel fc = fos.getChannel();

            System.out.println("Size = " + size);
            if (size > 0) {
                new Thread(() -> {
                    while (reading.value) {
                        long current = 0;
                        try {
                            current = fc.size();
                        }
                        catch (Exception ignored) {}

                        double s = current / (double) size;
                        mutable.value = min + (max - min) * s;
                        try {
                            Thread.sleep(30);
                        }
                        catch (Exception ignored) {}
                    }
                }, "Download-Monitor").start();
            }

            fc.transferFrom(rbc, 0, Long.MAX_VALUE);
            reading.value = false;
            fos.close();
        }
        catch (IOException i) {
            closeQuietly(fos);

            throw new IOException(i);
        }

        System.out.println("Downloaded " + url + " as " + tempFile);

        return tempFile;
    }

    public static void closeQuietly(Closeable close) {
        try {
            if (close != null)
                close.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
