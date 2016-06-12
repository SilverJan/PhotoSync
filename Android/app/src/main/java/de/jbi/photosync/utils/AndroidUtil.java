package de.jbi.photosync.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 13.05.2016.
 */
public class AndroidUtil {

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Returns a formatted human readable byte count string
     * @param bytes
     * @param si some way of formatting. Just pass true^^
     * @return
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Returns a list of all .jepg, .jpg, .png, .mp4, .wmv files in a directory
     *
     * @param parentDir The parent directory to search in
     * @param recursive Pass true, if recursive search should be allowed
     * @return
     */
    public static List<File> getAllPhotosAndVideos(File parentDir, Boolean recursive) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if (recursive) {
                    inFiles.addAll(getAllPhotosAndVideos(file, true));
                }
            } else {
                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png") ||
                file.getName().endsWith(".mp4") || file.getName().endsWith(".wmv")) {
                    inFiles.add(file);
                }
            }
        }
        return inFiles;
    }

    /**
     * Returns the amount of child elements in a directory. Directories are ignored
     *
     * @param dir       The parent directory to search in
     * @param recursive Pass true, if recursive search should be allowed
     * @return
     */
    public static int getDirectoryElements(File dir, Boolean recursive) {
        int amount = 0;
        for (final File fileEntry : dir.listFiles()) {
            if (fileEntry.isDirectory()) {
                if (recursive) {
                    getDirectoryElements(fileEntry, true);
                }
            } else {
                amount++;
            }
        }
        return amount;
    }

    public static boolean isPortInvalid(String port) {
        return port.length() > 4 || port == null || port.equals("");
    }

    public static boolean isIpInvalid(String url) {
        // TODO add regexp
        return url == null || url.equals("");
    }

    public static class ContextHandler {
        private static Context mainCtx;

        public static void setMainContext(Context ctx) {
            mainCtx = ctx;
        }
        public static Context getMainContext() {
            if (mainCtx == null) {
                throw new ExceptionInInitializerError("Context is not set! First call setMainContext() in MainActivity!");
            } else {
                return mainCtx;
            }
        }
    }
}
