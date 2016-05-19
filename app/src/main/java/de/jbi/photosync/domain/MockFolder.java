package de.jbi.photosync.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.jbi.photosync.domain.Folder;

/**
 * Created by Jan on 16.05.2016.
 */
public class MockFolder {
    private static File mockFileA = new File("/storage/emulated/0/Pictures/Leute");
    private static File mockFileB = new File("/storage/emulated/0/Pictures/Fitness");

    private static List<Picture> mockPictureListA = new ArrayList<>();
    private static List<Picture> mockPictureListB = new ArrayList<>();


    static {
        mockPictureListA.add(MockPicture.mockPictureA);
        mockPictureListA.add(MockPicture.mockPictureB);
        mockPictureListB.add(MockPicture.mockPictureB);
        mockPictureListB.add(MockPicture.mockPictureC);
    }

    public static Folder mockFolderA = new Folder(mockFileA, mockFileA.getName(), 50, mockFileA.getTotalSpace(), true, mockPictureListA);
    public static Folder mockFolderB = new Folder(mockFileB, mockFileB.getName(), 40, mockFileB.getTotalSpace(), true, mockPictureListB);

}
