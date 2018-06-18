package zlh.com.combinepluginhotfix.tool;

import org.json.JSONException;
import org.json.JSONObject;

import static zlh.com.combinepluginhotfix.tool.JSONParser.ApkName;
import static zlh.com.combinepluginhotfix.tool.JSONParser.Version;

/**
 * Created by 张丽华 on 2018/6/18.
 * Description:
 */

public class PatchInfo {
    public String pluginName;
    public String patchName;
    public float version;

    public static PatchInfo CreatePatchInfo(JSONObject jsonObject) {
        try {
            PatchInfo patchInfo = new PatchInfo(jsonObject.getString(ApkName),
                    jsonObject.getString(ApkName),
                    Float.parseFloat(jsonObject.getString(Version)));
            return patchInfo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PatchInfo() {
    }

    public PatchInfo(String pluginName, String patchName, float version) {
        this.pluginName = pluginName;
        this.patchName = patchName;
        this.version = version;
    }

    @Override
    public String toString() {
        return "PatchInfo{" +
                "pluginName='" + pluginName + '\'' +
                ", patchName='" + patchName + '\'' +
                ", version=" + version +
                '}';
    }

}