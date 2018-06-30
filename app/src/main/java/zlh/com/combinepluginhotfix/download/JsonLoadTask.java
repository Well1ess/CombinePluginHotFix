package zlh.com.combinepluginhotfix.download;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import zlh.com.combinepluginhotfix.activity.MainActivity;
import zlh.com.combinepluginhotfix.tool.JSONParser;

/**
 * Created by shs1330 on 2018/3/28.
 */

public class JsonLoadTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = "JsonLoadTask";
    private MainActivity app;
    public JsonLoadTask(MainActivity app) {
        this.app = app;
    }

    @Override
    protected String doInBackground(String... params) {
        return download(params[0]);
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        app.setNetworkPluginList(JSONParser.parserUpdateInfo(s));
    }

    private String download(String str) {
        InputStream inputStream = null;
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(str);
            conn = (HttpURLConnection) url.openConnection();

            conn.connect();
            // 获取输入流
            InputStreamReader read = new InputStreamReader(conn.getInputStream());
            reader = new BufferedReader(read);
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return sb.toString();
    }

}
