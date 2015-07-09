package alexiil.starter;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DependencyDownloader {
    private static final int BUFFER_SIZE = 16384;

    public static File tryThrice(String url) throws IOException {
        IOException except = null;
        for (int i = 0; i < 3; i++) {
            try {
                return download(url);
            }
            catch (IOException io) {
                except = new IOException(io);
            }
        }
        throw except;
    }

    public static File download(String url) throws IOException {
        String tempFolder = System.getProperty("java.io.tmpdir");
        File folder = new File(tempFolder);
        File tempFile = new File(folder, "dependency-" + url.hashCode() + "-" + System.currentTimeMillis());
        // We don't want this afterwards
        tempFile.deleteOnExit();

        InputStream in = null;
        FileOutputStream fos = null;

        try {
            URL urlActual = new URL(url);
            URLConnection connection = urlActual.openConnection();
            in = connection.getInputStream();
            fos = new FileOutputStream(tempFile);
            byte[] buffer = new byte[BUFFER_SIZE];
            int read = BUFFER_SIZE;
            while (read == BUFFER_SIZE) {
                read = in.read(buffer);
                fos.write(buffer, 0, read);
            }
            in.close();
            fos.close();
        }
        catch (IOException i) {
            closeQuietly(in);
            closeQuietly(fos);

            throw new IOException(i);
        }

        return tempFile;
    }

    private static void closeQuietly(Closeable close) {
        try {
            if (close != null)
                close.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
