package de.jbi.photosync.http;

import android.util.Log;

import com.google.gson.JsonSyntaxException;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

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
@SuppressWarnings("unchecked")
public class PhotoSyncBoundary {
    private static PhotoSyncBoundary ourInstance = new PhotoSyncBoundary();
    private Retrofit retrofit;
    private PhotoSyncService photoSyncService;

    private HttpUrl baseUrl;

    private String default_ip;
    private String default_port;

    private int tries = 0;

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
            Logger.getInstance().appendLog("Invalid port: " + port + "\nUse default port now", true);
            port = default_port;
        }
        if (AndroidUtil.isIpInvalid(ip)) {
            Logger.getInstance().appendLog("Invalid ip: " + ip + "\nUse default ip now", true);
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
            Logger.getInstance().appendLog(e.getMessage(), true);

        }
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        photoSyncService = retrofit.create(PhotoSyncService.class);
    }

    /**
     * Return a configured instance of PhotoSyncService
     * @return
     */
    public PhotoSyncService getPhotoSyncService() {
        setBaseUrl();
        rebuildRetrofit();

        return photoSyncService;
    }

    public Promise getAllFoldersAsync() throws IOException {
        setBaseUrl();
        rebuildRetrofit();

        final Deferred deferred = new DeferredObject();
        Promise promise = deferred.promise();

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
                    deferred.resolve("done");

                } else {
                    String message = "Response unsuccessful (" + response.code() + "): " + response.message();
                    Logger.getInstance().appendLog(message, true);

                    deferred.reject("fail");
                }
            }

            @Override
            public void onFailure(Call<List<FolderTO>> call, Throwable t) {
                Logger.getInstance().appendLog("Response failed (Caused by Client): " + t.getMessage(), true);
                deferred.reject(t);
            }
        });

        return promise;
    }

    public void addFolderAsync(Folder folder) {
        setBaseUrl();
        rebuildRetrofit();

        FolderTO folderTO = convertFolderToFolderTO(folder);

        photoSyncService.createFolder(folderTO).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Logger.getInstance().appendLog("Post successful", false);
                } else {
                    String message = "Response unsuccessful (" + response.code() + "): " + response.message();
                    Logger.getInstance().appendLog(message, true);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof JsonSyntaxException) {
                    Logger.getInstance().appendLog("Post successful", false);
                    return;
                }
                Logger.getInstance().appendLog("Response failed (Caused by Client): " + t.getMessage(), true);
            }
        });
    }

    /**
     * Upload one picture to the server async
     * @param pic
     * @return
     */
    public Promise uploadPictureAsync(Picture pic) {
        setBaseUrl();
        rebuildRetrofit();

        final Deferred deferred = new DeferredObject();
        Promise promise = deferred.promise();

        PictureTO picTO = convertPictureToPictureTO(pic);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pic.getAbsolutePath());
        MultipartBody.Part body = MultipartBody.Part.createFormData(picTO.getName(), picTO.getName(), requestFile);
        String folderToPutInString = pic.getAbsolutePath().getParentFile().getName();
        RequestBody folderToPutIn = RequestBody.create(MediaType.parse("multipart/form-data"), folderToPutInString);

        photoSyncService.uploadPicture(folderToPutInString, folderToPutIn, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Logger.getInstance().appendLog("Post successful", false);
                    deferred.resolve("");
                } else {
                    String message = "Response unsuccessful (" + response.code() + "): " + response.message();
                    Logger.getInstance().appendLog(message, false);
                    deferred.reject(message);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.getInstance().appendLog("Response failed (Caused by Client): " + t.getMessage(), false);

                if (t instanceof SocketTimeoutException || tries < 3) {
                    call.clone().enqueue(this);
                    tries++;
                } else {
                    deferred.reject(t);
                }
            }
        });
        return promise;
    }


//    public Promise uploadPictureListAsync(List<Picture> pictureList) throws ExecutionException, InterruptedException {
//        progressReceived = 0;
//        progressSent = 0;
//
//        final Deferred deferred = new DeferredObject();
//        Promise promise = deferred.promise();
//
//        final int sum = pictureList.size();
//
//        if (sum > 50) {
//
//        } else {
//            for (Picture pic : pictureList) {
//                // Do this with AsyncTasks because of Thread_Pool_Executor -> Better handling of many async calls
//                PhotoUploadAsyncTask asyncTask = new PhotoUploadAsyncTask();
//                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pic)
//                        .get()
//                        .done(new DoneListCallback(deferred, progressReceived, sum))
//                        .fail(new FailCallback() {
//                                  @Override
//                                  public void onFail(Object result) {
//                                      deferred.reject(result);
//                                  }
//                              }
//                        );
//            }
//        }
//        return promise;

    /**
     * Divides a list of pictures into smaller lists.
     *
     * @param sourceList The source list to be divided
     * @param div        The factor of division
     * @return
     */
    private List<List<Picture>> divideListInPieces(List<Picture> sourceList, int div) {
        List<List<Picture>> listCollector = new ArrayList<>();

        for (int i = 0; i != sourceList.size(); i += div) {
            List<Picture> dividedPicList;
            if (i + div > sourceList.size()) {
                dividedPicList = sourceList.subList(i, sourceList.size());
                listCollector.add(dividedPicList);
                break;
            } else {
                dividedPicList = sourceList.subList(i, i + 50);
                listCollector.add(dividedPicList);
            }
        }
        return listCollector;
    }
}
