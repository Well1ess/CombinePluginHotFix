package zlh.com.combinepluginhotfix.application;

import android.app.Application;
import android.content.Context;

import java.util.List;

import zlh.com.combinepluginhotfix.hook.ams.ProxyActivityManagerService;
import zlh.com.combinepluginhotfix.hook.h.HookCallback;
import zlh.com.combinepluginhotfix.hook.instrumentation.CustomInstrumentation;
import zlh.com.combinepluginhotfix.hook.loadedapk.ApkLoader;
import zlh.com.combinepluginhotfix.hook.pms.ProxyPackageManagerService;
import zlh.com.combinepluginhotfix.tool.FileHelper;
import zlh.com.combinepluginhotfix.tool.JSONParser;
import zlh.com.combinepluginhotfix.tool.PH;

/**
 * Created by shs1330 on 2018/3/12.
 */

public class App extends Application {
    private static final String TAG = "App";
    private static final String PLUGIN_ONE = "PluginOne.apk";
    private static final String PLUGIN_TWO = "PluginTwo.apk";
    private static final String PLUGIN_JNI = "Jni.apk";

    private static final String PATCH_P_ONE = "PluginOne.patch";

    public static final String SOURCE_PKGNAME = "zlh.com.combinepluginhotfix";
    public static final String PLUGIN_ONE_PKGNAME = "nim.shs1330.netease.com.pluginone";
    public static final String PLUGIN_TWO_PKGNAME = "zlh.com.plugintwo";
    public static final String PLUGIN_JNI_PKGNAME = "netease.com.jnisot";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PH.init(base);
        List<JSONParser.PluginInfo> pluginInfos = JSONParser.parser();

        CustomInstrumentation.hook();
        ProxyActivityManagerService.hook();
        ProxyPackageManagerService.hook();
        HookCallback.hook();

//        for (int i = 0; i < pluginInfos.size(); i++) {
//            Log.d(TAG, "attachBaseContext: " + pluginInfos.get(i).apkName);
//            FileHelper.extractAssets(pluginInfos.get(i).apkName);
//            ApkLoader.hook(getFileStreamPath(pluginInfos.get(i).apkName), TextUtils.isEmpty(pluginInfos.get(i).dependPlugin) ? null : ApkLoader.getPluginClassLoader(pluginInfos.get(i).dependPlugin));
//            ApkLoader.callPluginApplicationCreate(pluginInfos.get(i).packName);
//        }
        FileHelper.extractAssets(PLUGIN_JNI);
        FileHelper.extractAssets(PLUGIN_ONE);
        FileHelper.extractAssets(PLUGIN_TWO);
        FileHelper.extractPatch(PATCH_P_ONE);

        ApkLoader.hook(getFileStreamPath(PLUGIN_JNI), null);
        ApkLoader.hook(getFileStreamPath(PLUGIN_ONE), ApkLoader.getPluginClassLoader(PLUGIN_JNI_PKGNAME));
        ApkLoader.hook(getFileStreamPath(PLUGIN_TWO), ApkLoader.getPluginClassLoader(PLUGIN_JNI_PKGNAME));

        ApkLoader.installPatch(PLUGIN_ONE_PKGNAME, PATCH_P_ONE);

        ApkLoader.callPluginApplicationCreate(PLUGIN_JNI_PKGNAME);
        ApkLoader.callPluginApplicationCreate(PLUGIN_ONE_PKGNAME);
        ApkLoader.callPluginApplicationCreate(PLUGIN_TWO_PKGNAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
