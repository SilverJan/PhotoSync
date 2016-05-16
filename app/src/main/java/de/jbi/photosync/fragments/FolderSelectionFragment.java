package de.jbi.photosync.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.jbi.photosync.R;
import de.jbi.photosync.adapters.FolderArrayAdapter;
import de.jbi.photosync.content.DataContentHandler;
import de.jbi.photosync.utils.Folder;
import de.jbi.photosync.utils.MockFolder;

/**
 * Created by Jan on 14.05.2016.
 */
public class FolderSelectionFragment extends Fragment {
    private Activity activity;
    private Context ctx;
    private View rootView;
    private FolderArrayAdapter folderArrayAdapter;
    private DataContentHandler dataContentHandler;

    public FolderSelectionFragment() {
        // Empty constructor required for fragment subclasses
    }

    // TODO add folder selection handling

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_folder_selection, container, false);

        activity = getActivity();
        ctx = activity.getApplicationContext();
        dataContentHandler = DataContentHandler.getInstance();

        setFolderContainerInit();
        setAddFolderBtnClickListener();

        return rootView;
    }

    private void setFolderContainerInit() {
        ArrayList<Folder> allSelectedFolders = (ArrayList) dataContentHandler.getSelectedFolders();

        folderArrayAdapter = new FolderArrayAdapter(ctx, R.layout.list_folder_item, allSelectedFolders);
        ListView folderListView = (ListView) rootView.findViewById(R.id.folderContainerListView);
        folderListView.setAdapter(folderArrayAdapter);
    }

    private void setAddFolderBtnClickListener() {
        Button addBtn = (Button) rootView.findViewById(R.id.addFolderButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dataContentHandler.addSelectedFolder(MockFolder.mockFolderB);
                } catch (IllegalArgumentException ex) {
                    Toast.makeText(ctx, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                folderArrayAdapter.notifyDataSetChanged();
            }
        });
    }
}
