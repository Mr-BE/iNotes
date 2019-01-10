package c.codeblaq.inotes;

import android.net.Uri;
import android.provider.BaseColumns;

public final class NoteProviderContract {//

    public static final String AUTHORITY = "c.codeblaq.inotes.provider";
    //Base URI content:// + AUTHORITY
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private NoteProviderContract() {
    }

    //Courses ID coulumns interface
    protected interface CoursesIdColumns {
        String COLUMN_COURSE_ID = "course_id";
    }

    //Course column interface
    protected interface CoursesColumns {
        String COLUMN_COURSE_TITLE = "course_title";
    }

    //Note column interface
    protected interface NotesColumns {
        String COLUMN_NOTE_TITLE = "note_title";
        String COLUMNN_NOTE_TEXT = "note_text";
    }

    //nested class for courses
    public static final class Courses implements BaseColumns, CoursesColumns, CoursesIdColumns {
        public static final String PATH = "courses"; //Courses path constant
        //content://c.codeblaq.inotes.provider/courses
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    //Notes table class
    public static final class Notes implements BaseColumns, NotesColumns, CoursesIdColumns, CoursesColumns {
        public static final String PATH = "notes"; //Courses path constant
        //content://c.codeblaq.inotes.provider/notes
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
        //Constant for notes with course title
        public static final String PATH_EXPANDED = "notes_expanded";
        public static final Uri CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_EXPANDED);
    }
}
