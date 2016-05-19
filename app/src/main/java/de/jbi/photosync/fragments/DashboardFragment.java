package de.jbi.photosync.fragments;

/**
 * Created by Jan on 14.05.2016.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import de.jbi.photosync.R;
import de.jbi.photosync.content.DataContentHandler;
import de.jbi.photosync.content.SharedPreferencesUtil;
import de.jbi.photosync.http.PhotoSyncBoundary;
import de.jbi.photosync.utils.Logger;

import static de.jbi.photosync.content.DataContentHandler.getInstance;

public class DashboardFragment extends Fragment implements Observer{
    private Activity activity;
    private Context ctx;
    private View rootView;

    public static final String ARG_FRAGMENT_NUMBER = "fragment_number";

    public DashboardFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        activity = getActivity();
        ctx = activity.getApplicationContext();

        setUIAndContents();

        return rootView;
    }


    private void setUIAndContents() {
        DataContentHandler dataContentHandler = getInstance();

        TextView selectedFoldersInfoTV = (TextView) rootView.findViewById(R.id.totalSelectedFoldersInfoTextView);
        selectedFoldersInfoTV.append(Integer.toString(dataContentHandler.getFolders().size()));

        TextView totalFileAmountTV = (TextView) rootView.findViewById(R.id.totalFileAmountTextView);
        TextView lastSyncTV = (TextView) rootView.findViewById(R.id.lastSyncTextView);
        Button syncBtn = (Button) rootView.findViewById(R.id.syncButton);

        totalFileAmountTV.append(Integer.toString(dataContentHandler.getTotalAmountOfFiles()));
        lastSyncTV.append(SharedPreferencesUtil.getMetaData(ctx));

        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO add update of lastsync text view
                handleSync();
            }
        });

    }

    private void handleSync() {
        SharedPreferencesUtil.addMetaData(ctx);

//        PhotoSyncBoundary.getInstance().addFolder(MockFolder.mockFolderA);

        try {
            PhotoSyncBoundary.getInstance().getAllFolders();
        } catch (IOException e) {
            String logMessage = "Unhandled exception: " + e.getMessage();
            Logger.getInstance().appendLog(logMessage);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
    }
}
