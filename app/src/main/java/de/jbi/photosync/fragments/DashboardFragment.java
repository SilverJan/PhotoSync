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
import android.widget.TextView;

import de.jbi.photosync.R;
import de.jbi.photosync.content.DataContentHandler;

import static de.jbi.photosync.content.DataContentHandler.getInstance;

/**
 * Fragment that appears in the "frame_container", shows an empty fragment
 */
public class DashboardFragment extends Fragment {
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
//        int i = getArguments().getInt(ARG_FRAGMENT_NUMBER);

        activity = getActivity();
        ctx = activity.getApplicationContext();

        setInfos();

        return rootView;
    }

    private void setInfos() {
        DataContentHandler dataContentHandler = getInstance();

        TextView selectedFoldersInfoTV = (TextView) rootView.findViewById(R.id.selectedFoldersInfoTextView);
        selectedFoldersInfoTV.append(Integer.toString(dataContentHandler.getSelectedFolders().size()));

        TextView fileDirTV = (TextView) rootView.findViewById(R.id.fileDirTextView);

        TextView lastSyncTV = (TextView) rootView.findViewById(R.id.lastSyncTextView);

    }
}
