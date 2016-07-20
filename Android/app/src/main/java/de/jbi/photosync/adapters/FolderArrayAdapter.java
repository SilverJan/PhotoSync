package de.jbi.photosync.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.jbi.photosync.R;
import de.jbi.photosync.activities.EditFolderActivity;
import de.jbi.photosync.content.DataContentHandler;
import de.jbi.photosync.content.SharedPreferencesUtil;
import de.jbi.photosync.domain.Folder;
import de.jbi.photosync.utils.AndroidUtil;
import de.jbi.photosync.utils.Constants;

import static de.jbi.photosync.content.DataContentHandler.getInstance;
import static de.jbi.photosync.utils.AndroidUtil.humanReadableByteCount;
import static de.jbi.photosync.utils.BackupAgent.requestAppBackup;

/**
 * Created by Jan on 14.05.2016.
 */
public class FolderArrayAdapter extends ArrayAdapter {
    private ArrayList<Folder> folders;
    private Context ctx;

    public FolderArrayAdapter(Context context, int resource, ArrayList<Folder> folders) {
        super(context, resource, folders);
        this.folders = folders;
        this.ctx = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_folder_item, null);
        }

        // ##########################
        // ### SET FOLDER DETAILS ###
        // ##########################

        TextView fileNameTV = (TextView) view.findViewById(R.id.folderNameTextView);
        TextView fileAmountTV = (TextView) view.findViewById(R.id.folderAmountTextView);
        TextView fileSizeTV = (TextView) view.findViewById(R.id.folderSizeTextView);
        ImageButton fileRemoveButton = (ImageButton) view.findViewById(R.id.folderRemoveButton);

        final Folder positionedFolder = folders.get(position);

        fileNameTV.setText(positionedFolder.getName());
        fileAmountTV.setText(Integer.toString(positionedFolder.getChildAmount()));
        fileSizeTV.setText(AndroidUtil.humanReadableByteCount(positionedFolder.getSize(), true));

        // #######################
        // ### CLICK LISTENERS ###
        // #######################

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent startActivityIntent = new Intent(getContext(), EditFolderActivity.class).putExtra(Constants.PASS_FOLDER_INTENT, positionedFolder.getId().toString()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(startActivityIntent);

                return true;
            }
        });

        fileRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRemoveFolder(position);
            }
        });

        return view;
    }

    private void handleRemoveFolder(int position) {
        DataContentHandler dataContentHandler = getInstance();
        Folder folderToBeRemoved = dataContentHandler.getFolders().get(position);

        dataContentHandler.removeFolder(folderToBeRemoved);
        SharedPreferencesUtil.removeFolder(folderToBeRemoved);
        requestAppBackup();

        this.refreshAdapterAndLoadData();
    }

    /**
     * Clears the binded data, reloads it and refreshes the UI
     */
    public void refreshAdapterAndLoadData() {
        this.clear();
        this.addAll(SharedPreferencesUtil.getFolders());
        this.notifyDataSetChanged();
    }
}
