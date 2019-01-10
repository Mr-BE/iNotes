package c.codeblaq.inotes;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import c.codeblaq.inotes.NoteDatabaseContract.CourseInfoEntry;
import c.codeblaq.inotes.NoteDatabaseContract.NoteInfoEntry;
import c.codeblaq.inotes.NoteProviderContract.Courses;
import c.codeblaq.inotes.NoteProviderContract.CoursesIdColumns;
import c.codeblaq.inotes.NoteProviderContract.Notes;

public class iNotesProvider extends ContentProvider {
    public static final int COURSES = 0;
    public static final int NOTES = 1;
    public static final int NOTES_EXPANDED = 2;
    //Create URI matcher (NO_MATCH == attempt to access void of authority)
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //initialize static field
    static {
        //Courses table URI
        sUriMatcher.addURI(NoteProviderContract.AUTHORITY, Courses.PATH, COURSES);
        //notes
        sUriMatcher.addURI(NoteProviderContract.AUTHORITY, Notes.PATH, NOTES);
        //expanded notes
        sUriMatcher.addURI(NoteProviderContract.AUTHORITY, Notes.PATH_EXPANDED, NOTES_EXPANDED);
    }

    //Set dbOpenHelper to connect to DB
    private NoteOpenHelper mDbOpenHelper;

    public iNotesProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        ///Create instance of openHelper
        mDbOpenHelper = new NoteOpenHelper(getContext()); //"this" N/A
        return true;
    }

    /**
     * query
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null; //init cursor
        //get db ref
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        //Check received Uri
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case COURSES:
                //Query provider for courses
                cursor = db.query(CourseInfoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case NOTES:
                cursor = db.query(NoteInfoEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case NOTES_EXPANDED:
                cursor = notesExpandedQuery(db, projection, selection, selectionArgs, sortOrder);
        }

        return cursor;
    }

    private Cursor notesExpandedQuery(SQLiteDatabase db, String[] projection, String selection,
                                      String[] selectionArgs, String sortOrder) {

        //Table qualify columns
        String[] columns = new String[projection.length];
        for (int idx = 0; idx < projection.length; idx++) {
            //If current value of project = BaseColumns, table qualify column name
            columns[idx] = projection[idx].equals(BaseColumns._ID) ||
                    projection[idx].equals(CoursesIdColumns.COLUMN_COURSE_ID) ?
                    NoteInfoEntry.getQName(projection[idx]) : projection[idx];
        }

        //Join tables
        String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " +
                CourseInfoEntry.TABLE_NAME + " ON " +
                NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
                CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

        //query
        return db.query(tablesWithJoin, columns, selection, selectionArgs,
                null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
