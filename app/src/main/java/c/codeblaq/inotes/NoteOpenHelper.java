package c.codeblaq.inotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import c.codeblaq.inotes.NoteDatabaseContract.CourseInfoEntry;
import c.codeblaq.inotes.NoteDatabaseContract.NoteInfoEntry;

public class NoteOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "iNotes.db";
    public static final int DATABASE_VERSION = 2;

    public NoteOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create tables
        db.execSQL(CourseInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(NoteInfoEntry.SQL_CREATE_TABLE);
        //Create Indexes
        db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
        db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);
        //Create instance of DBDataWorker
        DatabaseDataWorker worker = new DatabaseDataWorker(db);
        worker.insertCourses();
        worker.insertSampleNotes();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CourseInfoEntry.SQL_CREATE_TABLE);
            db.execSQL(NoteInfoEntry.SQL_CREATE_TABLE);
        }
    }
}
