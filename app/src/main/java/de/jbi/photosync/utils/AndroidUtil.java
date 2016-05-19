package de.jbi.photosync.utils;

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
     * Returns a list of all .jepg and .jpg files in a directory
     *
     * @param parentDir The parent directory to search in
     * @param recursive Pass true, if recursive search should be allowed
     * @return
     */
    public static List<File> getAllPhotos(File parentDir, Boolean recursive) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if (recursive) {
                    inFiles.addAll(getAllPhotos(file, true));
                }
            } else {
                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png")) {
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
}
