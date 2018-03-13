package zlh.com.combinepluginhotfix.hook.pms;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by shs1330 on 2018/3/13.
 */

public class ProxyPackageManagerService implements InvocationHandler {
    private static final String TAG = "ProxyPackageManagerSer";
    private Object mBasePms;

    public ProxyPackageManagerService(Object mBasePms) {
        this.mBasePms = mBasePms;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d(TAG, "invoke: " + method.getName());
        if (method.getName().equals("getActivityInfo")){
            ActivityInfo activityInfo = new ActivityInfo();
            return activityInfo;
        }
        if (method.getName().equals("getPackageInfo")) {
            return new PackageInfo();
        }
        return method.invoke(mBasePms, args);
    }

    public static void hook(){
        try {
            Class activityThread = Class.forName("android.app.ActivityThread");
            Method getPackageManagerMethod = activityThread.getDeclaredMethod("getPackageManager");
            getPackageManagerMethod.setAccessible(true);
            Object sPackageManagerObject = getPackageManagerMethod.invoke(null);

            Object pms = Proxy.newProxyInstance(activityThread.getClassLoader(),
                    new Class[]{Class.forName("android.content.pm.IPackageManager")},
                    new ProxyPackageManagerService(sPackageManagerObject));

            Field sPackageManagerF = activityThread.getDeclaredField("sPackageManager");
            sPackageManagerF.setAccessible(true);

            sPackageManagerF.set(null, pms);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
