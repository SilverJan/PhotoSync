package de.jbi.photosync.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;

import de.jbi.photosync.R;
import de.jbi.photosync.domain.PictureVideo;
import de.jbi.photosync.utils.AndroidUtil;
import de.jbi.photosync.utils.BitmapUtils;
import de.jbi.photosync.utils.Logger;

public class MediaScreenSlideFragment extends Fragment {
    private static final String EXTRA_PICTURE_VIDEO = "mediascreenslidefragment.picturevideo";

    private onMediaSelectedListener listener;
    private Context ctx;

    private Toolbar toolbar;
    private ImageView mediaImageView;
//    private VideoView mediaVideoView;
    private ViewGroup rootView;
    private PictureVideo currentMedia;

    public MediaScreenSlideFragment() {

    }

    public static final MediaScreenSlideFragment newInstance(PictureVideo media) {
        MediaScreenSlideFragment f = new MediaScreenSlideFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_PICTURE_VIDEO, new Gson().toJson(media));
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getContext();

        String json = getArguments().getString(EXTRA_PICTURE_VIDEO);
        Type type = new TypeToken<PictureVideo>() {
        }.getType();
        currentMedia = new Gson().fromJson(json, type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_media_screen_slide, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.fragment_media_screen_toolbar);
        mediaImageView = (ImageView) rootView.findViewById(R.id.fragment_media_screen_slide_media_image_view);

        toolbar.setTitle(currentMedia.getName());

        File mediaFile = currentMedia.getAbsolutePath();

        if (AndroidUtil.isFilePicture(mediaFile)) {
            Bitmap bm = BitmapUtils.getBitmapFromFile(mediaFile);
            mediaImageView.setImageBitmap(bm);
        } else if (AndroidUtil.isFileVideo(mediaFile)){
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mediaFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            mediaImageView.setImageBitmap(thumb);
            // Make this VideoView
//            listener.onMediaSkipped();
        } else {
            Logger.showLogToast("File not supported: " + currentMedia.getName());
        }

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onMediaSelectedListener) {
            listener = (onMediaSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onMediaSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public interface onMediaSelectedListener {
        void onMediaSelected(Object obj);
        void onMediaSkipped();
    }
}
