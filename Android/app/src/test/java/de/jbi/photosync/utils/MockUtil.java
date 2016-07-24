package de.jbi.photosync.utils;

import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import de.jbi.photosync.content.DataContentHandler;
import de.jbi.photosync.domain.Folder;
import de.jbi.photosync.domain.MockFolder;

/**
 * Created by Jan on 01.06.2016.
 */
public class MockUtil {
    public static void setDataContentHandlerMock(DataContentHandler mockedObject) {
        List<Folder> demoList = new ArrayList<>();
        demoList.add(MockFolder.mockFolderA);
        demoList.add(MockFolder.mockFolderB);
        Mockito.when(mockedObject.getFolders()).thenReturn(demoList);
        Mockito.when(mockedObject.getTotalAmountOfFiles()).thenReturn(2);
    }
}
