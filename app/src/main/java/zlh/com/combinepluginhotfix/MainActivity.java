package zlh.com.combinepluginhotfix;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import zlh.com.combinepluginhotfix.application.App;
import zlh.com.combinepluginhotfix.hook.loadedapk.ApkLoader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public MainActivity() {
        Log.d(TAG, "MainActivity: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("nim.shs1330.netease.com.pluginone",
                        "nim.shs1330.netease.com.pluginone.MainActivity"));
                startActivity(intent);
            }
        });

        findViewById(R.id.bt_plugone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                ClassLoader classLoader = ApkLoader.getPluginClassLoader(App.PLUGIN_ONE_PKGNAME);
                try {
                    Class fragmentClazz = classLoader.loadClass("nim.shs1330.netease.com.pluginone.PluginOneMainFragment");
                    Fragment fragmentz = (Fragment) fragmentClazz.newInstance();
                    //Fragment fragment = PluginOneMainFragment.newInstance();
                    transaction.replace(R.id.flyt_contrainer, fragmentz);
                    transaction.commit();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
