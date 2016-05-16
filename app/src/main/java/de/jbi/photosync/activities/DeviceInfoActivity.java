package de.jbi.photosync.activities;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import de.jbi.photosync.R;
import de.jbi.photosync.adapters.PhotoArrayAdapter;
import de.jbi.photosync.utils.AndroidUtil;

import static de.jbi.photosync.utils.AndroidUtil.getAllPhotos;

@Deprecated
public class DeviceInfoActivity extends AppCompatActivity {

    private static Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ctx = getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device_info);

        setInfoTextViews();

        setPhotoView();
    }

    private void setInfoTextViews() {
        TextView accessTV = (TextView) findViewById(R.id.accessInfoTextView);
        accessTV.setText("Access info: ");
        if (AndroidUtil.isExternalStorageReadable()) {
            accessTV.append("SD card access granted!");
        } else {
            accessTV.append("SD card access denied!");
        }

        TextView fileDirTV = (TextView) findViewById(R.id.fileDirTextView);
        fileDirTV.setText("Files dir: " + ctx.getFilesDir().getAbsolutePath());
        fileDirTV.append("\nData dir: " + Environment.getDataDirectory());
        fileDirTV.append("\nRoot dir: " + Environment.getRootDirectory());
        fileDirTV.append("\nExt dir: " + Environment.getExternalStorageDirectory());

        File firstPic = getAllPhotos(Environment.getExternalStorageDirectory(), false).get(0);

        fileDirTV.append(firstPic.toString());
    }

    private void setPhotoView() {
        ArrayList<File> allPhotos = (ArrayList) getAllPhotos(Environment.getExternalStorageDirectory(), false);
        ArrayList<File> severalPhotos = new ArrayList<>();
        for (int i = 0; i != 10; i++) {
            severalPhotos.add(allPhotos.get(i));
        }

        PhotoArrayAdapter photoAdapter = new PhotoArrayAdapter(ctx, R.layout.list_photo_item, severalPhotos);

        ListView listView = (ListView) findViewById(R.id.photoListView);
        listView.setAdapter(photoAdapter);
    }
}
