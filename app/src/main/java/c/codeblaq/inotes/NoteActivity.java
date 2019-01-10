package c.codeblaq.inotes;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import c.codeblaq.inotes.NoteDatabaseContract.CourseInfoEntry;
import c.codeblaq.inotes.NoteDatabaseContract.NoteInfoEntry;
import c.codeblaq.inotes.NoteProviderContract.Courses;

public class NoteActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ORIGINAL_NOTE_TEXT = "c.codeblaq.inotes.ORIGINAL_NOTE_TEXT";

    //Constant for NoteInfo extra (use package name to make it unique)
    public static final String NOTE_ID = "c.codeblaq.inotes.NOTE_ID";
   /*Constant values for original note states */
    public static final String ORIGINAL_NOTE_COURSE_ID = "c.codeblaq.inotes.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "c.codeblaq.inotes.ORIGINAL_NOTE_TITLE";
    public static final int LOADER_NOTES = 0;
    public static final int LOADER_COURSES = 1;
    private final String TAG = getClass().getSimpleName();

    public static final int ID_NOT_SET = -1;

    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNoteId;
    private boolean mIsCancelling;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;
    private NoteOpenHelper mDbOpenHelper;
    private Cursor mNoteCursor;
    private int mCourseIdPos;
    private int mNoteTitlePos;
    private int mNoteTextPos;
    private SimpleCursorAdapter mAdapterCourses;
    private boolean mCourseQueryFinished;
    private boolean mNotesQueryFinished;

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new NoteOpenHelper(this);

        //Instantiate spinner
        mSpinnerCourses = findViewById(R.id.spinner_courses);
        //Get list for spinner
        //Create adapter for  spinner list
        mAdapterCourses = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE},
                new int[]{android.R.id.text1}, 0);
        //Drop down for list of courses
        mAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Associate adapter with spinner
        mSpinnerCourses.setAdapter(mAdapterCourses);
        //Load course data
        getLoaderManager().initLoader(LOADER_COURSES, null, this);//notify this activity of loader event changes
        
        //Read values from intent
        readDisplayValues();

        //Save original note input
       if (savedInstanceState == null){ // opened for the first time
//           saveOriginalNoteValues();
       } else{ //recreated activity
           restoreOriginalNoteValues(savedInstanceState);
       }


        //Reference editText fields for the notes
        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        //display spinners and editTexts (Notes contents)
        if (!mIsNewNote) {//No need for saved content
//      Load note data
            getLoaderManager().initLoader(LOADER_NOTES, null, this);
        }
    }

    private void loadCourseData() {
        //Create connection to db
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        //Get data columns
        String[] courseColumns = {
                CourseInfoEntry.COLUMN_COURSE_TITLE,
                CourseInfoEntry.COLUMN_COURSE_ID,
                CourseInfoEntry._ID
        };
        Cursor cursor = db.query(CourseInfoEntry.TABLE_NAME, courseColumns,
                null, null, null, null, CourseInfoEntry.COLUMN_COURSE_TITLE);
        //Associate cursor to adapter
        mAdapterCourses.changeCursor(cursor);
    }

    /**
     * Get Note data from Sqlite Db
     **/
    private void loadNoteData() {
        //Get readable data from open helper
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        //Select clauses and variables
        String courseId = "android_intents";
        String titleStart = "dynamic";

        String selection = NoteInfoEntry._ID + " = ?";
        String[] selectionArgs = {Integer.toString(mNoteId)};

        String[] noteColumns = {
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_NOTE_TEXT
        };

        mNoteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
                selection, selectionArgs, null, null, null);

        //get cursor positions
        mCourseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        mNoteCursor.moveToNext();
        displayNote();
    }

    /**Called when to repopulate activity with original values when recreated*/
    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        //Retrieve original state values
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    /** Save what was originally in the note*/
    private void saveOriginalNoteValues() {
        if (mIsNewNote)
            return;

            //original course ID of the altered note
            mOriginalNoteCourseId = mNote.getCourse().getCourseId();

            //original title of the altered note
            mOriginalNoteTitle = mNote.getTitle();

            //original text of the altered note
            mOriginalNoteText = mNote.getText();
        }


    //When User leaves current activity
    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCancelling){//User cancelling created note
            Log.i(TAG, "Cancelling note at position: " + mNoteId);

            //Get DataManager instance and remove note if new note
            if(mIsNewNote) {
                deleteNoteFromDatabase();
            } else {
//                storePreviousNotesValues();
            }
        } else {
            saveNote();
        }
    }

    private void deleteNoteFromDatabase() {
        // selection params
        final String selection = NoteInfoEntry._ID + " = ?";
        final String[] selectionArgs = {Integer.toString(mNoteId)};

        //Setup helper AsyncTask Anon class
        @SuppressLint("StaticFieldLeak") AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
                db.delete(NoteInfoEntry.TABLE_NAME, selection, selectionArgs);
                return null;
            }
        };
        task.execute();

    }

    /*Make sure cancelled state is saved*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save course id
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        //Save Note title
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        //save Note text
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }

    //Store note's previous values
    private void storePreviousNotesValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mOriginalNoteTitle);
        mNote.setText(mOriginalNoteText);
    }

    //Save note on db
    private void saveNoteToDatabase(String courseId, String noteTitle, String noteText) {
        String selection = NoteInfoEntry._ID + " = ?";
        String[] selectionArgs = {Integer.toString(mNoteId)};

        ContentValues values = new ContentValues();
        values.put(NoteInfoEntry.COLUMN_COURSE_ID, courseId);
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE, noteTitle);
        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT, noteText);

        //Get db connection
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        //update values
        db.update(NoteInfoEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    //Save note input
    private void saveNote() {
        //Get course value in spinner from cursor
        String courseId = selectedCourseId();
        //Get value of note title
        String noteTitle = mTextNoteTitle.getText().toString();
        //Get value of note text and set to title field
        String noteText = mTextNoteText.getText().toString();

        saveNoteToDatabase(courseId, noteTitle, noteText);
    }

    private String selectedCourseId() {
        //Check current spinner position
        int selectedPosition = mSpinnerCourses.getSelectedItemPosition();
        //Get cursor reference
        Cursor cursor = mAdapterCourses.getCursor();
        cursor.moveToPosition(selectedPosition);
        //Find index with selected course id
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        String courseId = cursor.getString(courseIdPos);
        return courseId;


    }

    /**
     * Display values from db
     **/
    private void displayNote() {
        //Get note details from cursor
        String courseId = mNoteCursor.getString(mCourseIdPos);
        String noteTitle = mNoteCursor.getString(mNoteTitlePos);
        String noteText = mNoteCursor.getString(mNoteTextPos);

        //Get index of note's course from db list
        int courseIndex = getIndexOfCourseId(courseId);
        //set index to spinner
        mSpinnerCourses.setSelection(courseIndex);
        //Set note title and text getting info from database
        mTextNoteTitle.setText(noteTitle);
        mTextNoteText.setText(noteText);

    }

    private int getIndexOfCourseId(String courseId) {
        //Get cursor from already set adapter
        Cursor cursor = mAdapterCourses.getCursor();
        //Get position index from Id
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        int courseRowIndex = 0;

        boolean more = cursor.moveToFirst();//Ensure cursor is at first position
        while (more) {
            String cursorCourseId = cursor.getString(courseIdPos);
            if (courseId.equals(cursorCourseId)) //Right course found
                break;

            courseRowIndex++;
            more = cursor.moveToNext();
        }
        return courseRowIndex;
    }

    /**Get values for notes to be displayed*/
    //Populates created notes with their content
    private void readDisplayValues() {
        //retrieve intent passed to this activity
        Intent intent = getIntent();
        //get int position

        /*The param "ID_NOT_SET =-1" */
//  When using value types and not reference types for intent extras, two params need to be provided.
// If a reference type was used, it could easily return null but for a value type, this needs to be
// specified if there's no extra with the specified name
        mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);

        //Check if a new note is being created based on the availability of a position value
        mIsNewNote = mNoteId == ID_NOT_SET;
        if (mIsNewNote) { //New note created
            createNewNote();
        }
            //Note has a position already (not a new note)
            //Get Data Manager Instance to provide content at given positions
