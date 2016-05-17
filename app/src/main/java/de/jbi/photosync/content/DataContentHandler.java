package de.jbi.photosync.content;

import java.util.ArrayList;
import java.util.List;

import de.jbi.photosync.utils.Folder;

/**
 * Created by Jan on 14.05.2016.
 */
public class DataContentHandler {
    private static DataContentHandler ourInstance = new DataContentHandler();
    private List<Folder> selectedFolders;

    public static DataContentHandler getInstance() {
        return ourInstance;
    }

    private DataContentHandler() {
        selectedFolders = new ArrayList<>();
    }

    public List<Folder> getSelectedFolders() {
        return selectedFolders;
    }

    public void setSelectedFolders(List<Folder> selectedFolders) {
        this.selectedFolders = selectedFolders;
    }

    public void addSelectedFolder(Folder folder) {
        Boolean exists = false;
        for (Folder selFolder : selectedFolders) {
            if (selFolder.getAbsolutePath().getPath().equals(folder.getAbsolutePath().getPath())) {
                exists = true;
                break;
            }
        }

        if (selectedFolders.contains(folder) || exists) {
            throw new IllegalArgumentException("Folder already exists!");
        }

        selectedFolders.add(folder);
    }

    public void removeSelectedFolder(Folder folder) {
        if (!selectedFolders.contains(folder)) {
            throw new IllegalArgumentException();
        }

        selectedFolders.remove(folder);
    }

    public int getTotalAmountOfFiles() {
        int total = 0;
        for (Folder folder:selectedFolders) {
            total += folder.getChildAmount();
        }
        return total;
    }

}
