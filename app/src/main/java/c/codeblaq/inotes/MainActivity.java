package c.codeblaq.inotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //    Recycler View Adapter
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private RecyclerView mRecyclerNotes;
    private RecyclerView mRecyclerItems;
    private LinearLayoutManager mNotesLayoutManager;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;
    private GridLayoutManager mCoursesLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start a new note activity inline
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });

        /*Update content based on user's preference*/

        //Default values for preference
        ///false says if values exists, don't pass defaults
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//        Nav View object
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set up display
        initializeDisplayContent();
    }

    //Called when app is resumed
    @Override
    protected void onResume() {
        super.onResume();
        //notify adapter of data change
        mNoteRecyclerAdapter.notifyDataSetChanged();
        //Update nav details
        updateNavHeader();
    }

    /*Update user details in Nav view*/
    private void updateNavHeader() {
        //Locate nav view
        NavigationView navigationView = findViewById(R.id.nav_view);
        //locate header within Nav view
        View headerView = navigationView.getHeaderView(0);
        //Locate user detail text views
        TextView textUserName = headerView.findViewById(R.id.text_user_name);
        TextView textUserEmailAddress = headerView.findViewById(R.id.text_user_email);

        //Create local shared preference instance
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        //Get username
        String userName = pref.getString("user_display_name", ""); //Second parameter can be left empty because of preset default
        String emailAddress = pref.getString("user_email_address", "");

        //Set to text views
        textUserName.setText(userName);
        textUserEmailAddress.setText(emailAddress);
    }

    //Display content list
    private void initializeDisplayContent() {

        //Create variable for recycler view
        mRecyclerItems = findViewById(R.id.list_items);
        //Create linear layout manager instance
        mNotesLayoutManager = new LinearLayoutManager(this);

        //Grid Layout Manager for Courses
        mCoursesLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.course_grid_span));

        //Get notes to be displayed within recycler view
        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        //Create instance of NoteRecyclerAdapter
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);

        /*For courses*/
        //Get courses from Data Manager
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        mCourseRecyclerAdapter = new CourseRecyclerAdapter(this, courses);
        displayNotes();
    }

    /*Display notes in recycler view*/
    private void displayNotes() {
        //associate recycler view with manager
        mRecyclerItems.setLayoutManager(mNotesLayoutManager);
        //Associate adapter with recycler view
        mRecyclerItems.setAdapter(mNoteRecyclerAdapter);
        selectNavigationMenuItem(R.id.nav_notes);


    }

    /**
     * Select Nav Menu Item
     */
    private void selectNavigationMenuItem(int id) {
        //Create nav view local variable
        NavigationView navigationView = findViewById(R.id.nav_view);
        //Create menu variable
        Menu menu = navigationView.getMenu();
        menu.findItem(id).setChecked(true);//display as selected option
    }

    /*Display Courses in Recycler View*/
    private void displayCourses() {
        mRecyclerItems.setLayoutManager(mCoursesLayoutManager);
        mRecyclerItems.setAdapter(mCourseRecyclerAdapter);
        selectNavigationMenuItem(R.id.nav_courses);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            // Handle the notes action
            displayNotes();

        } else if (id == R.id.nav_courses) {
            displayCourses();

        } else if (id == R.id.nav_share) {
//            handleSelection(R.string.nav_share_message);
            handleShare();

        } else if (id == R.id.nav_send) {
            handleSelection(R.string.nav_send_message);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleShare() {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, "Share to - " +
                        PreferenceManager.getDefaultSharedPreferences(this).getString("user_favorite_social", ""),
                Snackbar.LENGTH_LONG).show();
    }

    private void handleSelection(int message_id) {
        //Get recycler view as view to be passed to SnackBar
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, message_id, Snackbar.LENGTH_LONG).show();
    }
}
