package de.jbi.photosync;

import android.app.Activity;
import android.content.Context;

import org.junit.Before;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.fakes.RoboSharedPreferences;

import de.jbi.photosync.activities.MainActivity;
import de.jbi.photosync.content.DataContentHandler;

/**
 * Created by Jan on 28.05.2016.
 */
public abstract class AbstractUnitTest {
    Activity mainActivity;
    RoboSharedPreferences preferences;
    DataContentHandler dataContentHandler;

    @Before
    public void setUp() {
        mainActivity = Robolectric.setupActivity(MainActivity.class);
        preferences = (RoboSharedPreferences) RuntimeEnvironment.application.getSharedPreferences(mainActivity.getString(R.string.shared_preference_data), Context.MODE_PRIVATE);
        dataContentHandler = DataContentHandler.getInstance();
    }
}
