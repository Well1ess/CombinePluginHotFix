package zlh.com.combinepluginhotfix.hook.loadedapk;

import android.app.Application;
import android.app.Instrumentation;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import zlh.com.combinepluginhotfix.hook.classloader.PluginClassLoader;
import zlh.com.combinepluginhotfix.hook.instrumentation.CustomInstrumentation;
import zlh.com.combinepluginhotfix.tool.FileHelper;
import zlh.com.combinepluginhotfix.tool.PH;

import static zlh.com.combinepluginhotfix.tool.FileHelper.getOptDir;

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
                    getOptDir(applicationInfo.packageName).getPath(),
                    FileHelper.getPluginLibDir(applicationInfo.packageName).getPath(),
                    parentLoader == null ? ApkLoader.class.getClassLoader() : parentLoader);
            Log.d(TAG, "hook: " + apkFile.getPath());
            Log.d(TAG, "hook: " + getOptDir(applicationInfo.packageName).getPath());
            Log.d(TAG, "hook: " + FileHelper.getPluginLibDir(applicationInfo.packageName).getPath());
            Field mClassLoaderFieldField = loadedApk.getClass().getDeclaredField("mClassLoader");
            mClassLoaderFieldField.setAccessible(true);
            mClassLoaderFieldField.set(loadedApk, classLoader);

            sPluginClassLoader.put(applicationInfo.packageName, classLoader);
            Log.d(TAG, "hook:  " + applicationInfo.packageName + classLoader);
            sLoadedApk.put(applicationInfo.packageName, loadedApk);

            mPackages.put(applicationInfo.packageName, new WeakReference(loadedApk));

            FileHelper.moveLibFile(apkFile, applicationInfo.packageName);
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

    public static void installPatch(String packagName, String patchName) {
        File sourcePatch = PH.getBaseContext().getFileStreamPath(patchName+ ".zip");
        File optDex = new File(FileHelper.getOptDir(packagName) + "/" + packagName + "dex");
        if(!optDex.exists()) {
            try {
                optDex.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        hookParentClassLoader(ApkLoader.getPluginClassLoader(packagName), sourcePatch, optDex);
    }

    private static void hookParentClassLoader(ClassLoader classLoader, File apkFile, File optDex) {
        //LoadedApk中mClassLoader由BaseDexClassLoader中的Element数组获取生成
        //我们通过反射构造自己的Apk对应的Element加到BaseDexClassLoader中就可以委托系统帮我
        //们生成对应的ClassLoader
        try {
            Field pathListF = DexClassLoader.class.getSuperclass().getDeclaredField("pathList");
            pathListF.setAccessible(true);
            //获取唯一的List的Object
            Object pathList = pathListF.get(classLoader);

            Field dexElementsF = pathList.getClass().getDeclaredField("dexElements");
            dexElementsF.setAccessible(true);
            //获取Element数组
            Object[] dexElements = (Object[]) dexElementsF.get(pathList);

            Class elementClass = dexElements.getClass().getComponentType();

            //新的数组
            Object[] newDexElements = (Object[]) Array.newInstance(elementClass, dexElements.length + 1);
            Constructor constructor = elementClass.getConstructor(File.class, boolean.class, File.class, DexFile.class);

            //我们apk对应的element
            Object customElement = constructor.newInstance(apkFile, false, apkFile, DexFile.loadDex(apkFile.getCanonicalPath(), optDex.getAbsolutePath(), 0));

            Object[] addArray = new Object[]{customElement};

            System.arraycopy(dexElements, 0, newDexElements, 0, dexElements.length);
            System.arraycopy(addArray, 0, newDexElements, dexElements.length, addArray.length);

            //替换
            dexElementsF.set(pathList, newDexElements);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void callPluginApplicationCreate(String packageName) {
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
