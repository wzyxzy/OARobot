package com.zgty.oarobot.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zgty.oarobot.R;
import com.zgty.oarobot.util.SettingsFragment;
import com.zgty.oarobot.widget.CameraPreview;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mode_name;
    private TextView change_mode;
    private TextView name_staff;
    private TextView id_staff;
    private TextView sign_up_time;
    private TextView station_state;
    private FrameLayout camera_preview;

    private CameraPreview mPreview;
    private TextView name_part;
    private TextView robot_speak_text;
    private TextView setting_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        initScreen();
        initView();
        initCamera();
    }

    private void initCamera() {
        mPreview = new CameraPreview(this);
        camera_preview.addView(mPreview);
        SettingsFragment.passCamera(mPreview.getCameraInstance());
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SettingsFragment.setDefault(PreferenceManager.getDefaultSharedPreferences(this));
        SettingsFragment.init(PreferenceManager.getDefaultSharedPreferences(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//软件在后台屏幕不需要常亮
        mPreview = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        if (mPreview == null) {
            initCamera();
        }
    }

    private void initScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置横屏
        }
    }

    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        mode_name = findViewById(R.id.mode_name);
        change_mode = findViewById(R.id.change_mode);
        name_staff = findViewById(R.id.name_staff);
        id_staff = findViewById(R.id.id_staff);
        sign_up_time = findViewById(R.id.sign_up_time);
        station_state = findViewById(R.id.station_state);
        camera_preview = findViewById(R.id.camera_preview);


        change_mode.setOnClickListener(this);
        name_part = findViewById(R.id.name_part);
        robot_speak_text = findViewById(R.id.robot_speak_text);
        setting_main = findViewById(R.id.setting_main);
        setting_main.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.change_mode:
                intent = new Intent(this, ChatActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_main:
                intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
                break;
        }
    }

}
