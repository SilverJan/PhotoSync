package de.jbi.photosync.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import de.jbi.photosync.R;
import de.jbi.photosync.adapters.FolderArrayAdapter;
import de.jbi.photosync.content.DataContentHandler;
import de.jbi.photosync.content.SharedPreferencesUtil;
import de.jbi.photosync.utils.FileChooser;
import de.jbi.photosync.utils.Folder;

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
        setDetailsListViewClickListener();
        setAddFolderBtnClickListener();
        handleTemplateRow();

        return rootView;
    }

    /**
     * Set adapter and get initial folder selection
     */
    private void setFolderContainerInit() {
        ArrayList<Folder> allSelectedFolders = (ArrayList) dataContentHandler.getSelectedFolders();

        folderArrayAdapter = new FolderArrayAdapter(ctx, R.layout.list_folder_item, allSelectedFolders);
        ListView folderListView = (ListView) rootView.findViewById(R.id.folderContainerListView);
        folderListView.setAdapter(folderArrayAdapter);
    }

    private void setDetailsListViewClickListener() {
        ListView folderListView = (ListView) rootView.findViewById(R.id.folderContainerListView);
        folderListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                handleLongClick(position);
                return true;
            }
        });
    }

    /**
     * Make a toast with absolute path
     *
     * @param position
     */
    private void handleLongClick(int position) {
        Toast.makeText(ctx, dataContentHandler.getSelectedFolders().get(position).getAbsolutePath().getPath(), Toast.LENGTH_SHORT).show();
    }

    private void setAddFolderBtnClickListener() {
        Button addBtn = (Button) rootView.findViewById(R.id.addFolderButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddFolder();
            }
        });
    }

    /**
     * Opens a file chooser and adds a folder to the persistent storage
     */
    private void handleAddFolder() {
        new FileChooser(activity).setFileListener(new FileChooser.FileSelectedListener() {
            @Override
            public void fileSelected(final File file) {
                File parentFile = file.getParentFile();
                Folder folderToAdd = new Folder(parentFile, true);
                try {
                    dataContentHandler.addSelectedFolder(folderToAdd);
                    SharedPreferencesUtil.addFolder(ctx, folderToAdd);

                    // Workaround for adapter, Observer would be cooler
                    folderArrayAdapter.clear();
                    folderArrayAdapter.addAll(SharedPreferencesUtil.getFolders(ctx));

                } catch (IllegalArgumentException ex) {
                    Toast.makeText(ctx, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                folderArrayAdapter.notifyDataSetChanged();
            }
        }).showDialog();
    }

    /**
     * Remove functions from template row and add remove all folders to btn
     */
    private void handleTemplateRow() {
        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.infoTemplate);
        Button templateRemoveBtn = (Button) ll.findViewById(R.id.folderRemoveButton);
        templateRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.cleanFolderStorage(ctx);

                // Workaround for adapter
                folderArrayAdapter.clear();
                dataContentHandler.setSelectedFolders(SharedPreferencesUtil.getFolders(ctx));

                folderArrayAdapter.notifyDataSetChanged();
            }
        });
        templateRemoveBtn.setText("REMOVE ALL");
    }
}
