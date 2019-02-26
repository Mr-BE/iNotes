package c.codeblaq.inotes;

import android.content.Context;
import android.content.Intent;

public class CourseEventBroadcastHelper {

    public static final String ACTION_COURSE_EVENT = "c.codeblaq.inotes.action.COURSE_EVENT";
    public static final String EXTRA_COURSE_ID = "c.codeblaq.inotes.extra.COURSE_ID";
    public static final String EXTRA_COURSE_MESSAGE = "c.codeblaq.inotes.extra.COURSE_MESSAGE";

    public static void sendEventBroadcast(Context context, String courseId, String message) {

        Intent intent = new Intent(ACTION_COURSE_EVENT); //Create intent
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        intent.putExtra(EXTRA_COURSE_MESSAGE, message);

        context.sendBroadcast(intent);


    }
}
