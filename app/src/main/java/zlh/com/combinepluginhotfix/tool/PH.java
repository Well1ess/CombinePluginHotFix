package zlh.com.combinepluginhotfix.tool;

import android.content.Context;

/**
 * Created by shs1330 on 2018/3/13.
 */

public class PH {
    private static Context baseContext;
    public static void init(Context context) {
        PH.baseContext = context;
    }
    public static Context getBaseContext() {
        return baseContext;
    }
}
