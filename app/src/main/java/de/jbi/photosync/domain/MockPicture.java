package de.jbi.photosync.domain;

import java.io.File;
import java.util.UUID;

/**
 * Created by Jan on 19.05.2016.
 */
public class MockPicture {
    private static File mockFileA = new File("/storage/emulated/0/Pictures/Leute/a.jpg");
    private static File mockFileB = new File("/storage/emulated/0/Pictures/Fitness/b.png");
    private static File mockFileC = new File("/storage/emulated/0/Pictures/Fitness/c.jpeg");


    public static Picture mockPictureA = new Picture(mockFileA, mockFileA.getName(), mockFileA.length());
    public static Picture mockPictureB = new Picture(mockFileB, mockFileB.getName(), mockFileB.length());
    public static Picture mockPictureC = new Picture(mockFileC, mockFileC.getName(), mockFileC.length());

}
