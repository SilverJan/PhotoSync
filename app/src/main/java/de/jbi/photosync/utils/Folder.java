package de.jbi.photosync.utils;

import java.io.File;

/**
 * Created by Jan on 14.05.2016.
 */
public class Folder {
    private String name;

    private File absolutePath;

    private int contentAmount;

    private long size;

    private Boolean selected;

    public Folder(File absolutePath, String name, int contentAmount, long size, Boolean selected) {
        this.absolutePath = absolutePath;
        this.name = name;
        this.contentAmount = contentAmount;
        this.size = size;
        this.selected = selected;
    }


    /**
     * The name of the folder
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The absolute path of the folder
     */
    public File getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(File absolutePath) {
        this.absolutePath = absolutePath;
    }

    /**
     * The amount of files inside of the folder
     */
    public int getContentAmount() {
        return contentAmount;
    }

    public void setContentAmount(int contentAmount) {
        this.contentAmount = contentAmount;
    }

    /**
     * The size (in KB) of the folder
     */
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Is the folder to be synced?
     */
    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
