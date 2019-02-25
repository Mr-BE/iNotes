package c.codeblaq.inotes;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.net.Uri;
import android.os.AsyncTask;

public class NoteUploaderJobService extends JobService { //implement job 2nd
    //Job Extra
    public static final String EXTRA_DATA_URI = "c.codeblaq.inotes.extras.DATA_URI";
    private NoteUploader mNoteUploader;

    public NoteUploaderJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        //Background work
        @SuppressLint("StaticFieldLeak") AsyncTask<JobParameters, Void, Void> task = new AsyncTask<JobParameters, Void, Void>() {
            @Override
            protected Void doInBackground(JobParameters... backgroundParams) {
                JobParameters jobParameters = backgroundParams[0];
                String stringDataUri = jobParameters.getExtras().getString(EXTRA_DATA_URI);
                Uri dataUri = Uri.parse(stringDataUri);
                mNoteUploader.doUpload(dataUri);

                if (!mNoteUploader.isCanceled())
                    //notify when job is done
                    jobFinished(jobParameters, false); //TRUE reschedule job; FALSE job done
                return null;
            }
        };

        mNoteUploader = new NoteUploader(this);
        //start job
        task.execute(params);
        return true; //Alert job scheduler that job should be run to the end
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mNoteUploader.cancel(); //stop job
        return true; //true == job to be rescheduled
    }
}
