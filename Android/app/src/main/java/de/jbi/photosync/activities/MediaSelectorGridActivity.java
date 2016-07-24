package de.jbi.photosync.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.jbi.photosync.R;
import de.jbi.photosync.domain.PictureVideo;
import de.jbi.photosync.http.FileUploadIntentService;
import de.jbi.photosync.utils.AndroidUtil;
import de.jbi.photosync.utils.BitmapWorkerTask;
import de.jbi.photosync.utils.Constants;
import de.jbi.photosync.utils.Logger;

import static de.jbi.photosync.utils.Constants.EXTRA_MEDIA_LIST;

public class MediaSelectorGridActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final int FINISH_MEDIA_SLIDE_REQUEST_CODE = 1;
    private List<PictureVideo> mediaList;

    private Toolbar toolbar;
    private GridView grid;
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_selector_grid);

        // ##########################
        // ### SET INITIAL VALUES ###
        // ##########################

        String obj = getIntent().getStringExtra(Constants.EXTRA_MEDIA_LIST);
        Type type = new TypeToken<PictureVideo[]>() {
        }.getType();
        List<PictureVideo> list = Arrays.asList((PictureVideo[])new Gson().fromJson(obj, type));
        mediaList = new ArrayList<>(list);

        // ####################
        // ### SET UI STUFF ###
        // ####################

        grid = (GridView) findViewById(R.id.activity_media_selector_grid_view);
        adapter = new ImageAdapter(this);
        grid.setAdapter(adapter);
        toolbar = (Toolbar) findViewById(R.id.activity_media_selector_grid_view_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pre-Sync validation");

//        grid.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent().putExtra(Constants.PASS_SYNC_MAX_COUNTER_AFTER_FILE_VALIDATION_INTENT, new Gson().toJson(mediaList)));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_activity_media_selector_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_activity_media_selector_grid_sync:
                List<PictureVideo> enabledPicVidList = PictureVideo.getEnabledPictureVideos(mediaList);
                startService(new Intent(getBaseContext(), FileUploadIntentService.class).putExtra(Constants.EXTRA_MEDIA_LIST, new Gson().toJson(enabledPicVidList)));
                setResult(RESULT_OK, new Intent().putExtra(Constants.PASS_SYNC_MAX_COUNTER_AFTER_FILE_VALIDATION_INTENT, new Gson().toJson(enabledPicVidList)));
                finish();
                return true;

            case R.id.toolbar_activity_media_selector_grid_select_all:
                PictureVideo.changeMediaEnabledState(mediaList, true);
                adapter.notifyDataSetInvalidated();
                return true;

            case R.id.toolbar_activity_media_selector_grid_deselect_all:
                PictureVideo.changeMediaEnabledState(mediaList, false);
                adapter.notifyDataSetInvalidated();
                return true;

            case R.id.toolbar_activity_media_selector_grid_remove_selected:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Remove media?");
                builder.setMessage("All selected medias will be removed from your device!");
                builder.setCancelable(true);
                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<PictureVideo> anotherEnabledPicVidList = PictureVideo.getEnabledPictureVideos(mediaList);
                        for (PictureVideo media : anotherEnabledPicVidList) {
                            try {
                                media.removeFile();
                                mediaList.remove(media);
                            } catch (IOException e) {
                                Logger.getInstance().appendLog("File remove not successful", true);
                            }
                        }
                        adapter.notifyDataSetInvalidated();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Logger.showLogToast("click");
    }

    public class ImageAdapter extends BaseAdapter {
        private Context ctx;
        private LayoutInflater inflater;

        public ImageAdapter(Context ctx) {
            super();
            this.ctx = ctx;
            inflater = LayoutInflater.from(ctx);
        }

        @Override
        public int getCount() {
            return mediaList.size();
        }

        @Override
        public Object getItem(int position) {
            return mediaList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            FrameLayout view;
            final TextView textView;
            final ImageView baseImageView;
            final ImageView selectedImageView;

            final PictureVideo media = mediaList.get(position);
            File mediaFile = media.getAbsolutePath();

//            if (view == null) {
                view = (FrameLayout) inflater.inflate(R.layout.grid_media_element, parent, false);
                view.setLayoutParams(new GridView.LayoutParams(530, 530));
                selectedImageView = (ImageView) view.findViewById(R.id.grid_media_element_selected_image_view);
                selectedImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_check_white_24dp));
                if (!media.isEnabled()) {
                    selectedImageView.setVisibility(View.INVISIBLE);
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedImageView.getVisibility() == View.INVISIBLE) {
                            selectedImageView.setVisibility(View.VISIBLE);
                            media.setEnabled(true);
                        } else {
                            selectedImageView.setVisibility(View.INVISIBLE);
                            media.setEnabled(false);
                        }
                    }
                });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent startDetailsIntent = new Intent(ctx, MediaScreenSlideActivity.class);
                    startDetailsIntent.putExtra(EXTRA_MEDIA_LIST, new Gson().toJson(mediaList));
                    startDetailsIntent.putExtra("pos", position);
                    startActivity(startDetailsIntent);
                    return true;
                }
            });

                baseImageView = (ImageView) view.findViewById(R.id.grid_media_element_base_image_view);
                baseImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                if (AndroidUtil.isFilePicture(mediaFile)) {
                    loadBitmap(mediaFile, baseImageView);
                } else if (AndroidUtil.isFileVideo(mediaFile)) {
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mediaFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    baseImageView.setImageBitmap(thumb);

                    ImageView playBtnImageView = new ImageView(ctx);
                    playBtnImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_circle_filled_white_24dp));
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER;
                    view.addView(playBtnImageView, params);
                } else {
                    // What happens if unsupported type added? Can this happen?
                }
//            } else {
//                baseImageView = (ImageView) view.findViewById(R.id.grid_media_element_base_image_view);
//                if (AndroidUtil.isFilePicture(mediaFile)) {
//                    loadBitmap(mediaFile, baseImageView);
//                } else if (AndroidUtil.isFileVideo(mediaFile)) {
//                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mediaFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
//                    baseImageView.setImageBitmap(thumb);
//                } else {
//                    // What happens if unsupported type added? Can this happen?
//                }
//            }
            textView = (TextView) view.findViewById(R.id.grid_media_element_text);
            textView.setText(mediaFile.getName());
            return view;
        }
    }

    /**
     * Sets a Bitmap instance from a given media to a given ImageView instance async (not in UI thread)
     *
     * @param media
     * @param imageView
     */
    public void loadBitmap(File media, ImageView imageView) {
        Bitmap placeHolderBitmap = null;
        if (cancelPotentialWork(media, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(getResources(), placeHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(media);
        }
    }

    /**
     * A wrapper for BitmapDrawable class which adds a BitmapWorkerTask field
     */
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);

            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(File media, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final File bitmapData = bitmapWorkerTask.media;
            if (media.equals(bitmapData)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    /**
     * Returns a BitmapWorkerTask instance of a given ImageView
     *
     * @param imageView
     * @return null or a BitmapWorkerTask instance
     */
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

}
