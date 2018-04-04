package zlh.com.combinepluginhotfix.tool;

/**
 * Created by shs1330 on 2017/10/18.
 */

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;

/**
 * 用于控制file的路径
 */
public class FileHelper {
    private static final String TAG = "FileHelper";
    //存放data/data/<packagename>/file
    private static File mBase = null;

    public static void extractPatch(String pathName) {
        AssetManager am = PH.getBaseContext().getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = am.open(pathName);
            File extractFile = PH.getBaseContext().getFileStreamPath(pathName + ".zip");
            fos = new FileOutputStream(extractFile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void makeDexElements(File optDexFile, ClassLoader context, ArrayList<IOException> suppressedException, File... dexFiles) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class baseDexClassLoaderC = DexClassLoader.class.getSuperclass();
        Field pathListF = baseDexClassLoaderC.getDeclaredField("pathList");
        pathListF.setAccessible(true);
        Object pathListO = pathListF.get(context);

        Class pahtListC = pathListO.getClass();
        Method makeDexElementsM = pahtListC.getDeclaredMethod("makeDexElements", List.class, File.class, List.class, ClassLoader.class);
        makeDexElementsM.setAccessible(true);

        Object[] otherElements = (Object[]) makeDexElementsM.invoke(null, Arrays.asList(dexFiles), optDexFile, suppressedException, context);

        Field dexElementsF = pahtListC.getDeclaredField("dexElements");
        dexElementsF.setAccessible(true);

        Object[] dexElementsO = (Object[]) dexElementsF.get(pathListO);

        Object[] newElements = (Object[]) Array.newInstance(dexElementsO.getClass().getComponentType(), dexElementsO.length + otherElements.length);

        System.arraycopy(dexElementsO, 0, newElements, 0, dexElementsO.length);
        System.arraycopy(otherElements, 0, newElements, dexElementsO.length, otherElements.length);
        dexElementsF.set(pathListO, newElements);

        Log.d(TAG, "makeDexElements: " + pathListO.toString());
    }

    /**
     * 将文件从assets复制到
     *
     * @param sourceName data/data/<packagename>/files/sourceName文件夹下
     */
    public static void extractAssets(String sourceName) {
        AssetManager am = PH.getBaseContext().getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = am.open(sourceName);
            File extractFile = PH.getBaseContext().getFileStreamPath(sourceName);
            fos = new FileOutputStream(extractFile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    enum DownloadType {
        Apk, Patch
    }

    public static void storeFile(String sourceName, byte[] result) {
        DownloadType type = DownloadType.Apk;
        if (sourceName.endsWith(".patch")) {
            type = DownloadType.Patch;
        }

        FileOutputStream fos = null;
        try {
            File extractFile = PH.getBaseContext().getFileStreamPath(sourceName);
            fos = new FileOutputStream(extractFile);

            fos.write(result, 0, result.length);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 返回对应plugin包名路径下的基础路径
     *
     * @param packageName
     * @return data/data/<packageName>/files/plugin/<PlugInPackageName>
     */
    public static File getBasePluginDir(String packageName) {
        if (mBase == null) {
            mBase = PH.getBaseContext().getFileStreamPath("plugin");
            enforeFileExists(mBase);
        }
        return enforeFileExists(new File(mBase, packageName));
    }

    /**
     * 返回opt file
     *
     * @param packagName
     * @return data/data/<packageName>/files/plugin/<PlugInPackageName>/odex
     */
    public static File getOptDir(String packagName) {
        return enforeFileExists(new File(getBasePluginDir(packagName), "odex"));
    }

    /**
     * @param packagName
     * @return data/data/<packageName>/files/plugin/<PlugInPackageName>/lib
     */
    public static File getPluginLibDir(String packagName) {
        enforeFileExists(new File(getBasePluginDir(packagName), "lib"));
        return enforeFileExists(new File(getBasePluginDir(packagName), "lib/x86"));
    }

    public static void moveLibFile(File apkFile, String pkgName) {
        try {
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(
                            new FileInputStream(apkFile)));
            while (true) {
                ZipEntry entry = zis.getNextEntry();
                if ((entry == null)) {
                    zis.close();
                    break;
                }
                String name = entry.getName();
                if (name.startsWith("lib/x86/") && name.endsWith(".so")) {
                    File libDir = new File(FileHelper.getPluginLibDir(pkgName).getPath()
                            + name.substring(name.indexOf('/'), name.lastIndexOf('/')));
                    if (!libDir.exists()) {
                        libDir.mkdir();
                    }
                    File libFile = new File(FileHelper.enforeFileExists(new File(FileHelper.getBasePluginDir(pkgName), "lib/x86")).getPath()
                            + name.substring(name.lastIndexOf('/')));
                    libFile.createNewFile();
                    Log.d(TAG, "moveLibFile: " + libFile.getPath());
                    FileOutputStream fos = new FileOutputStream(libFile);
                    byte[] arrayOfbytes = new byte[1024];
                    while (true) {
                        int i = zis.read(arrayOfbytes);
                        if (i == -1)
                            break;
                        fos.write(arrayOfbytes, 0, i);
                    }
                    fos.flush();
                    fos.close();
                }
                zis.closeEntry();
            }
            zis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 确保文件存在
     *
     * @param file
     * @return
     */
    private static File enforeFileExists(File file) {
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

}
