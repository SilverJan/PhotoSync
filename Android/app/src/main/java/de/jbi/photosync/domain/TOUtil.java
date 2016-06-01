package de.jbi.photosync.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 19.05.2016.
 */
public class TOUtil {

    // ###############
    // ### PICTURE ###
    // ###############

    /**
     * Converts a PictureVideoTO instance to a PictureVideo instance
     * Needed for GSON and Retrofit
     *
     * @param pictureVideoTO
     * @return
     */
    public static PictureVideo convertPictureTOToPicture(PictureVideoTO pictureVideoTO) {
        return new PictureVideo(new File(pictureVideoTO.getAbsolutePath()), pictureVideoTO.getName(), pictureVideoTO.getSize());
    }

    /**
     * Converts a PictureVideo instance to a PictureVideoTO instance
     * Needed for GSON and Retrofit
     *
     * @param pictureVideo
     * @return
     */
    public static PictureVideoTO convertPictureToPictureTO(PictureVideo pictureVideo) {
        return new PictureVideoTO(pictureVideo.getAbsolutePath().getAbsolutePath(), pictureVideo.getName(), pictureVideo.getSize());
    }

    // ##############
    // ### FOLDER ###
    // ##############

    /**
     * Converts a FolderTO instance to a Folder instance
     * Needed for GSON and Retrofit
     *
     * @param folderTO
     * @return
     */
    public static Folder convertFolderTOtoFolder(FolderTO folderTO) {
        List<PictureVideo> pictureVideoList = new ArrayList<>();
        for (PictureVideoTO picTO : folderTO.getPictureVideoTOs()) {
            pictureVideoList.add(convertPictureTOToPicture(picTO));
        }
        return new Folder(new File(folderTO.getAbsolutePath()), folderTO.getName(), folderTO.getChildAmount(), folderTO.getSize(), folderTO.getSelected(), pictureVideoList);
    }

    /**
     * Converts a FolderTO instance to a Folder instance
     * Needed for GSON and Retrofit
     *
     * @param folder
     * @return
     */
    public static FolderTO convertFolderToFolderTO(Folder folder) {
        List<PictureVideoTO> pictureVideoTOs = new ArrayList<>();
        for (PictureVideo pic : folder.getPictureVideos()) {
            pictureVideoTOs.add(convertPictureToPictureTO(pic));
        }
        return new FolderTO(folder.getSelected(), folder.getId().toString(), folder.getAbsolutePath().getAbsolutePath(), folder.getName(), folder.getChildAmount(), folder.getSize(), pictureVideoTOs);
    }
}
