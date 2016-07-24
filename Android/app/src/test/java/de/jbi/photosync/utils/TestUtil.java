package de.jbi.photosync.utils;

import org.robolectric.RuntimeEnvironment;

import java.io.File;

import de.jbi.photosync.domain.Folder;

/**
 * Created by Jan on 28.05.2016.
 */
public class TestUtil {
    public static String getTestEnvironmentPath() {
        String path = RuntimeEnvironment.application.getPackageResourcePath();
        String correctedPath = path.substring(0, path.length() - 1); // Remove '.' from path
        return correctedPath;
    }

    public static Folder getTestFolderA() {
        return new Folder(new File(getTestEnvironmentPath() + "src\\test\\res\\drawable\\A"), true);
    }

    public static Folder getTestFolderB() {
        return new Folder(new File(getTestEnvironmentPath() + "src\\test\\res\\drawable\\B"), true);
    }
}
