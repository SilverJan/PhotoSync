package de.jbi.photosync.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import de.jbi.photosync.R;
import de.jbi.photosync.domain.PictureVideo;
import de.jbi.photosync.fragments.MediaScreenSlideFragment;
import de.jbi.photosync.utils.Constants;

/**
 * Created by Jan on 20.07.2016.
 */
public class MediaScreenSlideActivity extends FragmentActivity implements MediaScreenSlideFragment.onMediaSelectedListener {
    public static final int FINISH_MEDIA_SLIDE_REQUEST_CODE = 1;

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private List<PictureVideo> mediaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_screen_slide);

        // ##########################
        // ### SET INITIAL VALUES ###
        // ##########################

        String obj = getIntent().getStringExtra(Constants.EXTRA_MEDIA_LIST);
        Type type = new TypeToken<List<PictureVideo>>() {
        }.getType();
        mediaList = new Gson().fromJson(obj, type);
        int pos = getIntent().getIntExtra("pos", 0); // TODO rename

        // ####################
        // ### SET UI STUFF ###
        // ####################

        pager = (ViewPager) findViewById(R.id.activity_media_screen_slide_view_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(pos);

    }

    @Override
    public void onMediaSelected(Object obj) {
        // TODO
    }

    @Override
    public void onMediaSkipped() {
        pager.setCurrentItem(pager.getCurrentItem() + 1);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MediaScreenSlideFragment.newInstance(mediaList.get(position));
        }

        @Override
        public int getCount() {
            return mediaList.size();
        }
    }
}
