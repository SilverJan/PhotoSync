package de.jbi.photosync.fragments;

/**
 * Created by Jan on 14.05.2016.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

import de.jbi.photosync.R;
import de.jbi.photosync.content.DataContentHandler;
import de.jbi.photosync.content.ServerDataContentHandler;
import de.jbi.photosync.content.SharedPreferencesUtil;
import de.jbi.photosync.domain.Folder;
import de.jbi.photosync.domain.Picture;
import de.jbi.photosync.domain.PictureTO;
import de.jbi.photosync.http.PhotoSyncBoundary;
import de.jbi.photosync.utils.Logger;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static de.jbi.photosync.content.DataContentHandler.getInstance;
import static de.jbi.photosync.domain.TOUtil.convertPictureToPictureTO;

public class DashboardFragment extends Fragment implements Observer {
    private Activity activity;
    private Context ctx;
    private View rootView;

    public static final String ARG_FRAGMENT_NUMBER = "fragment_number";
    private TextView selectedFoldersInfoTV;
    private TextView totalFileAmountTV;
    private TextView lastSyncTV;
    private Button syncBtn;
    private ProgressDialog syncInfoDialog;
    private ProgressDialog progressDialog;

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

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUIContents();
    }


    private void setUI() {
        selectedFoldersInfoTV = (TextView) rootView.findViewById(R.id.totalSelectedFoldersInfoTextView);

        totalFileAmountTV = (TextView) rootView.findViewById(R.id.totalFileAmountTextView);
        lastSyncTV = (TextView) rootView.findViewById(R.id.lastSyncTextView);
        syncBtn = (Button) rootView.findViewById(R.id.syncButton);

        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO add update of lastsync text view

                handleSync();
            }
        });
    }

    private void setUIContents() {
        DataContentHandler dataContentHandler = getInstance();
        selectedFoldersInfoTV.setText(getString(R.string.fragment_dashboard_total_selected_folders_info));
        selectedFoldersInfoTV.append(Integer.toString(dataContentHandler.getFolders().size()));
        totalFileAmountTV.setText(getString(R.string.fragment_dashboard_total_amount_of_files));
        totalFileAmountTV.append(Integer.toString(dataContentHandler.getTotalAmountOfFiles()));
        lastSyncTV.setText(getString(R.string.fragment_dashboard_last_sync));
        lastSyncTV.append(SharedPreferencesUtil.getMetaData());
    }

    @SuppressWarnings("unchecked")
    private void handleSync() {
        syncInfoDialog.setTitle(getResources().getString(R.string.fragment_dashboard_syncing_progressbar_title));
        syncInfoDialog.setMessage("Loading folders from server..");
        syncInfoDialog.setCancelable(false);
        syncInfoDialog.show();

        SharedPreferencesUtil.addMetaData();

        try {
            // STEP 1: Get all folders from server and store in ServerDataContentHandler
            PhotoSyncBoundary.getInstance()
                    .getAllFoldersAsync()
                    .done(new UploadPicturesCallBack())
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
     * Helper class for picture upload
     */
    private class PhotoUploadAsyncTask extends AsyncTask<Queue<Picture>, Integer, Void> {
        private int max;
        private int tries = 0;
        private boolean aborted = false;

        @Override
        protected void onPreExecute() {
            max = syncInfoDialog.getMax();
            progressDialog.setTitle(getResources().getString(R.string.fragment_dashboard_syncing_progressbar_title));
            progressDialog.setMessage("Uploading pictures...");
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
                }
            });
            progressDialog.show();
            syncInfoDialog.dismiss();
        }

        @Override
        protected Void doInBackground(final Queue<Picture>... params) {
            if (isCancelled()) {
                return null;
            }
            final Queue<Picture> picQueue = params[0];

            if (picQueue.size() > 0) {
                Picture pic = picQueue.element();
                PictureTO picTO = convertPictureToPictureTO(pic);

                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pic.getAbsolutePath());
                MultipartBody.Part body = MultipartBody.Part.createFormData(picTO.getName(), picTO.getName(), requestFile);
                String folderToPutInString = pic.getAbsolutePath().getParentFile().getName();
                RequestBody folderToPutIn = RequestBody.create(MediaType.parse("multipart/form-data"), folderToPutInString);

                PhotoSyncBoundary.getInstance().getPhotoSyncService().uploadPicture(folderToPutInString, folderToPutIn, body).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Logger.getInstance().appendLog("Post successful", false);
                            picQueue.remove();
                            publishProgress(max - picQueue.size());
                            doInBackground(picQueue);
                        } else {
                            String message = "Response unsuccessful (" + response.code() + "): " + response.message();
                            Logger.getInstance().appendLog(message, false);
                            cancel(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Logger.getInstance().appendLog("Response failed (Caused by Client): " + t.getMessage(), false);

                        if (t instanceof SocketTimeoutException && tries < 3) {
                            tries++;
                            doInBackground(picQueue);
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

            if (progress[0] >= max) {
                progressDialog.dismiss();
                Logger.getInstance().appendLog("Sync successful!", true);
            }
        }

        @Override
        protected void onCancelled() {
            // This is called instead of onPostExecute(), when cancel(true) -> Actually it will never be called in the recent impl. But it isn't that necessary, I guess...
            progressDialog.dismiss();
            if (aborted) {
                Logger.getInstance().appendLog("Sync cancelled by user!", true);
            } else {
                Logger.getInstance().appendLog("Sync failed!", true);
            }
        }
    }

    /**
     * Class that handles the algorithm steps > 1
     */
    private class UploadPicturesCallBack implements DoneCallback {

        @SuppressWarnings("unchecked")
        @Override
        public void onDone(Object result) {
            syncInfoDialog.setMessage("Comparing local folders with server folders..");
            List<Folder> allServerFolders = ServerDataContentHandler.getInstance().getFolders();
            List<String> allServerFolderNames = Folder.getFolderNameList(allServerFolders);
            List<Picture> allPicsToUpload = new ArrayList<>();

            // STEP 2: Get all selected folders from client
            List<Folder> allClientFolders = DataContentHandler.getInstance().getFolders();

            // STEP 3: Sort client folders to a) server-existing or b) non-server-existing list
            List<Folder> completeFoldersToUpload = new ArrayList<>();
            List<Folder> incompleteFoldersToUpload = new ArrayList<>();
            for (Folder clientFolder : allClientFolders) {
                if (!allServerFolderNames.contains(clientFolder.getName())) {
                    completeFoldersToUpload.add(clientFolder);
                } else {
                    incompleteFoldersToUpload.add(clientFolder);
                }
            }

            // STEP 4: For all non-server-existing folders -> Add pictures to uploadList (missing folders will be added automatically)
            for (Folder completeFolderToUpload : completeFoldersToUpload) {
                for (Picture clientPicInNewFolder : completeFolderToUpload.getPictures()) {
                    allPicsToUpload.add(clientPicInNewFolder);
                }
            }

            // STEP 5: For all server-existing folders -> Get server-equal-folder
            for (Folder incompleteFolderToUpload : incompleteFoldersToUpload) {
                int serverEqualIndex = allServerFolderNames.indexOf(incompleteFolderToUpload.getName());
                Folder serverEqual = allServerFolders.get(serverEqualIndex);

                // STEP 6: If server and client folder are equal (same size of pics) then do nothing
                if (serverEqual.getSize() == incompleteFolderToUpload.getSize()) {
                    continue;
                } else {
                    // STEP 7: For all pictures in a server-existing folder -> If clientPic !exists (name) on server -> Add to upload list
                    List<Picture> allClientFolderPics = incompleteFolderToUpload.getPictures();
                    List<Picture> allServerFolderPics = serverEqual.getPictures();
                    List<String> allServerFolderPicsNames = Picture.getPictureNameList(allServerFolderPics);

                    for (Picture clientFolderPic : allClientFolderPics) {
                        if (!allServerFolderPicsNames.contains(clientFolderPic.getName())) {
                            allPicsToUpload.add(clientFolderPic);
                        }
                    }
                }
            }
            // STEP 8: Upload all pictures from upload list
            if (allPicsToUpload.size() > 0) {
                Queue<Picture> picturesToUploadQueue = new LinkedList<>(allPicsToUpload);

                syncInfoDialog.setMax(picturesToUploadQueue.size()); // This is a bad way to save max, refactor that!
                new PhotoUploadAsyncTask().execute(picturesToUploadQueue);
            } else {
                syncInfoDialog.dismiss();
                Logger.getInstance().appendLog("Everything up-to-date!", true);
            }
        }
    }

    private void handleHttpError(Exception e) {
        syncInfoDialog.dismiss();
        String logMessage = "Unhandled exception: " + e.getMessage();
        Logger.getInstance().appendLog(logMessage, true);
    }

    @Override
    public void update(Observable observable, Object data) {
    }
}
