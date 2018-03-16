package zlh.com.combinepluginhotfix.hook.loadedapk;

import android.app.Application;
import android.app.Instrumentation;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import zlh.com.combinepluginhotfix.hook.classloader.PluginClassLoader;
import zlh.com.combinepluginhotfix.hook.instrumentation.CustomInstrumentation;
import zlh.com.combinepluginhotfix.tool.FileHelper;

/**
 * Created by shs1330 on 2018/3/14.
 */

public class ApkLoader {
    private static final String TAG = "ApkLoader";

    //packageName->LoadedApk
    private static Map<String, Object> sLoadedApk = new HashMap<>();
    //packageName->ClassLoader
    private static Map<String, ClassLoader> sPluginClassLoader = new HashMap<>();

    public static ClassLoader getPluginClassLoader(String apkName) {
        return sPluginClassLoader.get(apkName);
    }

    public static Object getLoadedApk(String apkName) {
        return sLoadedApk.get(apkName);
    }

    public static void hook(File apkFile, ClassLoader parentLoader) {

        //获取ActivityThread
        Class activityThreadClz = null;
        try {
            activityThreadClz = Class.forName("android.app.ActivityThread");
            //访问ActivityThread的方法
            Method currentActivityThreadM = activityThreadClz.getDeclaredMethod("currentActivityThread");
            currentActivityThreadM.setAccessible(true);
            //ActivityThread对象
            Object activityThread = currentActivityThreadM.invoke(null);

            Field mPackagesF = activityThreadClz.getDeclaredField("mPackages");
            mPackagesF.setAccessible(true);
            //ActivityThread中PackageName和LoadedApk的对应Map
            Map mPackages = (Map) mPackagesF.get(activityThread);

            //获得生成LoadedApk的方法
            Method getPackageInfoNoCheck = activityThreadClz.getDeclaredMethod("getPackageInfoNoCheck",
                    ApplicationInfo.class,
                    Class.forName("android.content.res.CompatibilityInfo"));
            getPackageInfoNoCheck.setAccessible(true);

            //生成LoadedApk必须有对应apk的AndroidManifest.xml的信息
            //与之对应的是ApplicationInfo
            ApplicationInfo applicationInfo = generateApplicationInfo(apkFile);
            Object loadedApk = getPackageInfoNoCheck.invoke(activityThread, applicationInfo, generateCompatibilityInfo());

//            PluginClassLoader classLoader = new PluginClassLoader(apkFile.getPath(),
//                    FileHelper.getOptDir(applicationInfo.packageName).getPath(),
//                    FileHelper.getPluginLibDir(applicationInfo.packageName).getPath(),
//                    ClassLoader.getSystemClassLoader());
            PluginClassLoader classLoader = new PluginClassLoader(apkFile.getPath(),
                    FileHelper.getOptDir(applicationInfo.packageName).getPath(),
                    FileHelper.getPluginLibDir(applicationInfo.packageName).getPath(),
                    parentLoader == null ? ApkLoader.class.getClassLoader() : parentLoader);
            Log.d(TAG, "hook: " + apkFile.getPath());
            Log.d(TAG, "hook: " + FileHelper.getOptDir(applicationInfo.packageName).getPath());
            Log.d(TAG, "hook: " + FileHelper.getPluginLibDir(applicationInfo.packageName).getPath());
            Field mClassLoaderFieldField = loadedApk.getClass().getDeclaredField("mClassLoader");
            mClassLoaderFieldField.setAccessible(true);
            mClassLoaderFieldField.set(loadedApk, classLoader);

            sPluginClassLoader.put(applicationInfo.packageName, classLoader);
            sLoadedApk.put(applicationInfo.packageName, loadedApk);

            mPackages.put(applicationInfo.packageName, new WeakReference(loadedApk));

            FileHelper.moveLibFile(apkFile, applicationInfo.packageName);
            callPluginApplicationCreate(applicationInfo.packageName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static void callPluginApplicationCreate(String packageName) {
        Class loadedApk = sLoadedApk.get(packageName).getClass();
        try {
            Method makeApplication = loadedApk.getDeclaredMethod("makeApplication", boolean.class, Instrumentation.class);
            Application app = (Application) makeApplication.invoke(sLoadedApk.get(packageName), false, CustomInstrumentation.getInstance());
            Log.d(TAG, "callPluginApplicationCreate: " + app.getApplicationInfo().packageName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static ApplicationInfo generateApplicationInfo(File apkFile) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class packageParserClass = Class.forName("android.content.pm.PackageParser");

        Class packageParser$PackageClass = Class.forName("android.content.pm.PackageParser$Package");
        Class packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
        Method generateApplicationInfo = packageParserClass.getDeclaredMethod("generateApplicationInfo", packageParser$PackageClass,
                int.class, packageUserStateClass);
        generateApplicationInfo.setAccessible(true);
        Object parser = packageParserClass.newInstance();
        Method parsePackage = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
        parsePackage.setAccessible(true);

        Object packageObject = parsePackage.invoke(parser, apkFile, 0);

        ApplicationInfo applicationInfo = (ApplicationInfo) generateApplicationInfo.invoke(parser, packageObject, 0, packageUserStateClass.newInstance());
        applicationInfo.sourceDir = apkFile.getPath();
        applicationInfo.publicSourceDir = apkFile.getPath();
        return applicationInfo;
    }

    private static Object generateCompatibilityInfo() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class CompatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
        Field defaultCompatibilityInfoField = CompatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
        defaultCompatibilityInfoField.setAccessible(true);
        return defaultCompatibilityInfoField.get(null);
    }
}
