package de.jbi.photosync.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.jbi.photosync.utils.AndroidUtil;

/**
 * Created by Jan on 19.05.2016.
 */
public class PictureVideo {

    private File absolutePath;

    private String name;

    private long size;

    private Boolean enabled;

    public PictureVideo(File absolutePath, String name, long size) {
        this.absolutePath = absolutePath;
        this.name = name;
        this.size = size;
        this.enabled = true;
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

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }


    public static List<PictureVideo> getPicturesAndVideosFromFile(File parentFile) {
        List<File> fileList = AndroidUtil.getAllPhotosAndVideos(parentFile.getAbsoluteFile(), false);
        List<PictureVideo> pictureVideoList = new ArrayList<>();

        for (File file : fileList) {
            PictureVideo pic = new PictureVideo(file.getAbsoluteFile(), file.getName(), file.length());
            pictureVideoList.add(pic);
        }
        return pictureVideoList;
    }

    /**
     * Returns a new list of Strings that contain the names of the passed picture list
     *
     * @param pictureVideoList
     * @return
     */
    public static List<String> getPictureNameList(List<PictureVideo> pictureVideoList) {
        List<String> pictureNames = new ArrayList<>();
        for (int i = 0; i != pictureVideoList.size(); i++) {
            pictureNames.add(pictureVideoList.get(i).getName());
        }
        return pictureNames;
    }

    /**
     * Returns a new list of PictureVideo instances which enabled property is enabled
     * @param pictureVideoList
     * @return
     */
    public static List<PictureVideo> getEnabledPictureVideos(List<PictureVideo> pictureVideoList) {
        List<PictureVideo> enabledPicVidList = new ArrayList<>();
        for (PictureVideo picVid : pictureVideoList) {
            if (picVid.isEnabled())
                enabledPicVidList.add(picVid);
        }
        return enabledPicVidList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PictureVideo that = (PictureVideo) o;

        if (size != that.size) return false;
        if (absolutePath != null ? !absolutePath.equals(that.absolutePath) : that.absolutePath != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return enabled != null ? enabled.equals(that.enabled) : that.enabled == null;

    }

    @Override
    public int hashCode() {
        int result = absolutePath != null ? absolutePath.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        return result;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        PictureVideo pictureVideo = (PictureVideo) o;
//
//        if (size != pictureVideo.size) return false;
//        if (!absolutePath.equals(pictureVideo.absolutePath)) return false;
//        return name != null ? name.equals(pictureVideo.name) : pictureVideo.name == null;
//
//    }
//
//    @Override
//    public int hashCode() {
//        int result = absolutePath != null ? absolutePath.hashCode() : 0;
//        result = 31 * result + (name != null ? name.hashCode() : 0);
//        result = 31 * result + (int) (size ^ (size >>> 32));
//        return result;
//    }
}
