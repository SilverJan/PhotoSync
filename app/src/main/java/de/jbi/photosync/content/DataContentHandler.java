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
        if (selectedFolders.contains(folder)) {
            // TODO: Logging?
            throw new IllegalArgumentException();
        }

        selectedFolders.add(folder);
    }

    public void deleteSelectedFolder(Folder folder) {
        if (!selectedFolders.contains(folder)) {
            // TODO: Logging?
            throw new IllegalArgumentException();
        }

        selectedFolders.remove(folder);
    }

}
