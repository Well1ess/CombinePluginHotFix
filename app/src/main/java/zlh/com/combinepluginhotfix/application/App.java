package zlh.com.combinepluginhotfix.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import zlh.com.combinepluginhotfix.hook.ams.ProxyActivityManagerService;
import zlh.com.combinepluginhotfix.hook.instrumentation.CustomInstrumentation;

/**
 * Created by shs1330 on 2018/3/12.
 */

public class App extends Application {
    private static final String TAG = "App";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        CustomInstrumentation.hook();
        ProxyActivityManagerService.hook();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:");
    }
}
