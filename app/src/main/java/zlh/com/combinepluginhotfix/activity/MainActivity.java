package zlh.com.combinepluginhotfix.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import zlh.com.combinepluginhotfix.R;
import zlh.com.combinepluginhotfix.hook.loadedapk.ApkLoader;

import static zlh.com.combinepluginhotfix.application.App.PLUGIN_ONE_PKGNAME;
import static zlh.com.combinepluginhotfix.application.App.PLUGIN_TWO_PKGNAME;

/**
 * 方法以及运行则不能加载
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    public MainActivity() {
        Log.d(TAG, "MainActivity: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        findViewById(R.id.bt_plugone).setOnClickListener(this);
        findViewById(R.id.bt_plugtwo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_plugone) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ClassLoader classLoader = ApkLoader.getPluginClassLoader(PLUGIN_ONE_PKGNAME);
            try {
                Class fragmentClazz = classLoader.loadClass("nim.shs1330.netease.com.pluginone.PluginOneMainFragment");
                Fragment fragment = (Fragment) fragmentClazz.newInstance();
                transaction.replace(R.id.flyt_contrainer, fragment);
                transaction.commit();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        else if (v.getId() == R.id.bt_plugtwo){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ClassLoader classLoader = ApkLoader.getPluginClassLoader(PLUGIN_TWO_PKGNAME);
            try {
                Class fragmentClazz = classLoader.loadClass("zlh.com.plugintwo.PluginTwoMainFragment");
                Fragment fragment = (Fragment) fragmentClazz.newInstance();
                transaction.replace(R.id.flyt_contrainer, fragment);
                transaction.commit();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}