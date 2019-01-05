package c.codeblaq.inotes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import c.codeblaq.inotes.NoteDatabaseContract.CourseInfoEntry;
import c.codeblaq.inotes.NoteDatabaseContract.NoteInfoEntry;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {
    //Context for Activity
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    //cursor having list for notes
    private Cursor mCursor;
    private int mCoursePos;
    private int mNoteTitlePos;
    private int mIdPos;

    /**
     * NoteRecyclerAdapter class constructor
     */
    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        //Layout inflater to get views from context
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPosition();
    }

    private void populateColumnPosition() {//Get index of column to use in adapter
        //Check if cursor is null
        if (mCursor == null)
            return;
        //Get Column indexes from cursor
        mCoursePos = mCursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE);
        mNoteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mIdPos = mCursor.getColumnIndex(NoteInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor) {
        if (mCursor != null) //Cursor exists
            mCursor.close();
        mCursor = cursor;
        populateColumnPosition(); //ensure that new cursor has required columns
        notifyDataSetChanged();//Inform recycler view that data has changed


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Create a view for the view holder (Inflate View, create new viewholder instance and associate view with instance)
        View itemView = mLayoutInflater.inflate(R.layout.item_note_list, parent, false);
        //False simply means this View should not automatically attach to its parent viewGroup but depend on the adapter

        return new ViewHolder(itemView);
    }

    /*Display data at specific positions*/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //move cursor to right row
        mCursor.moveToPosition(position);
        //Get values at position
        String course = mCursor.getString(mCoursePos);
        String noteTitle = mCursor.getString(mNoteTitlePos);
        int id = mCursor.getInt(mIdPos);

        //Get each Text Views from viewholder
        holder.mTextCourses.setText(course);
        holder.mTextTitle.setText(noteTitle);
        holder.mId = id;
    }

    /*Determine the number of items in the list*/
    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    /**
     * View holder class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextCourses;
        public final TextView mTextTitle;
        //Viewholder position
        public int mId;

        public ViewHolder(View itemView) {
            super(itemView);
            //Get reference to TextViews for notes
            mTextCourses = itemView.findViewById(R.id.text_course);
            mTextTitle = itemView.findViewById(R.id.text_title);

            //Set up OnClickListener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //intent for clicking on note views
                    Intent intent = new Intent(mContext, NoteActivity.class);
                    //put position as intent extra
                    intent.putExtra(NoteActivity.NOTE_ID, mId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
