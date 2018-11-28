package c.codeblaq.inotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final String ORIGINAL_NOTE_TEXT = "c.codeblaq.inotes.ORIGINAL_NOTE_TEXT";

    //Constant for NoteInfo extra (use package name to make it unique)
    public static final String NOTE_POSITION = "c.codeblaq.inotes.NOTE_POSITION";
   /*Constant values for original note states */
    public static final String ORIGINAL_NOTE_COURSE_ID = "c.codeblaq.inotes.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "c.codeblaq.inotes.ORIGINAL_NOTE_TITLE";
    private final String TAG = getClass().getSimpleName();

    public static final int POSITION_NOT_SET = -1;

    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNotePosition;
    private boolean mIsCancelling;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Instantiate spinner
        mSpinnerCourses = findViewById(R.id.spinner_courses);
        //Get list for spinner
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        //Create adapter for  spinner list
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        //Drop down for list of courses
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Associate adapter with spinner
        mSpinnerCourses.setAdapter(adapterCourses);

        //Read values from intent
        readDisplayValues();

        //Save original note input
       if (savedInstanceState == null){ // opened for the first time
           saveOriginalNoteValues();
       } else{ //recreated activity
           restoreOriginalNoteValues(savedInstanceState);
       }


        //Reference editText fields for the notes
        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        //display spinners and editTexts (Notes contents)
        if (!mIsNewNote) {//No need for saved content
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);
        }
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
            //Get DataManager instance and remove note if new note
            if(mIsNewNote) {
                DataManager.getInstance().removeNote(mNotePosition);
            } else {
                storePreviousNotesValues();
            }
        }
        else{
            saveNote();
        }
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

    //Save note input
    private void saveNote() {
        //Set course value to what is in spinner
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        //Get value of note title and set to title field
        mNote.setTitle(mTextNoteTitle.getText().toString());
        //Get value of note text and set to title field
        mNote.setText(mTextNoteText.getText().toString());
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        //Get course list from data manager
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        //Get index of note's course from within list
        int courseIndex = courses.indexOf(mNote.getCourse());
        //set index to spinner
        spinnerCourses.setSelection(courseIndex);

        //Set note title and text getting info from parcel
        textNoteTitle.setText(mNote.getTitle());
        Log.v("NoteActivity: ", "Note title is "+ mNote.getTitle());
        textNoteText.setText(mNote.getText());
        Log.v("NoteActivity: ", "Note text is "+ mNote.getText());


    }

    /**Get values for notes to be displayed*/
    //Populates created notes with their content
    private void readDisplayValues() {
        //retrieve intent passed to this activity
        Intent intent = getIntent();
        //get int position

        /*The param "POSITION_NOT_SET =-1" */
//  When using value types and not reference types for intent extras, two params need to be provided.
// If a reference type was used, it could easily return null but for a value type, this needs to be
// specified if there's no extra with the specified name
        mNotePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);

        //Check if a new note is being created based on the availability of a position value
        mIsNewNote = mNotePosition == POSITION_NOT_SET;
        if (mIsNewNote) { //New note created
            createNewNote();
        }
            //Note has a position already (not a new note)
            //Get Data Manager Instance to provide content at given positions
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);
    }
    /*Method for handling creation of new notes */
    private void createNewNote() {
        DataManager dm = DataManager.getInstance(); //Local instance of DataManager
        //Call Data Manager's "createNewNote" feature and get position
        mNotePosition = dm.createNewNote();
        //Get note at position and assign to "mNote"
//        mNote = dm.getNotes().get(mNotePosition);
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
        }

        return super.onOptionsItemSelected(item);
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
}
