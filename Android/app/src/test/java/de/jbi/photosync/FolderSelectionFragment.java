package de.jbi.photosync;

import android.app.Fragment;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by Jan on 28.05.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class FolderSelectionFragment extends AbstractUnitTest {
    Fragment fragment;

    @Test
    public void whenCreateFragment_Then() {
        // ### STEP 1: Pre ###
        FragmentTestUtil.startFragment(fragment);

        // ### STEP 2: Action ###


        // ### STEP 3: Assert ###

        // ### STEP 4: Post ###
    }

    @Test
    public void when_Then() {
        // ### STEP 1: Pre ###

        // ### STEP 2: Action ###

        // ### STEP 3: Assert ###

        // ### STEP 4: Post ###
    }

    @Before
    public void setUp() {
        super.setUp();
        fragment = new de.jbi.photosync.fragments.FolderSelectionFragment();
    }
}
