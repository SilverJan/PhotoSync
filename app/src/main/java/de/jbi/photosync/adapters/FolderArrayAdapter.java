package de.jbi.photosync.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import de.jbi.photosync.R;
import de.jbi.photosync.utils.Folder;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_folder_item, null);
        }

        TextView fileNameTV = (TextView) view.findViewById(R.id.folderTableFileNameTextView);
        TextView filePathTV = (TextView) view.findViewById(R.id.folderTableFilePathTextView);
        TextView fileSizeTV = (TextView) view.findViewById(R.id.folderTableSizeTextView);
        Button fileRemoveButton = (Button) view.findViewById(R.id.folderTableRemoveButton);

        Folder positionedFolder = folders.get(position);

        fileNameTV.setText(positionedFolder.getName());
        filePathTV.setText(positionedFolder.getAbsolutePath().getAbsolutePath());
        fileSizeTV.setText(positionedFolder.getContentAmount());

        // TODO add remove button handling

        return view;
    }
}
