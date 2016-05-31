package de.jbi.photosync.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    private List<Picture> pictures;

    public Folder(File absolutePath, String name, int childAmount, long size, Boolean selected, List<Picture> pictures) {
        this.id = UUID.randomUUID();
        this.absolutePath = absolutePath;
        this.name = name;
        this.childAmount = childAmount;
        this.size = size;
        this.selected = selected;
        this.pictures = pictures;
    }

    /**
     * Intelligent constructor which inherits all values from the passed file
     * @param file
     * @param selected
     */
    public Folder(File file, Boolean selected) {
        this.id = UUID.randomUUID();
        this.absolutePath = file.getAbsoluteFile();
        this.name = file.getName();
        this.childAmount = getAllPhotos(file, false).size();
        this.selected = selected;
        this.pictures = Picture.getPicturesFromFile(file);
        this.size = getSizeFromPicturelist(Picture.getPicturesFromFile(file));
    }

    public void setId(UUID id) {
        this.id = id;
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
     * Attention: Only png, jpg and jpeg child-files are used for size calculation
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


    /**
     * The list of pictures (children of folder)
     */
    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        for (Picture pic : pictures) {
            if (pic.getAbsolutePath().isDirectory()) {
                throw new IllegalArgumentException("Argument is a directory: " + pic);
            }
        }
        this.pictures = pictures;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "id=" + id +
                ", absolutePath=" + absolutePath +
                ", name='" + name + '\'' +
                ", childAmount=" + childAmount +
                ", size=" + size +
                ", selected=" + selected +
                '}';
    }

    /**
     * Refreshes the childAmount, picture List and the size of the folder object
     */
    public void refreshFolderMetaData() {
        this.childAmount = getAllPhotos(this.absolutePath, false).size();
        this.pictures = Picture.getPicturesFromFile(this.absolutePath);
        this.size = getSizeFromPicturelist(Picture.getPicturesFromFile(this.absolutePath));
    }


    /**
     * Sums up the sizes of all passed pictures
     * @param picList
     * @return
     */
    public static long getSizeFromPicturelist(List<Picture> picList) {
        long size = 0;
        for (Picture pic : picList) {
            size += pic.getSize();
        }
        return size;
    }

    public static long getSizeFromFolder(Folder folder) {
        return getSizeFromPicturelist(folder.getPictures());
    }

    /**
     * Returns a new list of Strings that contain the names of the passed folder list
     * @param folderList
     * @return
     */
    public static List<String> getFolderNameList(List<Folder> folderList) {
        List<String> folderNames = new ArrayList<>();
        for(int i = 0; i != folderList.size(); i++) {
            folderNames.add(folderList.get(i).getName());
        }
        return folderNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Folder folder = (Folder) o;

//        if (childAmount != folder.childAmount) return false;
//        if (size != folder.size) return false;
        if (id != null ? !id.equals(folder.id) : folder.id != null) return false;
        if (!absolutePath.equals(folder.absolutePath)) return false;
        if (name != null ? !name.equals(folder.name) : folder.name != null) return false;
        if (selected != null ? !selected.equals(folder.selected) : folder.selected != null)
            return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + absolutePath.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + childAmount;
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (selected != null ? selected.hashCode() : 0);
        result = 31 * result + (pictures != null ? pictures.hashCode() : 0);
        return result;
    }
}
