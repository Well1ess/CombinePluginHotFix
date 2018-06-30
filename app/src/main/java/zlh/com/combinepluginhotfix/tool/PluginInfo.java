package zlh.com.combinepluginhotfix.tool;

import org.json.JSONException;
import org.json.JSONObject;

import static zlh.com.combinepluginhotfix.tool.JSONParser.ApkMain;
import static zlh.com.combinepluginhotfix.tool.JSONParser.ApkMainType;
import static zlh.com.combinepluginhotfix.tool.JSONParser.ApkName;
import static zlh.com.combinepluginhotfix.tool.JSONParser.DependPlugin;
import static zlh.com.combinepluginhotfix.tool.JSONParser.PackName;
import static zlh.com.combinepluginhotfix.tool.JSONParser.Version;

/**
 * Created by 张丽华 on 2018/6/18.
 * Description:
 */
public class PluginInfo {
    public String apkName;
    public String packageName;
    public String apkMainType;
    public String apkMain;
    public String dependPlugin;
    public float version;

    public static PluginInfo CreatePluginInfo(JSONObject jsonObject) {
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
        this.packageName = packName;
        this.apkMainType = apkMainType;
        this.apkMain = apkMain;
        this.dependPlugin = dependPlugin;
        this.version = Float.parseFloat(version);
    }

    @Override
    public String toString() {
        return "PluginInfo{" +
                "apkName='" + apkName + '\'' +
                ", packName='" + packageName + '\'' +
                ", apkMainType='" + apkMainType + '\'' +
                ", apkMain='" + apkMain + '\'' +
                ", dependPlugin='" + dependPlugin + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}