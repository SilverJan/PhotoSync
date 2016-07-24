package de.jbi.photosync.activities;

import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import de.jbi.photosync.utils.AbstractUnitTest;
import de.jbi.photosync.BuildConfig;
import de.jbi.photosync.R;
import de.jbi.photosync.utils.TestUtil;
import de.jbi.photosync.content.SharedPreferencesUtil;
import de.jbi.photosync.domain.Folder;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;


/**
 * Created by Jan on 28.05.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MainActivityTest extends AbstractUnitTest {

    @Test
    public void whenOnCreate_Then() {
        // ### STEP 1: Pre ###

        // ### STEP 2: Action ###
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();

        // ### STEP 3: Assert ###
//        verify(dataContentHandlerMock).setFolders();

        // ### STEP 4: Post ###
    }

}
