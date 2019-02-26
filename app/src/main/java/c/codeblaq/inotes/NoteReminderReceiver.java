package c.codeblaq.inotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class NoteReminderReceiver extends BroadcastReceiver {
    public static final String EXTRA_NOTE_TITLE = "c.codeblaq.inotes.extra.NOTE_TITLE";
    public static final String EXTRA_NOTE_TEXT = "c.codeblaq.inotes.extra.NOTE_TEXT";
    public static final String EXTRA_NOTE_ID = "c.codeblaq.inotes.extra.NOTE_ID";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        //Get intent extras as variables
        String noteTitle = intent.getStringExtra(EXTRA_NOTE_TITLE);
        String noteText = intent.getStringExtra(EXTRA_NOTE_TEXT);
        int noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0);

        NoteReminderNotification.notify(context, noteTitle, noteText, noteId);
    }
}
