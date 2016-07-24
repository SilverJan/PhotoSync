package de.jbi.photosync.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.Observer;

import de.jbi.photosync.R;
import de.jbi.photosync.content.DataContentHandler;
import de.jbi.photosync.content.SharedPreferencesUtil;
import de.jbi.photosync.fragments.DashboardFragment;
import de.jbi.photosync.fragments.FolderSelectionFragment;
import de.jbi.photosync.fragments.SettingsFragment;
import de.jbi.photosync.domain.Folder;
import de.jbi.photosync.utils.AndroidUtil;
import de.jbi.photosync.utils.Logger;
import de.jbi.photosync.utils.NotificationFactory;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private Toolbar toolbar;

    private CharSequence title;
    public String[] fragmentTitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ##########################
        // ### SET INITIAL VALUES ###
        // ##########################

        // Set main Context
        AndroidUtil.ContextHandler.setMainContext(this);

        // Set default preferences from preferences.xml
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Load folders from SharedPreferences and set to global DataContentHandler
        reloadInitialFolders();

        registerObserver();

        // ####################
        // ### SET UI STUFF ###
        // ####################

        fragmentTitles = getResources().getStringArray(R.array.fragments_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.list_drawer_item, fragmentTitles));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectFragment(position);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.fragment_media_screen_toolbar);
        setSupportActionBar(toolbar);
        if (getApplicationInfo().theme == R.style.Theme_TestTheme_NoActionBar_Fitted) {
            toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_dehaze_black_24dp);
        }

        // Remove potential notifications because they should only exist if app is hidden
        NotificationFactory.dismissNotification();

        if (savedInstanceState == null) {
            selectFragment(0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadInitialFolders();

        // Remove potential notifications because they should only exist if app is hidden
        NotificationFactory.dismissNotification();
    }


    @Override
    public void onResume() {
        super.onResume();
        reloadInitialFolders();

        // Remove potential notifications because they should only exist if app is hidden
        NotificationFactory.dismissNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This is recently unused! If functionality should be added to toolbar, then change res/menu/toolbar.xml and handle them in onOptionsItemSelected()
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void selectFragment(int position) {
        // update the main content by replacing fragments
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new DashboardFragment();
                handleDashboardFragment(fragment, position);
                break;
            case 1:
                fragment = new FolderSelectionFragment();
                break;
            case 2:
                fragment = new SettingsFragment();
                break;
            default:
                fragment = new DashboardFragment();
                handleDashboardFragment(fragment, position);
                break;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        drawerList.setItemChecked(position, true);
        setTitle(fragmentTitles[position]);
        drawerLayout.closeDrawer(drawerList);
    }

    private void handleDashboardFragment(Fragment fragment, int position) {
        Bundle args = new Bundle();
        args.putInt(DashboardFragment.ARG_FRAGMENT_NUMBER, position);
        fragment.setArguments(args);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getSupportActionBar().setTitle(this.title);
    }

    /**
     * Refreshes the DataContentHandler folder instances -> If there are new files on the device, then this method must be called
     */
    public void reloadInitialFolders() {
        List<Folder> initialFolders = SharedPreferencesUtil.getFolders();
        for (Folder folder: initialFolders) {
            folder.refreshFolderMetaData();
        }
        DataContentHandler.getInstance().setFolders(initialFolders);
    }

    private void registerObserver() {
        Observer logObserver = new DashboardFragment();
        Logger logger = Logger.getInstance();
        Logger.setCtx(this);
        logger.addObserver(logObserver);
    }
}