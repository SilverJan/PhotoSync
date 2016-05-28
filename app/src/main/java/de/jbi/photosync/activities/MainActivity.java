package de.jbi.photosync.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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
import de.jbi.photosync.fragments.DeviceInfoFragment;
import de.jbi.photosync.fragments.FolderSelectionFragment;
import de.jbi.photosync.fragments.SettingsFragment;
import de.jbi.photosync.domain.Folder;
import de.jbi.photosync.utils.AndroidUtil;
import de.jbi.photosync.utils.Logger;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private Toolbar toolbar;

    private CharSequence title;
    private String[] fragmentTitles;


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
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_dehaze_black_24dp);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadInitialFolders();
    }


    @Override
    public void onResume() {
        super.onResume();
        reloadInitialFolders();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
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

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new DashboardFragment();
                handleDashboardFragment(fragment, position);
                break;
            case 1:
                fragment = new DeviceInfoFragment();
                break;
            case 2:
                fragment = new FolderSelectionFragment();
                break;
            case 3:
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

    private void reloadInitialFolders() {
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


// TODO: Handle: list_drawer_item.xml, fragment_emptyxml, strings entries


//    private DrawerLayout drawerLayout;
//    private ListView drawerList;
//    private ActionBarDrawerToggle drawerToggle;
//
//    private CharSequence drawerTitle;
//    private CharSequence title;
//    private String[] fragmentTitles;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        title = drawerTitle = getTitle();
//        fragmentTitles = getResources().getStringArray(R.array.planets_array);
//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawerList = (ListView) findViewById(R.id.left_drawer);
//
//        // set a custom shadow that overlays the main content when the drawer opens
////        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
//        // set up the drawer's list view with items and click listener
//        drawerList.setAdapter(new ArrayAdapter<>(this,
//                R.layout.list_drawer_item, fragmentTitles));
//        drawerList.setOnItemClickListener(new DrawerItemClickListener());
//
//        // enable ActionBar app icon to behave as action to toggle nav drawer
////        getActionBar().setDisplayHomeAsUpEnabled(true);
////        getActionBar().setHomeButtonEnabled(true);
//
//        // ActionBarDrawerToggle ties together the the proper interactions
//        // between the sliding drawer and the action bar app icon
//        drawerToggle = new ActionBarDrawerToggle(
//                this,                  /* host Activity */
//                drawerLayout,         /* DrawerLayout object */
////                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
//                R.string.drawer_open,  /* "open drawer" description for accessibility */
//                R.string.drawer_close  /* "close drawer" description for accessibility */
//        ) {
//            public void onDrawerClosed(View view) {
//                getActionBar().setTitle(title);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle(drawerTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//        };
//        drawerLayout.setDrawerListener(drawerToggle);
//
//        if (savedInstanceState == null) {
//            selectItem(0);
//        }
//    }
//
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        MenuInflater inflater = getMenuInflater();
////        inflater.inflate(R.menu.main, menu);
////        return super.onCreateOptionsMenu(menu);
////    }
//
//    /* Called whenever we call invalidateOptionsMenu() */
////    @Override
////    public boolean onPrepareOptionsMenu(Menu menu) {
////        // If the nav drawer is open, hide action items related to the content view
////        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
////        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
////        return super.onPrepareOptionsMenu(menu);
////    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // The action bar home/up action should open or close the drawer.
//        // ActionBarDrawerToggle will take care of this.
//        if (drawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        // Handle action buttons
//        switch (item.getItemId()) {
////            case R.id.action_websearch:
////                // create intent to perform web search for this planet
////                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
////                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
////                // catch event that there's no activity to handle intent
////                if (intent.resolveActivity(getPackageManager()) != null) {
////                    startActivity(intent);
////                } else {
////                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
////                }
////                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    /* The click listner for ListView in the navigation drawer */
//    private class DrawerItemClickListener implements ListView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            selectItem(position);
//        }
//    }
//
//    private void selectItem(int position) {
//        // update the main content by replacing fragments
//        Fragment fragment = new DashboardFragment();
//        Bundle args = new Bundle();
//        args.putInt(DashboardFragment.ARG_FRAGMENT_NUMBER, position);
//        fragment.setArguments(args);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//
//        // update selected item and title, then close the drawer
//        drawerList.setItemChecked(position, true);
//        setTitle(fragmentTitles[position]);
//        drawerLayout.closeDrawer(drawerList);
//    }
//
//    @Override
//    public void setTitle(CharSequence title) {
//        title = title;
//        getActionBar().setTitle(title);
//    }
//
//    /**
//     * When using the ActionBarDrawerToggle, you must call it during
//     * onPostCreate() and onConfigurationChanged()...
//     */
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        drawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        // Pass any configuration change to the drawer toggls
//        drawerToggle.onConfigurationChanged(newConfig);
//    }
//
//    /**
//     * Fragment that appears in the "content_frame", shows a planet
//     */
//    public static class DashboardFragment extends Fragment {
//        public static final String ARG_FRAGMENT_NUMBER = "planet_number";
//
//        public DashboardFragment() {
//            // Empty constructor required for fragment subclasses
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
//            int i = getArguments().getInt(ARG_FRAGMENT_NUMBER);
//            String planet = getResources().getStringArray(R.array.planets_array)[i];
//
//            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
//                    "drawable", getActivity().getPackageName());
//            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
//            getActivity().setTitle(planet);
//            return rootView;
//        }
//    }
//}