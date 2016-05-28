package de.jbi.photosync.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.jbi.photosync.utils.AndroidUtil;

/**
 * Created by Jan on 19.05.2016.
 */
public class Picture {

    private File absolutePath;

    private String name;

    private long size;

    public Picture(File absolutePath, String name, long size) {
        this.absolutePath = absolutePath;
        this.name = name;
        this.size = size;
    }

    public File getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(File absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public static List<Picture> getPicturesFromFile(File parentFile) {
        List<File> fileList = AndroidUtil.getAllPhotos(parentFile.getAbsoluteFile(), false);
        List<Picture> pictureList = new ArrayList<>();

        for (File file : fileList) {
            Picture pic = new Picture(file.getAbsoluteFile(), file.getName(), file.length());
            pictureList.add(pic);
        }
        return pictureList;
    }

    /**
     * Returns a new list of Strings that contain the names of the passed picture list
     * @param pictureList
     * @return
     */
    public static List<String> getPictureNameList(List<Picture> pictureList) {
        List<String> pictureNames = new ArrayList<>();
        for(int i = 0; i != pictureList.size(); i++) {
            pictureNames.add(pictureList.get(i).getName());
        }
        return pictureNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Picture picture = (Picture) o;

        if (size != picture.size) return false;
        if (!absolutePath.equals(picture.absolutePath)) return false;
        return name != null ? name.equals(picture.name) : picture.name == null;

    }

    @Override
    public int hashCode() {
        int result = absolutePath != null ? absolutePath.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }
}
