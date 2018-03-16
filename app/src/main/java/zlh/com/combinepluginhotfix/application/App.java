package zlh.com.combinepluginhotfix.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import zlh.com.combinepluginhotfix.hook.ams.ProxyActivityManagerService;
import zlh.com.combinepluginhotfix.hook.h.HookCallback;
import zlh.com.combinepluginhotfix.hook.instrumentation.CustomInstrumentation;
import zlh.com.combinepluginhotfix.hook.loadedapk.ApkLoader;
import zlh.com.combinepluginhotfix.hook.pms.ProxyPackageManagerService;
import zlh.com.combinepluginhotfix.tool.FileHelper;
import zlh.com.combinepluginhotfix.tool.PH;

/**
 * Created by shs1330 on 2018/3/12.
 */

public class App extends Application {
    private static final String TAG = "App";
    private static final String PLUGIN_ONE = "PluginOne.apk";
    private static final String PLUGIN_JNI = "Jni.apk";
    public static final String SOURCE_PKGNAME = "zlh.com.combinepluginhotfix";
    public static final String PLUGIN_ONE_PKGNAME = "nim.shs1330.netease.com.pluginone";
    public static final String PLUGIN_JNI_PKGNAME = "netease.com.jnisot";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PH.init(base);

        CustomInstrumentation.hook();
        ProxyActivityManagerService.hook();
        ProxyPackageManagerService.hook();
        HookCallback.hook();

        FileHelper.extractAssets(PLUGIN_JNI);
        FileHelper.extractAssets(PLUGIN_ONE);
        ApkLoader.hook(getFileStreamPath(PLUGIN_JNI), null);
        ApkLoader.hook(getFileStreamPath(PLUGIN_ONE), ApkLoader.getPluginClassLoader(PLUGIN_JNI_PKGNAME));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:");
    }
}
