package zlh.com.combinepluginhotfix.download;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import zlh.com.combinepluginhotfix.activity.MainActivity;
import zlh.com.combinepluginhotfix.hook.loadedapk.ApkLoader;
import zlh.com.combinepluginhotfix.tool.FileHelper;

import static zlh.com.combinepluginhotfix.application.App.PLUGIN_JNI_PKGNAME;
import static zlh.com.combinepluginhotfix.application.App.PLUGIN_TWO_PKGNAME;
import static zlh.com.combinepluginhotfix.tool.PH.getBaseContext;

/**
 * Created by shs1330 on 2018/3/28.
 */

public class DownloadPatchTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = "DownloadPatchTask";
    private MainActivity activity;
    private byte[] result = null;
    public DownloadPatchTask(MainActivity activity) {
        this.activity = activity;
    }
    private String apkName;
    private String downType;
    @Override
    protected String doInBackground(String... params) {
        apkName = params[0];
        downType = params[1];
        return download(params[2]);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        activity.publishProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        activity.dismissDialog();
        activity.downImage.setImageBitmap(BitmapFactory.decodeByteArray(result, 0, result.length));

        FileHelper.storeFile(apkName, downType, result);
        if (downType.equals("Apk")) {
            ApkLoader.hook(getBaseContext().getFileStreamPath(apkName), ApkLoader.getPluginClassLoader(PLUGIN_JNI_PKGNAME));
            ApkLoader.callPluginApplicationCreate(PLUGIN_TWO_PKGNAME);
        }
    }

    private String download(String str) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpURLConnection conn = null;
        String resultStr = "";
        try {
            URL url = new URL(str);
            conn = (HttpURLConnection) url.openConnection();

            conn.connect();
            // 获取输入流
            inputStream = conn.getInputStream();
            // 获取文件流大小，用于更新进度
            long file_length = conn.getContentLength();
            Log.d(TAG, "download: " + file_length);
            int len;
            int total_length = 0;
            byte[] data = new byte[1024];
            while ((len = inputStream.read(data)) != -1) {
                total_length += len;
                int value = (int) ((total_length / (float) file_length) * 100);
                // 调用update函数，更新进度
                publishProgress(value);
                outputStream.write(data, 0, len);
            }
            result = outputStream.toByteArray();
            resultStr = result.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return resultStr;
    }

}
