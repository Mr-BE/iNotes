package c.codeblaq.inotes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jim.
 */

public final class NoteInfo implements Parcelable {
    /*Use to create new instances of type NoteInfo*/
    public final static Parcelable.Creator<NoteInfo> CREATOR =
            new Parcelable.Creator<NoteInfo>() {
                @Override
                //Create new instance of type NoteInfo and set values
                //createFromParcel and writeToParcel methods should have the same order of values
                public NoteInfo createFromParcel(Parcel source) {
                    return new NoteInfo(source);
                }

                @Override
                //Creates an array of type NoteInfo
                public NoteInfo[] newArray(int size) {
                    return new NoteInfo[size];
                }
            };
    private int mId;
    private CourseInfo mCourse;
    private String mTitle;
    private String mText;

    /*Constructors*/
    public NoteInfo(CourseInfo course, String title, String text) {
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    public NoteInfo(int id, CourseInfo course, String title, String text) {
        mId = id;
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    //Constructor for parcel
    //Read variables in the same order they were instantiated
    private NoteInfo(Parcel source) {
        //Class loader info is needed for readParcelable method
        mCourse = source.readParcelable(CourseInfo.class.getClassLoader());
        mTitle = source.readString();
        mText = source.readString();
    }

    public CourseInfo getCourse() {
        return mCourse;
    }

    public void setCourse(CourseInfo course) {
        mCourse = course;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mCourse.getCourseId() + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo that = (NoteInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    /**
     * Saving side of parceling
     * handled by describeContents() and writeToParcel()
     */
    @Override
    public int describeContents() {
        //return 0 if no special parcelable needs are required
        return 0;
    }

    //Writes member info of type instance into the parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //For special objects, we use the "writeParceable" method
        dest.writeParcelable(mCourse, 0);
        dest.writeString(mTitle);
        dest.writeString(mText);
        //The "mTitle" and "mText" variables above can use the default methods because they are of primitive data types

    }
}
