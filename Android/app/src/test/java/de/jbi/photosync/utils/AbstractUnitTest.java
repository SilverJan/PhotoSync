package de.jbi.photosync.utils;

import android.app.Activity;
import android.content.Context;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.fakes.RoboSharedPreferences;

import de.jbi.photosync.R;
import de.jbi.photosync.activities.MainActivity;
import de.jbi.photosync.content.DataContentHandler;

/**
 * Created by Jan on 28.05.2016.
 */
public abstract class AbstractUnitTest {
    protected Activity mainActivity;
    protected RoboSharedPreferences preferences;

    @Mock protected DataContentHandler dataContentHandlerMock;

    @Before
    public void setUp() throws IllegalAccessException {
        MockitoAnnotations.initMocks(this);

        mainActivity = Robolectric.setupActivity(MainActivity.class);
        preferences = (RoboSharedPreferences) RuntimeEnvironment.application.getSharedPreferences(mainActivity.getString(R.string.shared_preference_data), Context.MODE_PRIVATE);

        handleDataContentHandlerMock();
    }

    private void handleDataContentHandlerMock() throws IllegalAccessException {
        DataContentHandler.setInstance(dataContentHandlerMock);
        MockUtil.setDataContentHandlerMock(dataContentHandlerMock);
    }
}
