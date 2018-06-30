package zlh.com.combinepluginhotfix.tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by shs1330 on 2017/10/30.
 * {
 * apkName:
 * packageName:
 * apkMainType:{Fragment,Activity}
 * apkMain:
 * version:1.0
 * }
 * PluginSetting.json
 */
public class JSONParser {
    public final static String PluginSetting = "PluginSetting.json";
    public final static String ApkName = "apkName";
    public final static String PackName = "packageName";
    public final static String ApkMainType = "apkMainType";
    public final static String ApkMain = "apkMain";
    public final static String DependPlugin = "dependPlugin";
    public final static String Version = "version";
    public final static String DownloadUrl = "downloadUrl";

    public final static String PatchName = "patchName";

    private static final String TAG = "JSONParser";

    public static List<PluginInfo> parser() {
        FileHelper.extractAssets(PluginSetting);
        String jsonContent = "";
        File file = PH.getBaseContext().getFileStreamPath(PluginSetting);
        InputStream inputStream = null;
        ArrayList<PluginInfo> pluginInfos = new ArrayList<>();

        try {
            inputStream = new FileInputStream(file);
            byte[] buf = new byte[1024];
            while (inputStream.read(buf) > 0) {
                jsonContent += new String(buf, "utf-8");
            }
            try {
                JSONArray jsonArray = new JSONArray(jsonContent);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    pluginInfos.add(PluginInfo.CreatePluginInfo(jsonObject));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pluginInfos;
    }

    public static List<PluginInfo> parser(String content) {
        ArrayList<PluginInfo> pluginInfos = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                pluginInfos.add(PluginInfo.CreatePluginInfo(jsonObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pluginInfos;
    }

    public static List<UpdateInfo> parserUpdateInfo(String content) {
        ArrayList<UpdateInfo> updateInfos = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                updateInfos.add(UpdateInfo.CreateUpdateInfo(jsonObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return updateInfos;
    }

    /**
     * 解析patch json
     *
     * @param patchJsonPath
     */
    public static List<PatchInfo> parserPatch(String patchJsonPath) {
        PatchInfoListComparator comparator = new PatchInfoListComparator();
        File file = PH.getBaseContext().getFileStreamPath(patchJsonPath);
        InputStream inputStream = null;
        ArrayList<PatchInfo> patchInfos = new ArrayList<>();
        String jsonContent = "";
        try {
            inputStream = new FileInputStream(file);
            byte[] buf = new byte[1024];
            while (inputStream.read(buf) > 0) {
                jsonContent += new String(buf, "utf-8");
            }

            try {
                JSONArray jsonArray = new JSONArray(jsonContent);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    patchInfos.add(PatchInfo.CreatePatchInfo(jsonObject));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(patchInfos, comparator);
        return patchInfos;
    }

    public static class PatchInfoListComparator implements Comparator<PatchInfo> {

        @Override
        public int compare(PatchInfo o1, PatchInfo o2) {
            if (o1.version > o2.version)
                return 1;
            else if (o1.version < o2.version)
                return -1;
            else
                return 0;
        }
    }
}
