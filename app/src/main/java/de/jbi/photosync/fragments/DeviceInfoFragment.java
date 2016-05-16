package de.jbi.photosync.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import de.jbi.photosync.R;
import de.jbi.photosync.adapters.PhotoArrayAdapter;
import de.jbi.photosync.utils.AndroidUtil;

import static de.jbi.photosync.utils.AndroidUtil.getAllPhotos;

/**
 * Created by Jan on 14.05.2016.
 */
public class DeviceInfoFragment extends Fragment {
    private Activity activity;
    private Context ctx;
    private View rootView;

    public DeviceInfoFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_device_info, container, false);

        activity = getActivity();
        ctx = activity.getApplicationContext();

        setInfoTextViews();
        setPhotoView();

        return rootView;
    }

    private void setInfoTextViews() {

        TextView accessTV = (TextView) rootView.findViewById(R.id.accessInfoTextView);
        accessTV.setText("Access info: ");
        if (AndroidUtil.isExternalStorageReadable()) {
            accessTV.append("SD card access granted!");
        } else {
            accessTV.append("SD card access denied!");
        }

        TextView fileDirTV = (TextView) rootView.findViewById(R.id.fileDirTextView);
        fileDirTV.setText("Files dir: " + ctx.getFilesDir().getAbsolutePath());
        fileDirTV.append("\nData dir: " + Environment.getDataDirectory());
        fileDirTV.append("\nRoot dir: " + Environment.getRootDirectory());
        fileDirTV.append("\nExt dir: " + Environment.getExternalStorageDirectory());

        File firstPic = getAllPhotos(Environment.getExternalStorageDirectory()).get(0);

        fileDirTV.append(firstPic.toString());
    }

    private void setPhotoView() {
        ArrayList<File> allPhotos = (ArrayList) getAllPhotos(Environment.getExternalStorageDirectory());
        ArrayList<File> severalPhotos = new ArrayList<>();
        for (int i = 0; i != 2; i++) {
            severalPhotos.add(allPhotos.get(i));
        }

        PhotoArrayAdapter photoAdapter = new PhotoArrayAdapter(ctx, R.layout.list_photo_item, severalPhotos);
        ListView listView = (ListView) rootView.findViewById(R.id.photoListView);
        listView.setAdapter(photoAdapter);
    }
}
