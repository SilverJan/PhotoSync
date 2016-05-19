package de.jbi.photosync.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jbi.photosync.content.ServerDataContentHandler;
import de.jbi.photosync.domain.Folder;
import de.jbi.photosync.domain.FolderTO;
import de.jbi.photosync.utils.Logger;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jan on 17.05.2016.
 */
public class PhotoSyncBoundary {
    private static PhotoSyncBoundary ourInstance = new PhotoSyncBoundary();
    private Retrofit retrofit;
    private PhotoSyncService photoSyncService;

    private static final String BASE_URL = "https://192.168.178.46:8443";

    public static PhotoSyncBoundary getInstance() {
        return ourInstance;
    }

    private PhotoSyncBoundary() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        photoSyncService = retrofit.create(PhotoSyncService.class);
    }

    /**
     * Call this before doing any REST call, otherwise -> CertPathValidatorException
     * Recently this is called in MainActivity (because of Context needed)
     *
     * @param client
     */
    public void setClient(OkHttpClient client) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        photoSyncService = retrofit.create(PhotoSyncService.class);
    }

    public void getAllFolders() throws IOException {
        photoSyncService.getAllFolders().enqueue(new Callback<List<FolderTO>>() {
            @Override
            public void onResponse(Call<List<FolderTO>> call, Response<List<FolderTO>> response) {
                if (response.isSuccessful()) {

                    // Converting FolderTO List to Folder List and add to ServerDataContentHandler
                    List<FolderTO> folderTOList = response.body();
                    List<Folder> folderList = new ArrayList<>();

                    for (FolderTO folderTO : folderTOList) {
                        folderList.add(Folder.convertFolderTOtoFolder(folderTO));
                    }

                    ServerDataContentHandler.getInstance().setFolders(folderList);
                } else {
                    String message = "Response unsuccessful (" + response.code() + "): " + response.message();
                    Logger.getInstance().appendLog(message);
                }
            }

            @Override
            public void onFailure(Call<List<FolderTO>> call, Throwable t) {
                Logger.getInstance().appendLog("Response failed: " + t.getMessage());
            }
        });
    }

    public void addFolder(Folder folder) {
        photoSyncService.createFolder(folder).enqueue(new Callback<Folder>() {
            @Override
            public void onResponse(Call<Folder> call, Response<Folder> response) {
                if (response.isSuccessful()) {
                    Logger.getInstance().appendLog("Post successful");
                } else {
                    String message = "Response unsuccessful (" + response.code() + "): " + response.message();
                    Logger.getInstance().appendLog(message);
                }
            }

            @Override
            public void onFailure(Call<Folder> call, Throwable t) {
                Logger.getInstance().appendLog("Response failed: " + t.getMessage());
            }
        });
    }
}
