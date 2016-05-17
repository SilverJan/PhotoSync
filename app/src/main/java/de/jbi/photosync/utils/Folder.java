package de.jbi.photosync.utils;

import java.io.File;
import java.util.UUID;

import static de.jbi.photosync.utils.AndroidUtil.getAllPhotos;

/**
 * Created by Jan on 14.05.2016.
 */
public class Folder {
    private UUID id;

    private File absolutePath;

    private String name;

    private int childAmount;

    private long size;

    private Boolean selected;

    public Folder(File absolutePath, String name, int childAmount, long size, Boolean selected) {
        this.id = UUID.randomUUID();
        this.absolutePath = absolutePath;
        this.name = name;
        this.childAmount = childAmount;
        this.size = size;
        this.selected = selected;
    }

    public Folder(File file, Boolean selected) {
        this.id = UUID.randomUUID();
        this.absolutePath = file.getAbsoluteFile();
        this.name = file.getName();
        this.childAmount = getAllPhotos(file, false).size();
        this.size = file.getTotalSpace();
        this.selected = selected;
    }

    public UUID getId() {
        return id;
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
    public int getChildAmount() {
        return childAmount;
    }

    public void setChildAmount(int childAmount) {
        this.childAmount = childAmount;
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
