package c.codeblaq.inotes;

import android.provider.BaseColumns;

public final class NoteDatabaseContract {
    private NoteDatabaseContract() {
    }// Non-creatable

    //Nested class for Courses table (name + "Entry")
    public static final class CourseInfoEntry implements BaseColumns {
        //Table property constants
        public static final String TABLE_NAME = "course_info";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";

        //CREATE INDEX course_info_index ON course_info (course_title)
        public static final String INDEX1 = "_index1"; //value for index
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                        "(" + COLUMN_COURSE_TITLE + ")";
        //SQL  CREATE command
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_COURSE_ID + " TEXT UNIQUE NOT NULL, " +
                COLUMN_COURSE_TITLE + " TEXT NOT NULL)";

        //Get columns
        public static final String getQName(String columnName) {
            return TABLE_NAME + "." + columnName;
        }
    }

    public static final class NoteInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "note_info";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";

        //Create Note Index
        public static final String INDEX1 = "_index"; //value for index
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                        "(" + COLUMN_NOTE_TITLE + ")";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NOTE_TITLE + " TEXT NOT NULL, " +
                COLUMN_NOTE_TEXT + " TEXT, " +
                COLUMN_COURSE_ID + " TEXT NOT NULL)";

        public static final String getQName(String columnName) {
            return TABLE_NAME + "." + columnName;
        }
    }
}
