package de.jbi.photosync.http;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.net.SocketTimeoutException;
import java.util.Queue;

import javax.net.ssl.SSLException;

import de.jbi.photosync.content.ContentUtil;
import de.jbi.photosync.domain.PictureVideo;
import de.jbi.photosync.domain.PictureVideoTO;
import de.jbi.photosync.utils.Constants;
import de.jbi.photosync.utils.Logger;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static de.jbi.photosync.domain.TOUtil.convertPictureToPictureTO;


public class FileUploadIntentService extends IntentService {
    public static final String PROGRESS_TAG = "progress_of_sync";

    private Queue<PictureVideo> pictureVideoQueueComplete;
    private int max;
    private int tries = 0;
    public static volatile boolean cancelled = false;

    public FileUploadIntentService() {
        super("FileUploadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        cancelled = false;
        final Queue<PictureVideo> picVidQueue = ContentUtil.getNewFilesToUploadQueue(); // That is made 2 times, once here and once in DashboardFragment TODO -> Refactor it

        if (picVidQueue.size() == 0) {
            throw new IllegalStateException("This cannot happen. The IntentService should only be called, when there are files to synchronize!");
        }
        pictureVideoQueueComplete = picVidQueue;
        max = pictureVideoQueueComplete.size();
        uploadFiles();
    }

    private void uploadFiles() {
        if (pictureVideoQueueComplete.size() > 0 && !cancelled) {
            final PictureVideo picVid = pictureVideoQueueComplete.element();
            PictureVideoTO picTO = convertPictureToPictureTO(picVid);

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), picVid.getAbsolutePath());
            MultipartBody.Part body = MultipartBody.Part.createFormData(picTO.getName(), picTO.getName(), requestFile);
            String folderToPutInString = picVid.getAbsolutePath().getParentFile().getName();
            RequestBody folderToPutIn = RequestBody.create(MediaType.parse("multipart/form-data"), folderToPutInString);

            PhotoSyncBoundary.getInstance().getPhotoSyncService().uploadPictureVideo(folderToPutInString, folderToPutIn, body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Logger.getInstance().appendLog("Post successful", false);
                        pictureVideoQueueComplete.remove();
                        publishProgress(max - pictureVideoQueueComplete.size());
                        uploadFiles();
                    } else {
                        String message = "Response unsuccessful (" + response.code() + "): " + response.message();
                        Logger.getInstance().appendLog(message, false);
                        onCancel();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Logger.getInstance().appendLog("Response failed (Caused by Client): " + t.getMessage(), false);

                    if (t instanceof SocketTimeoutException || t instanceof SSLException && tries < 3) {
                        tries++;
                        PictureVideo toBeRequeued = pictureVideoQueueComplete.poll();
                        pictureVideoQueueComplete.add(toBeRequeued);
                        uploadFiles();
                    } else {
                        onCancel();
                    }
                }
            });
        }
    }

    /**
     * Sends a local broadcast with the progress value
     *
     * @param progress
     */
    private void publishProgress(int progress) {
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
        localIntent.putExtra(PROGRESS_TAG, progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void onCancel() {
        cancelled = true;
    }
}
