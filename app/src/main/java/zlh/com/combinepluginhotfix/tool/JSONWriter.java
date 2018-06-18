package zlh.com.combinepluginhotfix.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 张丽华 on 2018/6/18.
 * Description:
 */

public class JSONWriter {
    public static void WriterPluginSetting(String content) {
        File mPluginSettingJson = PH.getBaseContext().getFileStreamPath(JSONParser.PluginSetting);
        byte[] bytes = content.getBytes();
        int b = content.length();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mPluginSettingJson);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Writer(String content, String jsonName) {
        File mPluginSettingJson = PH.getBaseContext().getFileStreamPath(jsonName);
        byte[] bytes = content.getBytes();
        int b = content.length();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mPluginSettingJson);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
