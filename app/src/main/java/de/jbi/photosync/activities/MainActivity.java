package de.jbi.photosync.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.jbi.photosync.R;
import de.jbi.photosync.fragments.EmptyFragment;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private CharSequence title;
    private String[] fragmentTitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentTitles = getResources().getStringArray(R.array.fragments_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, fragmentTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (savedInstanceState == null) {
            selectItem(0);
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
        Fragment fragment = new EmptyFragment();
        Bundle args = new Bundle();
        args.putInt(EmptyFragment.ARG_FRAGMENT_NUMBER, position);
        fragment.setArguments(args);

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

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getSupportActionBar().setTitle(this.title);
    }
}


// TODO: Handle: drawer_list_item.xml, fragment_emptyxml, strings entries


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
//                R.layout.drawer_list_item, fragmentTitles));
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
//        Fragment fragment = new EmptyFragment();
//        Bundle args = new Bundle();
//        args.putInt(EmptyFragment.ARG_FRAGMENT_NUMBER, position);
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
//    public static class EmptyFragment extends Fragment {
//        public static final String ARG_FRAGMENT_NUMBER = "planet_number";
//
//        public EmptyFragment() {
//            // Empty constructor required for fragment subclasses
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_empty, container, false);
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