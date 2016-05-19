package de.jbi.photosync.domain;

import java.util.List;

/**
 * Created by Jan on 18.05.2016.
 */
public class FolderTO {
    private String id;

    private String absolutePath;

    private String name;

    private int childAmount;

    private long size;

    private Boolean selected;

    private List<Picture> pictures;

    public FolderTO(Boolean selected, String id, String absolutePath, String name, int childAmount, long size, List<Picture> pictures) {
        this.selected = selected;
        this.id = id;
        this.absolutePath = absolutePath;
        this.name = name;
        this.childAmount = childAmount;
        this.size = size;
        this.pictures = pictures;
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

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
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
