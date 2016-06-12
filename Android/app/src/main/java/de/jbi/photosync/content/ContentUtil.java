package de.jbi.photosync.content;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.jbi.photosync.domain.Folder;
import de.jbi.photosync.domain.PictureVideo;

/**
 * Created by Jan on 11.06.2016.
 */
public class ContentUtil {
    /**
     * Compares the local and the server contents and returns a queue that contains all the new files
     * @return
     */
    public static Queue<PictureVideo> getNewFilesToUploadQueue() {
        List<Folder> allServerFolders = ServerDataContentHandler.getInstance().getFolders();
        List<String> allServerFolderNames = Folder.getFolderNameList(allServerFolders);
        List<PictureVideo> allPicsVidsToUpload = new ArrayList<>();

        // STEP 2: Get all selected folders from client
        List<Folder> allClientFolders = DataContentHandler.getInstance().getFolders();

        // STEP 3: Sort client folders to a) server-existing or b) non-server-existing list
        List<Folder> completeFoldersToUpload = new ArrayList<>();
        List<Folder> incompleteFoldersToUpload = new ArrayList<>();
        for (Folder clientFolder : allClientFolders) {
            if (!allServerFolderNames.contains(clientFolder.getName())) {
                completeFoldersToUpload.add(clientFolder);
            } else {
                incompleteFoldersToUpload.add(clientFolder);
            }
        }

        // STEP 4: For all non-server-existing folders -> Add pictures to uploadList (missing folders will be added automatically)
        for (Folder completeFolderToUpload : completeFoldersToUpload) {
            for (PictureVideo clientPicInNewFolder : completeFolderToUpload.getPictureVideos()) {
                allPicsVidsToUpload.add(clientPicInNewFolder);
            }
        }

        // STEP 5: For all server-existing folders -> Get server-equal-folder
        for (Folder incompleteFolderToUpload : incompleteFoldersToUpload) {
            int serverEqualIndex = allServerFolderNames.indexOf(incompleteFolderToUpload.getName());
            Folder serverEqual = allServerFolders.get(serverEqualIndex);

            // STEP 6: If server and client folder are equal (same size of pics) then do nothing
            if (serverEqual.getSize() == incompleteFolderToUpload.getSize()) {
                continue;
            } else {
                // STEP 7: For all pictures in a server-existing folder -> If clientPic !exists (name) on server -> Add to upload list
                List<PictureVideo> allClientFolderPicsVids = incompleteFolderToUpload.getPictureVideos();
                List<PictureVideo> allServerFolderPics = serverEqual.getPictureVideos();
                List<String> allServerFolderPicsNames = PictureVideo.getPictureNameList(allServerFolderPics);

                for (PictureVideo clientFolderPicVid : allClientFolderPicsVids) {
                    if (!allServerFolderPicsNames.contains(clientFolderPicVid.getName())) {
                        allPicsVidsToUpload.add(clientFolderPicVid);
                    }
                }
            }
        }
        return new LinkedList<>(allPicsVidsToUpload);
    }
}
