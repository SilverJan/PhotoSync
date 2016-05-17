package de.jbi.photosync.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.jbi.photosync.R;
import de.jbi.photosync.content.DataContentHandler;
import de.jbi.photosync.content.SharedPreferencesUtil;
import de.jbi.photosync.utils.Folder;

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
                String folderPath = getInstance().getSelectedFolders().get(position).getAbsolutePath().getPath();
                Toast.makeText(getContext(), folderPath , Toast.LENGTH_SHORT).show();

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
        Folder folderToBeRemoved = dataContentHandler.getSelectedFolders().get(position);

        dataContentHandler.removeSelectedFolder(folderToBeRemoved);
        SharedPreferencesUtil.removeFolder(getContext(), folderToBeRemoved);

        this.notifyDataSetChanged();
    }
}
