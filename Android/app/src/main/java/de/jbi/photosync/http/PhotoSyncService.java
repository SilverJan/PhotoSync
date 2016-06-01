package de.jbi.photosync.http;

import java.util.List;

import de.jbi.photosync.domain.FolderTO;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
    Call<ResponseBody> createFolder(@Body FolderTO folder);

    @Multipart
    @POST("folders/{folder}")
    Call<ResponseBody> uploadPictureVideo(@Header("foldertoputinheader") String folderToPutInHeader,
                                          @Part("folderToPutIn") RequestBody folderToPutIn,
                                          @Part MultipartBody.Part picture);
}
