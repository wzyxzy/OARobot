package com.zgty.oarobot.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zgty.oarobot.R;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.dao.StaffDaoUtils;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView staff_manage;
    private TextView time_manage;
    private TextView dialog_manage;
    private TextView pass_manage;
    private TextView com_message;
    private TextView back_change;
    private TextView back_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        initScreen();
        initView();

    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//软件在后台屏幕不需要常亮
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮

    }

    private void initScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置横屏
        }
    }

    private void initView() {
        staff_manage = findViewById(R.id.staff_manage);
        time_manage = findViewById(R.id.time_manage);
        dialog_manage = findViewById(R.id.dialog_manage);
        pass_manage = findViewById(R.id.pass_manage);
        com_message = findViewById(R.id.com_message);
        back_change = findViewById(R.id.back_change);
        back_main = findViewById(R.id.back_main);

        staff_manage.setOnClickListener(this);
        time_manage.setOnClickListener(this);
        dialog_manage.setOnClickListener(this);
        pass_manage.setOnClickListener(this);
        com_message.setOnClickListener(this);
        back_change.setOnClickListener(this);
        back_main.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.staff_manage:
                insertStaff();
                break;
            case R.id.time_manage:

                break;
            case R.id.dialog_manage:

                break;
            case R.id.pass_manage:

                break;
            case R.id.com_message:

                break;
            case R.id.back_change:

                break;
            case R.id.back_main:
                finish();
                break;
        }
    }

    private void insertStaff() {
        StaffDaoUtils staffDaoUtils = new StaffDaoUtils(this);
        for (int i = 0; i < 50; i++) {
            Staff staff = new Staff();
            staff.setId(String.valueOf(10000 + i));
            staff.setName_user("张XX" + i);
            staff.setId_user("zhangxx" + i);
            staff.setId_clerk(String.valueOf(i + 1));
            staff.setName_part("技术研发部");
            staff.setName_position("安卓工程师");
            staff.setCall_num(String.valueOf(18010480090L + i));
            staff.setUser_type("会议室");
            staff.setRecordFace(false);
            staffDaoUtils.insertStaff(staff);
        }
    }
}
