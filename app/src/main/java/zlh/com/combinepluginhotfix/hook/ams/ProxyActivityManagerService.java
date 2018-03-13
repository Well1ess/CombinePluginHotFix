package zlh.com.combinepluginhotfix.hook.ams;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import zlh.com.combinepluginhotfix.StubActivity;

/**
 * Created by shs1330 on 2018/3/12.
 */

public class ProxyActivityManagerService implements InvocationHandler {
    public static final String TARGET_ACTIVITY = "TARGET_ACTIVITY";
    private static final String TAG = "ProxyActivityManagerSer";
    private Object mBaseAms;
    public ProxyActivityManagerService(Object object) {
        mBaseAms = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d(TAG, "invoke: " + method.getName());
        if (method.getName().equals("startActivity")) {
            Intent rawIntent = null;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    rawIntent = (Intent) args[i];
                    index = i;
                    break;
                }
            }
            String packageName = "zlh.com.combinepluginhotfix";
            ComponentName c = new ComponentName(packageName, StubActivity.class.getCanonicalName());
            Intent newIntent = new Intent();
            newIntent.setComponent(c);
            Log.d(TAG, "invoke: " + rawIntent);
            newIntent.putExtra(TARGET_ACTIVITY, rawIntent);
            args[index] = newIntent;
        }
        return method.invoke(mBaseAms, args);
    }

    public static void hook() {
        try {
            Class ActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Field gDefaultFiled = ActivityManagerNative.getDeclaredField("gDefault");
            gDefaultFiled.setAccessible(true);
            Object gDefaultObject = gDefaultFiled.get(null);
            Class Singleton = Class.forName("android.util.Singleton");
            Field mInstanceField = Singleton.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object mInstance = mInstanceField.get(gDefaultObject);

            Class IActivityManager = Class.forName("android.app.IActivityManager");
            Object proxyAMS = Proxy.newProxyInstance(ActivityManagerNative.getClassLoader(),
                    new Class[]{ IActivityManager},
                    new ProxyActivityManagerService(mInstance));
            mInstanceField.set(gDefaultObject, proxyAMS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
    }

}

        }