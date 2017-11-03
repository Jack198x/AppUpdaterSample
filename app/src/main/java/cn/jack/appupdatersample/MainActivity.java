package cn.jack.appupdatersample;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import cn.jack.appupdater.AppUpdater;
import cn.jack.appupdater.Listener;

public class MainActivity extends AppCompatActivity {

    private Button btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        btnDownload = findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });
    }

    private void download() {
        AppUpdater.with(MainActivity.this)
                .setDownloadUrl("http://7xlpo2.com1.z0.glb.clouddn.com/elife-release-5.2.1-channel_update.apk")
                .setVersionName("5.2.1")
                .setVersionCode(521)
                .setListener(new Listener() {
                    @Override
                    public void onComplete(String file) {
                        btnDownload.setText(file);
                    }

                    @Override
                    public void onProgress(int percent) {
                        btnDownload.setText(percent + "");
                        Log.e("onProgress", percent + "");
                    }

                    @Override
                    public void onError(String error) {
                        btnDownload.setText(error);
                    }
                })
                .start();
    }
}
