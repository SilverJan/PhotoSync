package de.jbi.photosync.domain;

import java.io.File;

/**
 * Created by Jan on 19.05.2016.
 */
public class MockPictureVideo {
    private static File mockFileA = new File("/storage/emulated/0/Download/da6ad85bfd.jpg");
    private static File mockFileB = new File("/storage/emulated/0/Pictures/Bodybilder/2014-02-27 22.20.54.jpg");
    private static File mockFileC = new File("/storage/emulated/0/Pictures/Bodybilder/2014-03-01 23.33.37.jpg");


    public static PictureVideo mockPictureVideoA = new PictureVideo(mockFileA, mockFileA.getName(), mockFileA.length());
    public static PictureVideo mockPictureVideoB = new PictureVideo(mockFileB, mockFileB.getName(), mockFileB.length());
    public static PictureVideo mockPictureVideoC = new PictureVideo(mockFileC, mockFileC.getName(), mockFileC.length());

}
