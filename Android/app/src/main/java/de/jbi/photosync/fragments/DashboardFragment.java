package de.jbi.photosync.fragments;

/**
 * Created by Jan on 14.05.2016.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

import javax.net.ssl.SSLException;

import de.jbi.photosync.R;
import de.jbi.photosync.content.ContentUtil;
import de.jbi.photosync.content.DataContentHandler;
import de.jbi.photosync.content.SharedPreferencesUtil;
import de.jbi.photosync.domain.PictureVideo;
import de.jbi.photosync.domain.PictureVideoTO;
import de.jbi.photosync.http.FileUploadIntentService;
import de.jbi.photosync.http.PhotoSyncBoundary;
import de.jbi.photosync.utils.Constants;
import de.jbi.photosync.utils.Logger;
import de.jbi.photosync.utils.NotificationFactory;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static de.jbi.photosync.content.DataContentHandler.getInstance;
import static de.jbi.photosync.content.SharedPreferencesUtil.META_LAST_SYNC_MISSING_FILES;
import static de.jbi.photosync.content.SharedPreferencesUtil.META_LAST_SYNC_NEW_FILES;
import static de.jbi.photosync.content.SharedPreferencesUtil.META_LAST_SYNC_NEW_FOLDERS;
import static de.jbi.photosync.content.SharedPreferencesUtil.META_LAST_SYNC_TIME;
import static de.jbi.photosync.domain.TOUtil.convertPictureToPictureTO;
import static de.jbi.photosync.utils.AndroidUtil.humanReadableByteCount;

public class DashboardFragment extends Fragment implements Observer {
    private Activity activity;
    private Context ctx;
    private View rootView;

    public static final String ARG_FRAGMENT_NUMBER = "fragment_number";
    private TextView selectedFoldersInfoTV;
    private TextView totalFileAmountTV;
    private TextView lastSyncTV;
    private TextView newFoldersInfoTV;
    private TextView newFileAmountInfoTV;
    private Button syncBtn;

    private ProgressDialog syncInfoDialog;
    private ProgressDialog progressDialog;
    private boolean isFragmentVisible;

    private Queue<PictureVideo> completePicsVidsToUploadQueue;
    private int maxFilesToUpload;
    private Integer missingFilesToUpload;
    private Boolean aborted;

    DataContentHandler dataContentHandler = getInstance();

    public DashboardFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        activity = getActivity();
        ctx = activity.getApplicationContext();
        syncInfoDialog = new ProgressDialog(activity);
        progressDialog = new ProgressDialog(activity);

        setUI();
        setUIContents();
        registerBroadcastReceiver();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUIContents();
        isFragmentVisible = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;
    }


    private void setUI() {
        selectedFoldersInfoTV = (TextView) rootView.findViewById(R.id.fragment_dashboard_totalSelectedFoldersInfoTextView);
        totalFileAmountTV = (TextView) rootView.findViewById(R.id.fragment_dashboard_totalFileAmountTextView);
        newFoldersInfoTV = (TextView) rootView.findViewById(R.id.fragment_dashboard_newSelectedFoldersInfoTextView);
        newFileAmountInfoTV = (TextView) rootView.findViewById(R.id.fragment_dashboard_newFileAmountTextView);
        lastSyncTV = (TextView) rootView.findViewById(R.id.fragment_dashboard_lastSyncTextView);
        syncBtn = (Button) rootView.findViewById(R.id.fragment_dashboard_syncButton);

        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO add update of lastsync text view

                handleSync();
            }
        });
    }

    private void setUIContents() {
        selectedFoldersInfoTV.setText(getString(R.string.fragment_dashboard_total_selected_folders_info));
        selectedFoldersInfoTV.append(Integer.toString(dataContentHandler.getFolders().size()));
        totalFileAmountTV.setText(getString(R.string.fragment_dashboard_total_amount_of_files));
        totalFileAmountTV.append(Integer.toString(dataContentHandler.getTotalAmountOfFiles()));
        refreshDynamicUI();
    }

    @SuppressWarnings("unchecked")
    private void handleSync() {
        syncInfoDialog.setTitle(getResources().getString(R.string.fragment_dashboard_syncing_progressbar_title));
        syncInfoDialog.setMessage("Loading folders from server..");
        syncInfoDialog.setCancelable(false);
        syncInfoDialog.show();

        try {
            // STEP 1: Get all folders from server and store in ServerDataContentHandler
            PhotoSyncBoundary.getInstance()
                    .getAllFoldersAsync()
                    .done(new DoneCallback() {
                        @Override
                        public void onDone(Object result) {
                            pushSyncBroadcast();
                        }
                    })
                    .fail(new FailCallback() {
                        @Override
                        public void onFail(Object result) {
                            handleHttpError((Exception) result);
                        }
                    });
        } catch (Exception e) {
            handleHttpError(e);
        }
    }

    /**
     * Refreshes the texts of all dynamic views on screen, like last sync, new files since last sync, ...
     */
    private void refreshDynamicUI() {
        Map<String, String> lastSyncMeta = SharedPreferencesUtil.getMetaData();
        lastSyncTV.setText(getString(R.string.fragment_dashboard_last_sync));
        lastSyncTV.append(lastSyncMeta.get(META_LAST_SYNC_TIME));

        newFoldersInfoTV.setText(getString(R.string.fragment_dashboard_new_folders_since_last_sync));
        Integer folderDiff = dataContentHandler.getFolders().size() - Integer.parseInt(lastSyncMeta.get(META_LAST_SYNC_NEW_FOLDERS));
        newFoldersInfoTV.append(folderDiff.toString());

        // Last time: 10 files - Now: 15 files + 5 missing files from last sync = 10 new files, also negative possible
        newFileAmountInfoTV.setText(getString(R.string.fragment_dashboard_new_files_since_last_sync));
        Integer filesDiff = (dataContentHandler.getTotalAmountOfFiles() - Integer.parseInt(lastSyncMeta.get(META_LAST_SYNC_NEW_FILES))) + Integer.parseInt(lastSyncMeta.get(META_LAST_SYNC_MISSING_FILES));
        newFileAmountInfoTV.append(filesDiff.toString());
    }

    /**
     * Helper class for picture upload
     */
    private class PhotoUploadAsyncTask extends AsyncTask<Queue<PictureVideo>, Integer, Void> {
        private int max;
        private Integer missing = null;
        private int tries = 0;
        private boolean aborted = false;
        private Queue<PictureVideo> pictureVideoQueueComplete;

        @Override
        protected void onPreExecute() {
            max = syncInfoDialog.getMax();
            progressDialog.setTitle(getResources().getString(R.string.fragment_dashboard_syncing_progressbar_title));
            progressDialog.setMessage(activity.getString(R.string.fragment_dashboard_progress_dialog_message));
            progressDialog.setMax(max);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    aborted = true;
                    cancel(true);
                    onCancelled();
                }
            });
            progressDialog.show();
            syncInfoDialog.dismiss();
        }

        @Override
        protected Void doInBackground(final Queue<PictureVideo>... params) {
            if (isCancelled()) {
                return null;
            }
            final Queue<PictureVideo> picVidQueue = params[0];
            if (pictureVideoQueueComplete == null) {
                pictureVideoQueueComplete = picVidQueue;
            }

            if (picVidQueue.size() > 0) {
                final PictureVideo picVid = picVidQueue.element();
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
                            picVidQueue.remove();
                            publishProgress(max - picVidQueue.size());
                            doInBackground(picVidQueue);
                        } else {
                            String message = "Response unsuccessful (" + response.code() + "): " + response.message();
                            Logger.getInstance().appendLog(message, false);
                            cancel(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Logger.getInstance().appendLog("Response failed (Caused by Client): " + t.getMessage(), false);

                        if (t instanceof SocketTimeoutException || t instanceof SSLException && tries < 3) {
                            tries++;
                            PictureVideo toBeRequeued = picVidQueue.poll();
                            picVidQueue.add(toBeRequeued);
                            doInBackground(picVidQueue);
                        } else {
                            cancel(true);
                        }
                    }
                });
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(progress[0]);
            missing = max - progress[0];

            if (progress[0] >= max) {
                progressDialog.dismiss();
                Logger.getInstance().appendLog("Sync successful!", true);
                SharedPreferencesUtil.addMetaData(new Date(System.currentTimeMillis()), dataContentHandler.getFolders().size(), dataContentHandler.getTotalAmountOfFiles(), 0);
                refreshDynamicUI();
                return;
            }
            PictureVideo nextToUpload = pictureVideoQueueComplete.peek();
            progressDialog.setMessage(activity.getString(R.string.fragment_dashboard_progress_dialog_message) + "\n\nFile: " + nextToUpload.getName() + "(" + humanReadableByteCount(nextToUpload.getSize(), true) + ")");
        }

        @Override
        protected void onCancelled() {
            // This is called instead of onPostExecute(), when cancel(true) -> Actually it is called in progressDialog cancel button onClickHandler
            progressDialog.dismiss();
            if (missing == null) {
                // happens, when cancelled unfortunately after start sync. There is a chance that pictureVideoQueueComplete is null but this is unlikely
                missing = pictureVideoQueueComplete.size() - 1; // minus 1 because post was already made
            }
            if (aborted && missing > 0) {
                Logger.getInstance().appendLog("Sync cancelled by user. " + missing + " files missing!", true);
            } else if (aborted) {
                // happens, when cancelled right at the end or in the beginning
                Logger.getInstance().appendLog("Sync successful!", true);
            } else {
                Logger.getInstance().appendLog("Sync failed!", true);
            }
            SharedPreferencesUtil.addMetaData(new Date(System.currentTimeMillis()), dataContentHandler.getFolders().size(), dataContentHandler.getTotalAmountOfFiles(), missing);
            refreshDynamicUI();
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter statusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);

        LocalBroadcastManager.getInstance(ctx).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int progress = intent.getIntExtra(FileUploadIntentService.PROGRESS_TAG, Integer.MAX_VALUE);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgress(progress);
                missingFilesToUpload = maxFilesToUpload - progress;

                if (progress >= maxFilesToUpload) {
                    progressDialog.dismiss();
                    Logger.getInstance().appendLog("Sync successful!", true);

                    SharedPreferencesUtil.addMetaData(new Date(System.currentTimeMillis()), dataContentHandler.getFolders().size(), dataContentHandler.getTotalAmountOfFiles(), 0);
                    }
                    if (isAdded()) {
                        refreshDynamicUI();
                    }
                    return;

                }
                PictureVideo nextToUpload = completePicsVidsToUploadQueue.poll();
                if (nextToUpload != null) {
                    progressDialog.setMessage(activity.getString(R.string.fragment_dashboard_progress_dialog_message)
                            + "\n\nFile: " + nextToUpload.getName()
                            + " (" + humanReadableByteCount(nextToUpload.getSize(), true) + ")"
                    );

                    if (!isFragmentVisible) {
                        NotificationFactory.notify(notificationBuilder
                                .setProgress(maxFilesToUpload, progress, false)
                                .setContentText(progress + "/" + maxFilesToUpload + " Files synced")
                                .buildNotification());
                    } else {
                        NotificationFactory.dismissNotification();
                    }
                }
            }
        }, statusIntentFilter);
    }

    private void pushSyncBroadcast() {
        syncInfoDialog.setMessage("Comparing local folders with server folders..");

        completePicsVidsToUploadQueue = ContentUtil.getNewFilesToUploadQueue();

        if (completePicsVidsToUploadQueue.size() > 0) {
//                new PhotoUploadAsyncTask().execute(completePicsVidsToUploadQueue);

            maxFilesToUpload = completePicsVidsToUploadQueue.size();
            progressDialog.setTitle(getResources().getString(R.string.fragment_dashboard_syncing_progressbar_title));
            progressDialog.setMessage(activity.getString(R.string.fragment_dashboard_progress_dialog_message));
            progressDialog.setMax(maxFilesToUpload);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FileUploadIntentService.cancelled = true;
                    aborted = true;
                    onCancelledSync();
                }
            });
            syncInfoDialog.dismiss();
            progressDialog.show();

            // STEP 8: Upload all pictures from upload list
            ctx.startService(new Intent(ctx, FileUploadIntentService.class));

        } else {
            syncInfoDialog.dismiss();
            Logger.getInstance().appendLog("Everything up-to-date!", true);
        }
    }

    private void onCancelledSync() {
        // This is called instead of onPostExecute(), when cancel(true) -> Actually it is called in progressDialog cancel button onClickHandler
        progressDialog.dismiss();
        if (missingFilesToUpload == null) {
            // happens, when cancelled unfortunately after start sync. There is a chance that pictureVideoQueueComplete is null but this is unlikely
            missingFilesToUpload = completePicsVidsToUploadQueue.size() - 1; // minus 1 because post was already made
        }
        if (aborted && missingFilesToUpload > 0) {
            Logger.getInstance().appendLog("Sync cancelled by user. " + missingFilesToUpload + " files missing!", true);
        } else if (aborted) {
            // happens, when cancelled right at the end or in the beginning
            Logger.getInstance().appendLog("Sync successful!", true);
        } else {
            Logger.getInstance().appendLog("Sync failed!", true);
        }
        SharedPreferencesUtil.addMetaData(new Date(System.currentTimeMillis()), dataContentHandler.getFolders().size(), dataContentHandler.getTotalAmountOfFiles(), missingFilesToUpload);
        refreshDynamicUI();
    }

    private void handleHttpError(Exception e) {
        syncInfoDialog.dismiss();
        Logger.getInstance().appendLog("Unhandled exception: " + e.getMessage(), true);
    }

    @Override
    public void update(Observable observable, Object data) {
    }
}
