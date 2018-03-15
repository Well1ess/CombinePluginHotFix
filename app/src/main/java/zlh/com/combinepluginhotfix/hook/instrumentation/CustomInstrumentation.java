package zlh.com.combinepluginhotfix.hook.instrumentation;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import zlh.com.combinepluginhotfix.application.App;
import zlh.com.combinepluginhotfix.tool.PH;

/**
 * Created by shs1330 on 2018/3/12.
 * Activity的创建要经过Instrumentation
 */

public class CustomInstrumentation extends Instrumentation {
    private static final String TAG = "CustomInstrumentation";
    public final static String execStartActivity = "execStartActivity";
    private Instrumentation original;
    private Method execStartActivityMethod;
    private static volatile CustomInstrumentation instance;
    public static synchronized CustomInstrumentation getInstance() {
        return instance;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (execStartActivityMethod == null) {
            execStartActivityMethod = Instrumentation.class.getDeclaredMethod(execStartActivity, Context.class,
                    IBinder.class, IBinder.class, Activity.class, Intent.class, int.class,
                    Bundle.class);
        }
        Log.d(TAG, "execStartActivity: " + intent.toString());
        execStartActivityMethod.setAccessible(true);
        return (ActivityResult) execStartActivityMethod.invoke(original, who, contextThread, token, target,
                intent, requestCode, options);
    }

    public void callActivityOnCreate(Activity activity, Bundle icicle)
    {
        Log.d(TAG, "callActivityOnCreate: " + activity.getApplicationInfo().packageName);
        super.callActivityOnCreate(activity,icicle);
    }


    public void callApplicationOnCreate(Application app) {
        if (!app.getApplicationInfo().packageName.equals(App.SOURCE_PKGNAME)) {
            try {
                Log.d(TAG, "callApplicationOnCreate: " + app.getClass().getName());
                Field thatField = app.getClass().getDeclaredField("that");
                thatField.setAccessible(true);
                thatField.set(null, PH.getBaseContext());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        super.callApplicationOnCreate(app);
    }

    public static void hook() {
        Class activityThread = null;
        try {
            activityThread = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object activityThreadObject = currentActivityThreadMethod.invoke(null);
            Field mInstrumentationFiled = activityThread.getDeclaredField("mInstrumentation");
            mInstrumentationFiled.setAccessible(true);
            Instrumentation original = (Instrumentation) mInstrumentationFiled.get(activityThreadObject);
            CustomInstrumentation custom = new CustomInstrumentation();
            instance = custom;
            custom.original = original;
            mInstrumentationFiled.set(activityThreadObject, custom);
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
        }

    }
}
