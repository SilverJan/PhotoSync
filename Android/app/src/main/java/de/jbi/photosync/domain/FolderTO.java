package de.jbi.photosync.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jan on 18.05.2016.
 */
public class FolderTO {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("absolutePath")
    @Expose
    public String absolutePath;

    @SerializedName("childAmount")
    @Expose
    public int childAmount;

    @SerializedName("size")
    @Expose
    public long size;

    @SerializedName("selected")
    @Expose
    public boolean selected;

    @SerializedName("pictures")
    @Expose
    private List<PictureVideoTO> pictureVideoTOs;

    public FolderTO(Boolean selected, String id, String absolutePath, String name, int childAmount, long size, List<PictureVideoTO> pictureVideoTOs) {
        this.selected = selected;
        this.id = id;
        this.absolutePath = absolutePath;
        this.name = name;
        this.childAmount = childAmount;
        this.size = size;
        this.pictureVideoTOs = pictureVideoTOs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getChildAmount() {
        return childAmount;
    }

    public void setChildAmount(int childAmount) {
        this.childAmount = childAmount;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public List<PictureVideoTO> getPictureVideoTOs() {
        return pictureVideoTOs;
    }

    public void setPictureVideoTOs(List<PictureVideoTO> pictureVideoTOs) {
        this.pictureVideoTOs = pictureVideoTOs;
    }

    @Override
    public String toString() {
        return "FolderTO{" +
                "id='" + id + '\'' +
                ", absolutePath='" + absolutePath + '\'' +
                ", name='" + name + '\'' +
                ", childAmount=" + childAmount +
                ", size=" + size +
                ", selected=" + selected +
                '}';
    }
}
