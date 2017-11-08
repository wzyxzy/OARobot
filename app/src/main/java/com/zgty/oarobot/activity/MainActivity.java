package com.zgty.oarobot.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.zgty.oarobot.R;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.common.CommonActivity;
import com.zgty.oarobot.dao.StaffDaoUtils;
import com.zgty.oarobot.util.IdentifyFace;
import com.zgty.oarobot.util.LogToastUtils;

import java.util.List;

import static com.zgty.oarobot.common.OARobotApplication.mTts;

public class MainActivity extends CommonActivity implements View.OnClickListener {

    private TextView mode_name;
    private TextView change_mode;
    private TextView name_staff;
    private TextView id_staff;
    private TextView sign_up_time;
    private TextView station_state;
    private FrameLayout camera_preview;

    private TextView name_part;
    private TextView robot_speak_text;
    private TextView setting_main;
    private IdentifyFace identifyFace;
    private String userid = "00000";


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CAMERA_CODE = 0x100;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://识别成功
                    initData();

                    break;
                case 1:
                    break;
                case 2:
                    int code = mTts.startSpeaking("欢迎使用中广通业打卡系统", null);
//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
//			String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
//			int code = mTts.synthesizeToUri(text, path, mTtsListener);

                    if (code != ErrorCode.SUCCESS) {
                        LogToastUtils.toastShort(getApplicationContext(), "语音合成失败,错误码: " + code);
                    }
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        initView();
        handler.sendEmptyMessage(2);

    }




    @Override
    protected void onResume() {
        super.onResume();
        if (identifyFace == null) {
            identifyFace = new IdentifyFace(camera_preview, this);
            identifyFace.openSurfaceView();
        }
        identifyFace.setOnIdentifyListener(new IdentifyFace.OnIdentifyListener() {
            @Override
            public void onSuccess(String user_id) {
                LogToastUtils.toastShort(getApplicationContext(), "success");
                handler.sendEmptyMessage(0);
                userid = user_id;
//                onResume();
            }

            @Override
            public void onSwitch() {
                LogToastUtils.toastShort(getApplicationContext(), "switch");
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onError() {
                LogToastUtils.toastShort(getApplicationContext(), "error");
            }
        });

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

    //识别完毕后，数据加载，写在异步线程
    private void initData() {
//        userid = "10003";
        List<Staff> staffList = new StaffDaoUtils(this).queryStaffList(userid);
        if (staffList != null && staffList.size() > 0) {
            Staff staff = staffList.get(0);
            name_staff.setText(staff.getName_user());
            id_staff.setText(staff.getId_clerk());
            name_part.setText(staff.getName_part());
            sign_up_time.setText(getNowTime());
            station_state.setText(getType());
            mTts.startSpeaking(staff.getName_user() + "，早上好！新的一天开始了，好好工作哦！", null);

        } else {
            LogToastUtils.toastShort(this, "没有录入该信息");
//            mTts.startSpeaking("没有录入该信息", null);
        }

    }

    //获取打卡类型
    private String getType() {
        return "";
    }

    //获取当前时间
    private String getNowTime() {
        return "";
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        identifyFace.finisheIdentify();
        identifyFace = null;

    }
}
