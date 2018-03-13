package zlh.com.combinepluginhotfix.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import zlh.com.combinepluginhotfix.hook.ams.ProxyActivityManagerService;
import zlh.com.combinepluginhotfix.hook.h.HookCallback;
import zlh.com.combinepluginhotfix.hook.instrumentation.CustomInstrumentation;
import zlh.com.combinepluginhotfix.hook.pms.ProxyPackageManagerService;
import zlh.com.combinepluginhotfix.tool.PH;

/**
 * Created by shs1330 on 2018/3/12.
 */

public class App extends Application {
    private static final String TAG = "App";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PH.init(base);

        CustomInstrumentation.hook();
        ProxyActivityManagerService.hook();
        ProxyPackageManagerService.hook();
        HookCallback.hook();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:");
    }
}
