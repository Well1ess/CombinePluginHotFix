package zlh.com.combinepluginhotfix.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.google.protobuf.InvalidProtocolBufferException;

import zlh.com.combinepluginhotfix.R;
import zlh.com.combinepluginhotfix.bean.BookOuterClass;
import zlh.com.combinepluginhotfix.download.DownloadPatchTask;
import zlh.com.combinepluginhotfix.download.NewPatchTipDialog;
import zlh.com.combinepluginhotfix.hook.loadedapk.ApkLoader;
import zlh.com.combinepluginhotfix.tool.Tags;

import static zlh.com.combinepluginhotfix.application.App.PLUGIN_ONE_PKGNAME;
import static zlh.com.combinepluginhotfix.application.App.PLUGIN_TWO_PKGNAME;

/**
 * 方法以及运行则不能加载
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ProgressBar progressBar;
    private PopupWindow popupWindow;
    public ImageView downImage;

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

        downImage = (ImageView) findViewById(R.id.iv_download);
        findViewById(R.id.bt_plugone).setOnClickListener(this);
        findViewById(R.id.bt_plugtwo).setOnClickListener(this);
        findViewById(R.id.tv_dialog).setOnClickListener(this);
        BookOuterClass.Book book = BookOuterClass.Book.newBuilder()
                .setId(1001)
                .setName("Android")
                .setDesc("书籍推荐")
                .build();

        try {
            BookOuterClass.Book b2 = BookOuterClass.Book.parseFrom(book.toByteArray());
            Log.d(TAG, "onCreate: " + b2.toString());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public void publishProgress(float percent) {
        if (this.progressBar!=null) {
            this.progressBar.setProgress((int) percent);
        }
    }

    public void dismissDialog() {
        popupWindow.dismiss();
    }

    @Override
    public void onClick(final View v) {
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
        else if (v.getId() == R.id.tv_dialog) {
            new NewPatchTipDialog.Builder(this)
                    .create()
                    .setPositiveButton(new NewPatchTipDialog.OnPopWindowClickListener() {
                        @Override
                        public void onClick(View view, PopupWindow popupWindow) {
                            progressBar = (ProgressBar) view.findViewById(R.id.pb_process);
                            MainActivity.this.popupWindow = popupWindow;
                            new DownloadPatchTask(MainActivity.this).execute(Tags.spluginDownload);
                        }
                    })
                    .show(findViewById(R.id.flyt_contrainer), Gravity.CENTER, 0, 0);
        }
    }
}
