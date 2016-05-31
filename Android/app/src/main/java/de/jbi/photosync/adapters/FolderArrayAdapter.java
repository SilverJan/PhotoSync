package de.jbi.photosync.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.jbi.photosync.R;
import de.jbi.photosync.content.DataContentHandler;
import de.jbi.photosync.content.SharedPreferencesUtil;
import de.jbi.photosync.domain.Folder;

import static de.jbi.photosync.content.DataContentHandler.getInstance;

/**
 * Created by Jan on 14.05.2016.
 */
public class FolderArrayAdapter extends ArrayAdapter {
    private ArrayList<Folder> folders;

    public FolderArrayAdapter(Context context, int resource, ArrayList<Folder> folders) {
        super(context, resource, folders);
        this.folders = folders;
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

        TextView fileNameTV = (TextView) view.findViewById(R.id.folderFileNameTextView);
        TextView fileAmountTV = (TextView) view.findViewById(R.id.folderFileAmountTextView);
        Button fileRemoveButton = (Button) view.findViewById(R.id.folderRemoveButton);

        Folder positionedFolder = folders.get(position);

        fileNameTV.setText(positionedFolder.getName());
        fileAmountTV.setText(Integer.toString(positionedFolder.getChildAmount()));

        // #######################
        // ### CLICK LISTENERS ###
        // #######################

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Folder folder = getInstance().getFolders().get(position);
                String details = "Path: " + folder.getAbsolutePath().getPath() + "\n"
                        + "Size (File Bytes): " + folder.getSize() + "\n"
                        + "Size (Human readable): " + humanReadableByteCount(folder.getSize(), true);
                Toast.makeText(getContext(), details, Toast.LENGTH_LONG).show();

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

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private void handleRemoveFolder(int position) {
        DataContentHandler dataContentHandler = getInstance();
        Folder folderToBeRemoved = dataContentHandler.getFolders().get(position);

        dataContentHandler.removeFolder(folderToBeRemoved);
        SharedPreferencesUtil.removeFolder(folderToBeRemoved);

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
