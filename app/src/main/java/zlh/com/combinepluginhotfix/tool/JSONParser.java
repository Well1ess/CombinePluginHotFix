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
    private final static String PluginSetting = "PluginSetting.json";
    public final static String ApkName = "apkName";
    public final static String PackName = "packageName";
    public final static String ApkMainType = "apkMainType";
    public final static String ApkMain = "apkMain";
    public final static String DependPlugin = "dependPlugin";
    public final static String Version = "version";

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

    public static class PluginInfo{
        public String apkName;
        public String packName;
        public String apkMainType;
        public String apkMain;
        public String dependPlugin;
        public String version;

        public static PluginInfo CreatePluginInfo(JSONObject jsonObject){
            try {
                PluginInfo pluginInfo = new PluginInfo(jsonObject.getString(ApkName),
                        jsonObject.getString(PackName),
                        jsonObject.getString(ApkMainType),
                        jsonObject.getString(ApkMain),
                        jsonObject.getString(DependPlugin),
                        jsonObject.getString(Version));
                return pluginInfo;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public PluginInfo() {
        }

        public PluginInfo(String apkName, String packName, String apkMainType, String apkMain, String dependPlugin, String version) {
            this.apkName = apkName;
            this.packName = packName;
            this.apkMainType = apkMainType;
            this.apkMain = apkMain;
            this.dependPlugin = dependPlugin;
            this.version = version;
        }

        @Override
        public String toString() {
            return "PluginInfo{" +
                    "apkName='" + apkName + '\'' +
                    ", packName='" + packName + '\'' +
                    ", apkMainType='" + apkMainType + '\'' +
                    ", apkMain='" + apkMain + '\'' +
                    ", dependPlugin='" + dependPlugin + '\'' +
                    ", version='" + version + '\'' +
                    '}';
        }
    }
}
