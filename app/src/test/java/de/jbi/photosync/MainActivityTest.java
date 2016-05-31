package de.jbi.photosync;

import android.app.Activity;
import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboSharedPreferences;

import de.jbi.photosync.activities.MainActivity;
import de.jbi.photosync.fragments.FolderSelectionFragment;

/**
 * Created by Jan on 28.05.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {
    Activity mainActivity;
    RoboSharedPreferences preferences;

    @Test
    public void whenOnCreate_ThenValuesMustBeSet() {
        // ### STEP 1: Pre ###

        // ### STEP 2: Action ###

        // ### STEP 3: Assert ###

        // ### STEP 4: Post ###
    }

    @Test
    public void whenThen() {
        // ### STEP 1: Pre ###

        // ### STEP 2: Action ###

        // ### STEP 3: Assert ###

        // ### STEP 4: Post ###
    }

    @Before
    public void setUp() {
        mainActivity = Robolectric.setupActivity(MainActivity.class);
        preferences = (RoboSharedPreferences) RuntimeEnvironment.application.getSharedPreferences("example", Context.MODE_PRIVATE);
    }
}
