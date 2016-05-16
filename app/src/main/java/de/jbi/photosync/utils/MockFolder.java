package de.jbi.photosync.utils;

import java.io.File;

/**
 * Created by Jan on 16.05.2016.
 */
public class MockFolder {
    private static File mockFileA = new File("/storage/emulated/0/Pictures/Leute");
    private static File mockFileB = new File("/storage/emulated/0/Pictures/Fitness");

    public static Folder mockFolderA = new Folder(mockFileA, mockFileA.getName(), 50, mockFileA.getTotalSpace(), true);
    public static Folder mockFolderB = new Folder(mockFileB, mockFileB.getName(), 40, mockFileB.getTotalSpace(), true);

}
