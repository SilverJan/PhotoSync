package de.jbi.photosync.http;

import java.util.List;

import de.jbi.photosync.domain.Folder;
import de.jbi.photosync.domain.FolderTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Jan on 17.05.2016.
 */
public interface PhotoSyncService {
    @GET("folders")
    Call<List<FolderTO>> getAllFolders();

    @GET("folders/{folder}")
    Call<FolderTO> getFolder(@Path("folder") String folderName);

    @Headers({
            "Content-Type:application/json"
    })
    @POST("folders")
    Call<Folder> createFolder(@Body Folder folder);
}
