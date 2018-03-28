package zlh.com.combinepluginhotfix.download;

import android.os.AsyncTask;
import android.util.Log;

import zlh.com.combinepluginhotfix.activity.MainActivity;

/**
 * Created by shs1330 on 2018/3/28.
 */

public class DownloadPatchTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = "DownloadPatchTask";
    private MainActivity activity;
    private long totalSize = 4 * 1024;
    public DownloadPatchTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        for (int i = 0; i < totalSize; i++) {
            publishProgress(i);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        activity.publishProgress((values[0] * 100)/ (totalSize));
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        activity.dismissDialog();
    }
}
