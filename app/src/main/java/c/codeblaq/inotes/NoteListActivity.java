package c.codeblaq.inotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private NoteRecyclerAdapter mNoteRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //Start a new note activity inline
               startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
            }
        });
        
        initializeDisplayContent();
    }

    //Called when app is resumed
    @Override
    protected void onResume() {
        super.onResume();
        //notify adapter of data change
        mNoteRecyclerAdapter.notifyDataSetChanged();
    }


    //Display content list
    private void initializeDisplayContent() {

        //Create variable for recycler view
        final RecyclerView recyclerNotes = findViewById(R.id.list_notes);
        //Create linear layout manager instance
        final LinearLayoutManager notesLayoutManager = new LinearLayoutManager(this);
        //associate recycler view with manager
        recyclerNotes.setLayoutManager(notesLayoutManager);

        //Get notes to be displayed within recycler view
        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        //Create instance of NoteRecyclerAdapter
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, null);
        //Associate adapter with recycler view
        recyclerNotes.setAdapter(mNoteRecyclerAdapter);
    }
}
