package de.jbi.photosync.content;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public static void setInstance(DataContentHandler dataContentHandler) throws IllegalAccessException {
        if (ourInstance == null) {
            ourInstance = dataContentHandler;
        } else {
            throw new IllegalAccessException("Instance is already set");
        }
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
        for (Folder folder : folders) {
            total += folder.getChildAmount();
        }
        return total;
    }

    public Folder findFolderById(UUID id) {
        for (Folder folder : folders) {
            if (folder.getId().equals(id)) {
                return folder;
            }
        }
        return null;
    }

    public Folder findFolderByPath(File path) {
        for (Folder folder : folders) {
            if (folder.getAbsolutePath().equals(path)) {
                return folder;
            }
        }
        return null;
    }

}
