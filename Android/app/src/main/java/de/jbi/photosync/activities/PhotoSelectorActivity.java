package de.jbi.photosync.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import de.jbi.photosync.R;
import de.jbi.photosync.domain.PictureVideo;
import de.jbi.photosync.http.FileUploadIntentService;
import de.jbi.photosync.utils.AndroidUtil;
import de.jbi.photosync.utils.Constants;

public class PhotoSelectorActivity extends AppCompatActivity {
    public static final int FINISH_PHOTO_SELECTOR_REQUEST_CODE = 1;

    private TextView indexInfoTV;
    private ImageView mainImageView;
    private Button backButton;
    private Button nextButton;
    private CheckBox includeInSyncCB;
    private CheckBox excludeGloballyCB;

    private int currentIndex = 0;

    private PictureVideo[] pictureVideoList; // TODO maybe make this Queue (see FileUploadIntentService)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_selector);

        // ##########################
        // ### SET INITIAL VALUES ###
        // ##########################

        String obj = getIntent().getStringExtra(Constants.EXTRA_PICTURE_VIDEO_LIST);
        Type type = new TypeToken<PictureVideo[]>() {
        }.getType();
        pictureVideoList = new Gson().fromJson(obj, type);

        // ####################
        // ### SET UI STUFF ###
        // ####################

        indexInfoTV = (TextView) findViewById(R.id.activity_photo_selector_index_info_text_view);
        mainImageView = (ImageView) findViewById(R.id.activity_photo_selector_image_view);
        backButton = (Button) findViewById(R.id.activity_photo_selector_back_button);
        nextButton = (Button) findViewById(R.id.activity_photo_selector_next_button);
        includeInSyncCB = (CheckBox) findViewById(R.id.activity_photo_selector_include_in_sync_checkbox);
        excludeGloballyCB = (CheckBox) findViewById(R.id.activity_photo_selector_exclude_globally_checkbox);

        updateIndexTextView();

        // First media -> Back button untouchable or invisible
        if (currentIndex == 0) {
            backButton.setFocusable(false);
            backButton.setAlpha(.5f);
        }
        // Last media -> Sync text instead of next
        if (currentIndex >= (pictureVideoList.length - 1)) {
            nextButton.setText(getString(R.string.activity_photo_selector_sync));
            nextButton.setTypeface(nextButton.getTypeface(), Typeface.BOLD);
        }

        includeInSyncCB.setChecked(true); // default value
        setPictureToImageView(currentIndex); // set first media

        includeInSyncCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (includeInSyncCB.isChecked()) {
                    pictureVideoList[currentIndex].setEnabled(true);
                } else {
                    pictureVideoList[currentIndex].setEnabled(false);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex > 0) {
                    setPictureToImageView(currentIndex - 1);
                    currentIndex--;
                    if (nextButton.getText().equals(getString(R.string.activity_photo_selector_sync))) {
                        // f.e. 2/2 -> 1/2
                        nextButton.setText(getString(R.string.activity_photo_selector_next));
                        nextButton.setTypeface(backButton.getTypeface());
                    }
                    if (currentIndex <= 0) {
                        // f.e. 2/2 -> 1/2
                        backButton.setFocusable(false);
                        backButton.setAlpha(.5f);
                    }
                    includeInSyncCB.setChecked(pictureVideoList[currentIndex].isEnabled());
                    updateIndexTextView();
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if next btn or if sync btn
                if (nextButton.getText().equals(getString(R.string.activity_photo_selector_next))) {
                    if (currentIndex < (pictureVideoList.length - 1)) {
                        setPictureToImageView(currentIndex + 1);
                        currentIndex++;
                        if (currentIndex > 0) {
                            // f.e. 1/2 -> 2/2
                            backButton.setFocusable(true);
                            backButton.setAlpha(1f);
                        }
                        if (currentIndex >= (pictureVideoList.length - 1)) {
                            // f.e. 1/2 -> 2/2
                            nextButton.setText(getString(R.string.activity_photo_selector_sync));
                            nextButton.setTypeface(nextButton.getTypeface(), Typeface.BOLD);
                        }
                        includeInSyncCB.setChecked(pictureVideoList[currentIndex].isEnabled());
                        updateIndexTextView();
                    }
                } else {
                    // if last media -> sync starts with only enabled media
                    List<PictureVideo> enabledPicVidList = PictureVideo.getEnabledPictureVideos(Arrays.asList(pictureVideoList));
                    startService(new Intent(getBaseContext(), FileUploadIntentService.class).putExtra(Constants.EXTRA_PICTURE_VIDEO_LIST, new Gson().toJson(enabledPicVidList)));
                    setResult(RESULT_OK, new Intent().putExtra(Constants.PASS_SYNC_MAX_COUNTER_AFTER_FILE_VALIDATION_INTENT, new Gson().toJson(enabledPicVidList)));
                    finish();
                }
            }
        });
    }

    private void setPictureToImageView(int index) {
        Bitmap recent = AndroidUtil.getBitmapFromFile(pictureVideoList[index].getAbsolutePath());
        mainImageView.setImageBitmap(recent);
    }

    private void updateIndexTextView() {
        String text = (currentIndex + 1) + "/" + pictureVideoList.length;
        indexInfoTV.setText(text);
    }
}
