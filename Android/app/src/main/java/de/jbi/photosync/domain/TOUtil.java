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
     * Converts a PictureTO instance to a Picture instance
     * Needed for GSON and Retrofit
     *
     * @param pictureTO
     * @return
     */
    public static Picture convertPictureTOToPicture(PictureTO pictureTO) {
        return new Picture(new File(pictureTO.getAbsolutePath()), pictureTO.getName(), pictureTO.getSize());
    }

    /**
     * Converts a Picture instance to a PictureTO instance
     * Needed for GSON and Retrofit
     *
     * @param picture
     * @return
     */
    public static PictureTO convertPictureToPictureTO(Picture picture) {
        return new PictureTO(picture.getAbsolutePath().getAbsolutePath(), picture.getName(), picture.getSize());
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
        List<Picture> pictureList = new ArrayList<>();
        for (PictureTO picTO : folderTO.getPictureTOs()) {
            pictureList.add(convertPictureTOToPicture(picTO));
        }
        return new Folder(new File(folderTO.getAbsolutePath()), folderTO.getName(), folderTO.getChildAmount(), folderTO.getSize(), folderTO.getSelected(), pictureList);
    }

    /**
     * Converts a FolderTO instance to a Folder instance
     * Needed for GSON and Retrofit
     *
     * @param folder
     * @return
     */
    public static FolderTO convertFolderToFolderTO(Folder folder) {
        List<PictureTO> pictureTOs = new ArrayList<>();
        for (Picture pic : folder.getPictures()) {
            pictureTOs.add(convertPictureToPictureTO(pic));
        }
        return new FolderTO(folder.getSelected(), folder.getId().toString(), folder.getAbsolutePath().getAbsolutePath(), folder.getName(), folder.getChildAmount(), folder.getSize(), pictureTOs);
    }
}
