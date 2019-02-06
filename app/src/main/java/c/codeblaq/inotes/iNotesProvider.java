package c.codeblaq.inotes;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
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
    public static final String MIME_VENDOR_TYPE = "vnd." + NoteProviderContract.AUTHORITY + ".";
    //Create URI matcher (NO_MATCH == attempt to access void of authority)
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int NOTES_ROW = 3;

    //initialize static field
    static {
        //Courses table URI
        sUriMatcher.addURI(NoteProviderContract.AUTHORITY, Courses.PATH, COURSES);
        //notes
        sUriMatcher.addURI(NoteProviderContract.AUTHORITY, Notes.PATH, NOTES);
        //expanded notes
        sUriMatcher.addURI(NoteProviderContract.AUTHORITY, Notes.PATH_EXPANDED, NOTES_EXPANDED);
        //note row id (specific URI)
        sUriMatcher.addURI(NoteProviderContract.AUTHORITY, Notes.PATH + "/#", NOTES_ROW);
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
    public String getType(Uri uri) { //Handle MIME type requests
        String mimeType = null;

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case COURSES: // vnd.android.cursor.dir/vnd.c.codeblaq.inotes.provider.courses
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Courses.PATH;
                break;
            case NOTES:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.PATH;
                break;
            case NOTES_EXPANDED:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.PATH_EXPANDED;
                break;
            case NOTES_ROW:
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.PATH;
        }

        return mimeType;

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //Get db ref
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        long rowId = -1; //Row id init
        Uri rowUri = null; //Row uri init
        int uriMatch = sUriMatcher.match(uri);
        //Determine uri value
        switch (uriMatch) {
            case NOTES://insert values into db table and store id
                rowId = db.insert(NoteInfoEntry.TABLE_NAME, null, values);
                //content://c.codeblaq.inotes.provider/notes/1
                rowUri = ContentUris.withAppendedId(Notes.CONTENT_URI, rowId);
                break;
            case COURSES:
                rowId = db.insert(CourseInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(Courses.CONTENT_URI, rowId);
                break;
            case NOTES_EXPANDED:
                //throws exception that it is a read only table
                break;
        }

        return rowUri;
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
                break;
            case NOTES_ROW:
                long rowId = ContentUris.parseId(uri); //Get specific row Id from URI
                //Selection criteria
                String rowSelection = NoteInfoEntry._ID + " = ?"; //clause
                String[] rowSelectionArgs = new String[]{Long.toString(rowId)};
                cursor = db.query(NoteInfoEntry.TABLE_NAME, projection, rowSelection,
                        rowSelectionArgs, null, null, null);
                break;
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
