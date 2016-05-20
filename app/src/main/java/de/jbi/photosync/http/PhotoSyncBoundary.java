package de.jbi.photosync.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jbi.photosync.R;
import de.jbi.photosync.content.ServerDataContentHandler;
import de.jbi.photosync.content.SharedPreferencesUtil;
import de.jbi.photosync.domain.Folder;
import de.jbi.photosync.domain.FolderTO;
import de.jbi.photosync.domain.Picture;
import de.jbi.photosync.domain.PictureTO;
import de.jbi.photosync.fragments.SettingsFragment;
import de.jbi.photosync.utils.AndroidUtil;
import de.jbi.photosync.utils.Logger;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.jbi.photosync.domain.TOUtil.convertFolderTOtoFolder;
import static de.jbi.photosync.domain.TOUtil.convertFolderToFolderTO;
import static de.jbi.photosync.domain.TOUtil.convertPictureToPictureTO;

/**
 * Created by Jan on 17.05.2016.
 */
public class PhotoSyncBoundary {
    private static PhotoSyncBoundary ourInstance = new PhotoSyncBoundary();
    private Retrofit retrofit;
    private PhotoSyncService photoSyncService;

    private HttpUrl baseUrl;

    private String default_ip;
    private String default_port;

    public static PhotoSyncBoundary getInstance() {
        return ourInstance;
    }

    private PhotoSyncBoundary() {
        default_ip = AndroidUtil.ContextHandler.getMainContext().getString(R.string.default_ip);
        default_port = AndroidUtil.ContextHandler.getMainContext().getString(R.string.default_port);

        setBaseUrl();
        rebuildRetrofit();
    }

    public void setBaseUrl() {
        String ip = SharedPreferencesUtil.getAnyValue(SettingsFragment.KEY_PREF_SERVER_IP);
        String port = SharedPreferencesUtil.getAnyValue(SettingsFragment.KEY_PREF_SERVER_PORT);

        if (AndroidUtil.isPortInvalid(port)) {
            Logger.getInstance().appendLog("Invalid port: " + port + "\nUse default port now");
            port = default_port;
        }
        if (AndroidUtil.isIpInvalid(ip)) {
            Logger.getInstance().appendLog("Invalid ip: " + ip + "\nUse default ip now");
            ip = default_ip;
        }

        // Must be like this: https://192.168.178.46:8443
        baseUrl = new HttpUrl.Builder()
                .scheme("https")
                .host(ip)
                .port(Integer.parseInt(port))
                .build();
    }

    private void rebuildRetrofit() {
        OkHttpClient client = null;
        try {
            client = HttpClientFactory.handleCert(AndroidUtil.ContextHandler.getMainContext());
        } catch (Exception e) {
            Logger.getInstance().appendLog(e.getMessage());

        }
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        photoSyncService = retrofit.create(PhotoSyncService.class);
    }

//    /**
//     * Call this before doing any REST call, otherwise -> CertPathValidatorException
//     * Recently this is called in MainActivity (because of Context needed)
//     *
//     * @param client
//     */
//    public void setClient(OkHttpClient client) {
//        setBaseUrl();
//
//        retrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .client(client)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//
//        photoSyncService = retrofit.create(PhotoSyncService.class);
//    }

    public void getAllFolders() throws IOException {
        setBaseUrl();
        rebuildRetrofit();

        photoSyncService.getAllFolders().enqueue(new Callback<List<FolderTO>>() {
            @Override
            public void onResponse(Call<List<FolderTO>> call, Response<List<FolderTO>> response) {
                if (response.isSuccessful()) {

                    // Converting FolderTO List to Folder List and add to ServerDataContentHandler
                    List<FolderTO> folderTOList = response.body();
                    List<Folder> folderList = new ArrayList<>();

                    for (FolderTO folderTO : folderTOList) {
                        folderList.add(convertFolderTOtoFolder(folderTO));
                    }

                    ServerDataContentHandler.getInstance().setFolders(folderList);
                } else {
                    String message = "Response unsuccessful (" + response.code() + "): " + response.message();
                    Logger.getInstance().appendLog(message);
                }
            }

            @Override
            public void onFailure(Call<List<FolderTO>> call, Throwable t) {
                Logger.getInstance().appendLog("Response failed (Caused by Client): " + t.getMessage());
            }
        });
    }

    public void addFolder(Folder folder) {
        setBaseUrl();
        rebuildRetrofit();

        FolderTO folderTO = convertFolderToFolderTO(folder);

        photoSyncService.createFolder(folderTO).enqueue(new Callback<Folder>() {
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
                Logger.getInstance().appendLog("Response failed (Caused by Client): " + t.getMessage());
            }
        });
    }

    public void uploadPicture(Picture pic) {
        setBaseUrl();
        rebuildRetrofit();

        PictureTO picTO = convertPictureToPictureTO(pic);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pic.getAbsolutePath());
        MultipartBody.Part body = MultipartBody.Part.createFormData(picTO.getName(), picTO.getName(), requestFile);
        String folderToPutInString = pic.getAbsolutePath().getParentFile().getName();
        RequestBody folderToPutIn = RequestBody.create(MediaType.parse("multipart/form-data"), folderToPutInString);

        photoSyncService.uploadPicture(folderToPutIn, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Logger.getInstance().appendLog("Post successful");
                } else {
                    String message = "Response unsuccessful (" + response.code() + "): " + response.message();
                    Logger.getInstance().appendLog(message);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.getInstance().appendLog("Response failed (Caused by Client): " + t.getMessage());
            }

        });
    }
}
