package zlh.com.combinepluginhotfix.hook.h;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import zlh.com.combinepluginhotfix.hook.ams.ProxyActivityManagerService;

/**
 * Created by shs1330 on 2018/3/13.
 */

public class HookCallback implements Handler.Callback {
    private static final String TAG = "HookCallback";
    private Handler mH;
    public HookCallback(Handler mH) {
        this.mH = mH;
    }
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 100: {
                try {
                    Field intentField = msg.obj.getClass().getDeclaredField("intent");
                    intentField.setAccessible(true);
                    Intent changedIntent = (Intent) intentField.get(msg.obj);

                    Intent target = changedIntent.getParcelableExtra(ProxyActivityManagerService.TARGET_ACTIVITY);
                    Log.d(TAG, "target  - handleMessage: " + target);
                    if (target != null)
                    {
                        changedIntent.setComponent(target.getComponent());
                        Field activityInfoField = msg.obj.getClass().getDeclaredField("activityInfo");
                        activityInfoField.setAccessible(true);
                        ActivityInfo activityInfo = (ActivityInfo) activityInfoField.get(msg.obj);
                        activityInfo.applicationInfo.packageName = target.getPackage() == null ?
                                target.getComponent().getPackageName() : target.getPackage();
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "handleMessage: " + msg.obj.getClass().getName());
            }break;
        }
        mH.handleMessage(msg);
        return true;
    }

    public static void hook() {
        try {
            Class activityThread = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object activityThreadObject = currentActivityThreadMethod.invoke(null);

            Method getHandlerMethod = activityThread.getDeclaredMethod("getHandler");
            getHandlerMethod.setAccessible(true);
            Object mHObject = getHandlerMethod.invoke(activityThreadObject);

            Field mHField = Handler.class.getDeclaredField("mCallback");
            mHField.setAccessible(true);
            mHField.set(mHObject, new HookCallback((Handler) mHObject));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
}
