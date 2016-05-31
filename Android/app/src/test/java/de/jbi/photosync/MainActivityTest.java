package de.jbi.photosync;

import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import de.jbi.photosync.activities.MainActivity;
import de.jbi.photosync.content.SharedPreferencesUtil;
import de.jbi.photosync.domain.Folder;

import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * Created by Jan on 28.05.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MainActivityTest extends AbstractUnitTest {

    @Test
    public void whenSelectDashboard_ThenDashboardTitleAndSelected() {
        // ### STEP 1: Pre ###
        ListView drawerList = (ListView) mainActivity.findViewById(R.id.left_drawer);
        Toolbar toolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar);

        // ### STEP 2: Action ###
        drawerList.performItemClick(null, 0, 99);

        // ### STEP 3: Assert ###
        assertThat(drawerList.getCheckedItemCount()).isEqualTo(1);
        assertThat(toolbar.getTitle()).isEqualTo("Dashboard");

        // ### STEP 4: Post ###
    }

    @Test
    public void whenActivityStartedAndNoInitialFolders_ThenNoInitialFoldersMustBeLoaded() {
        // ### STEP 1: Pre ###

        // ### STEP 2: Action ###
        mainActivity = Robolectric.setupActivity(MainActivity.class);

        // ### STEP 3: Assert ###
        assertThat(dataContentHandler.getFolders()).hasSize(0);

        // ### STEP 4: Post ###
    }

    @Test
    public void whenActivityStartedAndInitialFolders_ThenInitialFoldersMustBeLoaded() {
        // ### STEP 1: Pre ###
        Folder testFolder = TestUtil.getTestFolderA();
        SharedPreferencesUtil.addFolder(testFolder);

        // ### STEP 2: Action ###
        mainActivity = Robolectric.setupActivity(MainActivity.class);

        // ### STEP 3: Assert ###
        assertThat(dataContentHandler.getFolders()).hasSize(1);
        assertThat(dataContentHandler.getFolders()).contains(testFolder);

        // ### STEP 4: Post ###
    }

    @Test
    public void whenActivityResumedAndInitialFolders_ThenInitialFoldersMustBeLoaded() {
        // ### STEP 1: Pre ###
        Folder testFolderA = TestUtil.getTestFolderA();
        Folder testFolderB = TestUtil.getTestFolderB();
        SharedPreferencesUtil.addFolder(testFolderA);

        // ### STEP 2: Action ###
        SharedPreferencesUtil.addFolder(testFolderB);
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().resume().get();

        // ### STEP 3: Assert ###
        assertThat(dataContentHandler.getFolders()).hasSize(2);
        assertThat(dataContentHandler.getFolders()).contains(testFolderA, testFolderB);

        // ### STEP 4: Post ###
    }
}
