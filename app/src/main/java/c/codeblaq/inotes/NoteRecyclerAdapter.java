package c.codeblaq.inotes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {
    //Context for Activity
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    //list for notes
    private final List<NoteInfo> mNotes;

    /**
     * NoteRecyclerAdapter class constructor
     */
    public NoteRecyclerAdapter(Context context, List<NoteInfo> notes) {
        mContext = context;
        mNotes = notes;
        //Layout inflater to get views from context
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Create a view for the view holder (Inflate View, create new viewholder instance and associate view with instance)
        View itemView = mLayoutInflater.inflate(R.layout.item_note_list, parent, false);
        //False simply means this View should not automatically attach to its parent viewGroup but depend on the adapter

        return new ViewHolder(itemView);
    }

    /*Bind data to views*/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get note corresponding to position
        NoteInfo note = mNotes.get(position);
        //Get each Text Views from viewholder
        holder.mTextCourses.setText(note.getCourse().getTitle());
        holder.mTextTitle.setText(note.getTitle());
        holder.mCurrentPosition = position;
    }

    /*Determine the number of items in the list*/
    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    /**
     * View holder class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextCourses;
        public final TextView mTextTitle;
        //Viewholder position
        public int mCurrentPosition;

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
                    intent.putExtra(NoteActivity.NOTE_POSITION, mCurrentPosition);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
