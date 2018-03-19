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

        FileHelper.extractPatch("PluginOne");
        FileHelper.extractAssets(PLUGIN_JNI);
        FileHelper.extractAssets(PLUGIN_ONE);
        ApkLoader.hook(getFileStreamPath(PLUGIN_JNI), null);
        ApkLoader.hook(getFileStreamPath(PLUGIN_ONE), ApkLoader.getPluginClassLoader(PLUGIN_JNI_PKGNAME));

        FileHelper.installPatch(ApkLoader.getPluginClassLoader(PLUGIN_ONE_PKGNAME), "PluginOne", PLUGIN_ONE_PKGNAME);

        ApkLoader.callPluginApplicationCreate(PLUGIN_JNI_PKGNAME);
        ApkLoader.callPluginApplicationCreate(PLUGIN_ONE_PKGNAME);

        try {
            Log.d(TAG, "attachBaseContext: " +  ApkLoader.getPluginClassLoader(PLUGIN_ONE_PKGNAME).loadClass("nim.shs1330.netease.com.pluginone.UserInfoActivity"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        try {
//            Class jsonInfoActivity = ApkLoader.getPluginClassLoader(PLUGIN_ONE_PKGNAME).loadClass("nim.shs1330.netease.com.pluginone.UserInfoActivity");
//            Class mainActivity = ApkLoader.getPluginClassLoader(PLUGIN_ONE_PKGNAME).loadClass("nim.shs1330.netease.com.pluginone.MainActivity");
//            Method f1M = jsonInfoActivity.getDeclaredMethod("onCreate", Bundle.class);
//            f1M.setAccessible(true);
//            Method f2M = mainActivity.getDeclaredMethod("onCreate", Bundle.class);
//            f2M.setAccessible(true);
//            Class vmReplace = ApkLoader.getPluginClassLoader(PLUGIN_ONE_PKGNAME).loadClass("netease.com.jnisot.JniApp");
//            Method methodReplace = vmReplace.getDeclaredMethod("replace", Method.class, Method.class);
//            methodReplace.setAccessible(true);
//            methodReplace.invoke(null, f2M, f1M);
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:");
    }
}
