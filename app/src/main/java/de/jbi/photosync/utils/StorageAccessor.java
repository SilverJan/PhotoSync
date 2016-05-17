package de.jbi.photosync.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jan on 16.05.2016.
 */
public class StorageAccessor {
    private Context ctx;

    private static final String INTERNAL_STORAGE_FILENAME = "photosync_config";

    public StorageAccessor(Context ctx) {
        this.ctx = ctx;
    }

    public void readDataFromStorage() throws IOException {
        final File INTERNAL_STORAGE_FILE = new File(ctx.getFilesDir() + INTERNAL_STORAGE_FILENAME);

        FileInputStream fis = ctx.openFileInput(INTERNAL_STORAGE_FILENAME);
        byte fileContent[] = new byte[(int) INTERNAL_STORAGE_FILE.length()];
        fis.read(fileContent);
        String s = new String(fileContent);
        System.out.println(s);
        fis.close();
    }

    public void saveDataToStorage() throws IOException {
        FileOutputStream fos = ctx.openFileOutput(INTERNAL_STORAGE_FILENAME, Context.MODE_PRIVATE);
        fos.write("hallo".getBytes());
        fos.close();
    }
}
