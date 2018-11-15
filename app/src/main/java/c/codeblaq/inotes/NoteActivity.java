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

    //Constant for NoteInfo extra (use package name to make it unique)
    public static final String NOTE_POSITION = "c.codeblaq.inotes.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;

    private NoteInfo mNote;
    private boolean mIsNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Instantiate spinner
        Spinner spinnerCourses = findViewById(R.id.spinner_courses);
        //Get list for spinner
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        //Create adapter for  spinner list
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        //Drop down for list of courses
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Associate adapter with spinner
        spinnerCourses.setAdapter(adapterCourses);
        
        //Read values from intent
        readDisplayValues();

        //Reference editText fields for the notes
        EditText textNoteTitle = findViewById(R.id.text_note_title);
        EditText textNoteText = findViewById(R.id.text_note_text);

        //display spinners and editTexts (Notes contents)
        if (!mIsNewNote){//No need for saved content
            displayNote(spinnerCourses, textNoteTitle, textNoteText);
        }
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

    /*Get values for notes to be displayed*/
    //Populates created notes with their content
    private void readDisplayValues() {
        //retrieve intent passed to this activity
        Intent intent = getIntent();
        //get int position

        /*The param "POSITION_NOT_SET =-1" */
//  When using value types and not reference types for intent extras, two params need to be provided. If a reference type was used, it could easily return null but for a value type, this needs to be specified if there's no extra with the specified name
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);

        //Check if a new note is being created based on the availability of a position value
        mIsNewNote = position == POSITION_NOT_SET;
        if (!mIsNewNote){ //Note has a position already (not a new note)
            //Get Data Manager Instance to provide content at given positions
            mNote = DataManager.getInstance().getNotes().get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
