package de.jbi.photosync;

import android.app.Fragment;
import android.app.RobolectricActivityManager;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;
import org.robolectric.util.FragmentTestUtil;

import de.jbi.photosync.fragments.DashboardFragment;

/**
 * Created by Jan on 28.05.2016.
 */
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
        fragment = new DashboardFragment();
    }
}
