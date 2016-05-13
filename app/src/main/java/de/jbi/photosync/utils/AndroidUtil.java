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

    public static List<File> getAllPhotos(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getAllPhotos(file));
            } else {
                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
                    inFiles.add(file);
                }
            }
        }
        return inFiles;
    }
}