//        mNote = DataManager.getInstance().getNotes().get(mNoteId);

    }
    /*Method for handling creation of new notes */
    private void createNewNote() {
        //Values to be put in new row in db table
        ContentValues values = new ContentValues();
        //Set up placeholder values
        values.put(NoteInfoEntry.COLUMN_COURSE_ID, "");
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE, "");
        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT, "");
        //db link
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        //Get id of newly created row
        mNoteId = (int) db.insert(NoteInfoEntry.TABLE_NAME, null, values); //Insert values

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    /*Handles menu option*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        }
        //Cancel note
        else if (id == R.id.action_cancel){
            //set flag when user cancels
            mIsCancelling = true;
            //end operations
            finish();
        } else if (id == R.id.action_next) {
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    /*Alter menu item in runtime*/
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        //create index of last note
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(mNoteId < lastNoteIndex);//Confirm that note is last
        return super.onPrepareOptionsMenu(menu);
    }

    /*Move over to next note*/
    private void moveNext() {
        //Save changes to the previous note
        saveNote();
        //increment position
        ++mNoteId;
        //get corresponding note at position
        mNote = DataManager.getInstance().getNotes().get(mNoteId);
        //Show original values if changes were made
        saveOriginalNoteValues();
        //display note at new position
        displayNote();
        //Call on onPrepareOptionsMenu
        invalidateOptionsMenu();
    }

    //Send a mail via implicit intent
    private void sendEmail() {
        //Get course info from spinner
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        //Email subject from note title
        String subject = mTextNoteTitle.getText().toString();
        //email body text
        String text = "Check out this course \""+
                course.getTitle() +"\"\n" + mTextNoteText.getText().toString();
        //Create intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822"); //Email MIME TYPE
        intent.putExtra(Intent.EXTRA_SUBJECT, subject); //Email subject
        intent.putExtra(Intent.EXTRA_TEXT,text); //Email body

        startActivity(intent);
    }

    /**
     * Loader methods
     **/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;//init cursor loader
        if (id == LOADER_NOTES)
            loader = createLoaderNotes();
        else if (id == LOADER_COURSES)
            loader = createLoaderCourses();
        return loader;
    }

    @SuppressLint("StaticFieldLeak")
    private CursorLoader createLoaderCourses() { //Populate spinner with courses list from query
        //Boolean flag
        mCourseQueryFinished = false;
        //Get Uri for content provider
        Uri uri = Courses.CONTENT_URI;
        //Get data columns
        String[] courseColumns = {
                Courses.COLUMN_COURSE_TITLE,
                Courses.COLUMN_COURSE_ID,
                Courses._ID
        };

        //Create instance of cursor loader
        return new CursorLoader(this, uri, courseColumns,
                null, null, Courses.COLUMN_COURSE_TITLE);

    }

    @SuppressLint("StaticFieldLeak")
    private CursorLoader createLoaderNotes() {
        //Boolean flag
        mNotesQueryFinished = false;
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                //Get readable data from open helper
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

                String selection = NoteInfoEntry._ID + " = ?";
                String[] selectionArgs = {Integer.toString(mNoteId)};

                String[] noteColumns = {
                        NoteInfoEntry.COLUMN_COURSE_ID,
                        NoteInfoEntry.COLUMN_NOTE_TITLE,
                        NoteInfoEntry.COLUMN_NOTE_TEXT
                };

                return db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
                        selection, selectionArgs, null, null, null);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Confirm loader ID
        if (loader.getId() == LOADER_NOTES)
            loadFinishedNotes(data);
        else if (loader.getId() == LOADER_COURSES) {
            //Associate cursor with cursor adapter
            mAdapterCourses.changeCursor(data);
            mCourseQueryFinished = true; //finished loading courses
            displayNoteWhenQueryFinished();
        }


    }

    private void loadFinishedNotes(Cursor data) {
        //associate cursor variable with field
        mNoteCursor = data;
        //get cursor positions
        mCourseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        mNoteCursor.moveToNext();
        mNotesQueryFinished = true; //Done loading notes
        displayNoteWhenQueryFinished();
    }

    private void displayNoteWhenQueryFinished() {
        if (mNotesQueryFinished && mCourseQueryFinished)
            displayNote();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {//Clean up resources
        //confirm loader id
        if (loader.getId() == LOADER_NOTES) {
            if (mNoteCursor != null)
                mNoteCursor.close();
        } else if (loader.getId() == LOADER_COURSES) {
            //Set cursor value to null
            mAdapterCourses.changeCursor(null);
        }


    }
}
