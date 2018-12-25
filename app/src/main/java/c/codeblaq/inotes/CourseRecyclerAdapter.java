package c.codeblaq.inotes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {
    //Context for Activity
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    //list for notes
    private final List<CourseInfo> mCourses;

    /**
     * NoteRecyclerAdapter class constructor
     */
    public CourseRecyclerAdapter(Context context, List<CourseInfo> courses) {
        mContext = context;
        mCourses = courses;
        //Layout inflater to get views from context
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Create a view for the view holder (Inflate View, create new viewholder instance and associate view with instance)
        View itemView = mLayoutInflater.inflate(R.layout.item_course_list, parent, false);
        //False simply means this View should not automatically attach to its parent viewGroup but depend on the adapter

        return new ViewHolder(itemView);
    }

    /*Bind data to views*/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get note corresponding to position
        CourseInfo course = mCourses.get(position);
        //Get each Text Views from viewholder
        holder.mTextCourses.setText(course.getTitle());
        holder.mCurrentPosition = position;
    }

    /*Determine the number of items in the list*/
    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    /**
     * View holder class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextCourses;
        //Viewholder position
        public int mCurrentPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            //Get reference to TextViews for notes
            mTextCourses = itemView.findViewById(R.id.text_course);

            //Set up OnClickListener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Show courses
                    Snackbar.make(v, mCourses.get(mCurrentPosition).getTitle(),
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
