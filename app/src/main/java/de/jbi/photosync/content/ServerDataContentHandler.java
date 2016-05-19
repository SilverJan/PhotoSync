package de.jbi.photosync.content;

import java.util.ArrayList;
import java.util.List;

import de.jbi.photosync.domain.Folder;

/**
 * Created by Jan on 17.05.2016.
 */
public class ServerDataContentHandler {
    private static ServerDataContentHandler ourInstance = new ServerDataContentHandler();
    private List<Folder> folders;

    public static ServerDataContentHandler getInstance() {
        return ourInstance;
    }

    private ServerDataContentHandler() {
        folders = new ArrayList<>();
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }
}
