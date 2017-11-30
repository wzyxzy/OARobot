package com.zgty.oarobot.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgty.oarobot.R;
import com.zgty.oarobot.common.CommonSActivity;
import com.zgty.oarobot.util.IdentifyFace;
import com.zgty.oarobot.util.LogToastUtils;

import static com.zgty.oarobot.common.Constant.MAIN_RECORD_CAMERA_TYPE;

public class MakeSureFace extends CommonSActivity implements View.OnClickListener {

    private FrameLayout camera_preview;
    private TextView edit_cancel;
    private TextView edit_sure;
    private LinearLayout edit_sure_cancel;
    private String staff_id;
    private IdentifyFace identifyFace;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://识别成功
                    edit_sure_cancel.setVisibility(View.VISIBLE);
                    break;
                case 1://取消
                    edit_sure_cancel.setVisibility(View.INVISIBLE);
                    if (identifyFace != null) {
                        identifyFace.startCameraView();
                    }
                    break;
                case 2://上传
                    identifyFace.addStaff(staff_id);

                    break;
            }

        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_sure_face);
        initView();
        initData();
    }

    private void initData() {
        staff_id = getIntent().getStringExtra("staff_id");
    }

    private void initView() {
        camera_preview = findViewById(R.id.camera_preview);
        edit_cancel = findViewById(R.id.edit_cancel);
        edit_sure = findViewById(R.id.edit_sure);
        edit_sure_cancel = findViewById(R.id.edit_sure_cancel);
        edit_cancel.setOnClickListener(this);
        edit_sure.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (identifyFace == null) {
            identifyFace = new IdentifyFace(camera_preview, this, MAIN_RECORD_CAMERA_TYPE);
            identifyFace.openSurfaceView();
        }
        identifyFace.setOnIdentifyListener(new IdentifyFace.OnIdentifyListener() {
            @Override
            public void onSuccess(String user_id) {
            }

            @Override
            public void onSwitch(byte[] b) {
            }

            @Override
            public void onError() {
            }

            @Override
            public void onCapture() {
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onRegisterSuccess() {
                LogToastUtils.toastShort(getApplicationContext(), "录入成功!");

                setResult(22);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_cancel:
                handler.sendEmptyMessage(1);
                break;
            case R.id.edit_sure:
                handler.sendEmptyMessage(2);
                break;
        }
    }
}
