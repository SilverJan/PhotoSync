package de.jbi.photosync.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jan on 19.05.2016.
 */
public class PictureVideoTO {
    @SerializedName("absolutePath")
    @Expose
    public String absolutePath;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("size")
    @Expose
    public long size;

    public PictureVideoTO(String absolutePath, String name, long size) {
        this.absolutePath = absolutePath;
        this.name = name;
        this.size = size;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
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

    @Override
    public String toString() {
        return "PictureVideoTO{" +
                "absolutePath='" + absolutePath + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PictureVideoTO pictureVideoTO = (PictureVideoTO) o;

        if (size != pictureVideoTO.size) return false;
        if (absolutePath != null ? !absolutePath.equals(pictureVideoTO.absolutePath) : pictureVideoTO.absolutePath != null)
            return false;
        return name != null ? name.equals(pictureVideoTO.name) : pictureVideoTO.name == null;

    }

    @Override
    public int hashCode() {
        int result = absolutePath != null ? absolutePath.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }
}
