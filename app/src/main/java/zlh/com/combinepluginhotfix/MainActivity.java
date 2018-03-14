package zlh.com.combinepluginhotfix;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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

    }
}
