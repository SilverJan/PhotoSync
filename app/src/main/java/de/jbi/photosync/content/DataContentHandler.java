package de.jbi.photosync.content;

import java.util.ArrayList;
import java.util.List;

import de.jbi.photosync.domain.Folder;

/**
 * Created by Jan on 14.05.2016.
 */
public class DataContentHandler {
    private static DataContentHandler ourInstance = new DataContentHandler();
    private List<Folder> folders;

    public static DataContentHandler getInstance() {
        return ourInstance;
    }

    private DataContentHandler() {
        folders = new ArrayList<>();
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public void addFolder(Folder folder) {
        Boolean exists = false;
        for (Folder selFolder : folders) {
            if (selFolder.getAbsolutePath().getPath().equals(folder.getAbsolutePath().getPath())) {
                exists = true;
                break;
            }
        }

        if (folders.contains(folder) || exists) {
            throw new IllegalArgumentException("Folder already exists!");
        }

        folders.add(folder);
    }

    public void removeFolder(Folder folder) {
        if (!folders.contains(folder)) {
            throw new IllegalArgumentException();
        }

        folders.remove(folder);
    }

    public int getTotalAmountOfFiles() {
        int total = 0;
        for (Folder folder: folders) {
            total += folder.getChildAmount();
        }
        return total;
    }

}
