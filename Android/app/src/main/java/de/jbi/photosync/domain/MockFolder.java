package de.jbi.photosync.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 16.05.2016.
 */
public class MockFolder {
    private static File mockFileA = new File("/storage/emulated/0/Pictures/Leute");
    private static File mockFileB = new File("/storage/emulated/0/Pictures/Fitness");

    private static List<PictureVideo> mockPictureVideoListA = new ArrayList<>();
    private static List<PictureVideo> mockPictureVideoListB = new ArrayList<>();


    static {
        mockPictureVideoListA.add(MockPictureVideo.mockPictureVideoA);
        mockPictureVideoListA.add(MockPictureVideo.mockPictureVideoB);
        mockPictureVideoListB.add(MockPictureVideo.mockPictureVideoB);
        mockPictureVideoListB.add(MockPictureVideo.mockPictureVideoC);
    }

    public static Folder mockFolderA = new Folder(mockFileA, mockFileA.getName(), 50, mockFileA.getTotalSpace(), true, mockPictureVideoListA);
    public static Folder mockFolderB = new Folder(mockFileB, mockFileB.getName(), 40, mockFileB.getTotalSpace(), true, mockPictureVideoListB);

}
