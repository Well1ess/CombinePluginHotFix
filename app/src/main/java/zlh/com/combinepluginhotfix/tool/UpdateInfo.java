package zlh.com.combinepluginhotfix.tool;

import org.json.JSONException;
import org.json.JSONObject;

import static zlh.com.combinepluginhotfix.tool.JSONParser.ApkMainType;
import static zlh.com.combinepluginhotfix.tool.JSONParser.ApkName;
import static zlh.com.combinepluginhotfix.tool.JSONParser.DownloadUrl;
import static zlh.com.combinepluginhotfix.tool.JSONParser.Version;

/**
 * Created by shs1330 on 2018/6/30.
 */

public class UpdateInfo {
    public String apkName;
    public float version;
    public String downloadUrl;
    public String apkMainType;

    public static UpdateInfo CreateUpdateInfo(JSONObject jsonObject) {
        try {
            UpdateInfo patchInfo = new UpdateInfo(jsonObject.getString(ApkName),
                    Float.parseFloat(jsonObject.getString(Version)),
                    jsonObject.getString(DownloadUrl),
                    jsonObject.getString(ApkMainType));
            return patchInfo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UpdateInfo(String apkName, float version, String downloadUrl, String type) {
        this.apkName = apkName;
        this.version = version;
        this.downloadUrl = downloadUrl;
        this.apkMainType = type;
    }

    @Override
    public String toString() {
        return "UpdateInfo{" +
                "apkName='" + apkName + '\'' +
                ", version='" + version + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", type='" + apkMainType + '\'' +
                '}';
    }
}
