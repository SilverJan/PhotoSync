package de.jbi.photosync.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import de.jbi.photosync.R;

/**
 * Created by Jan on 13.05.2016.
 */
public class PhotoArrayAdapter extends ArrayAdapter {
    private ArrayList<File> photos;

    public PhotoArrayAdapter(Context context, int resource, ArrayList<File> photos) {
        super(context, resource, photos);
        this.photos = photos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_photo_item, null);
        }

        File file = photos.get(position);

        ImageView imageView = (ImageView) view.findViewById(R.id.listPhotoImageView);
        imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));

        return view;
    }
}
