package de.jbi.photosync.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import de.jbi.photosync.R;
import de.jbi.photosync.adapters.FolderArrayAdapter;
import de.jbi.photosync.content.DataContentHandler;
import de.jbi.photosync.utils.Folder;

/**
 * Created by Jan on 14.05.2016.
 */
public class FolderSelectionFragment extends Fragment {
    private Activity activity;
    private Context ctx;
    private View rootView;

    private DataContentHandler dataContentHandler;

    public FolderSelectionFragment() {
        // Empty constructor required for fragment subclasses
    }

    // TODO add folder selection handling

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_device_info, container, false);

        activity = getActivity();
        ctx = activity.getApplicationContext();
        dataContentHandler = DataContentHandler.getInstance();

        setTableView();

        return rootView;
    }

    private void setTableView() {
        ArrayList<Folder> allSelectedFolders = (ArrayList) dataContentHandler.getSelectedFolders();

        FolderArrayAdapter folderArrayAdapter = new FolderArrayAdapter(ctx, R.layout.list_photo_item, allSelectedFolders);
        ListView folderListView = (ListView) rootView.findViewById(R.id.folderListView);
        folderListView.setAdapter(folderArrayAdapter);

    }
}
