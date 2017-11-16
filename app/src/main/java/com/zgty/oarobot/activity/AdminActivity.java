package com.zgty.oarobot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zgty.oarobot.R;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.common.CommonActivity;
import com.zgty.oarobot.dao.StaffDaoUtils;

public class AdminActivity extends CommonActivity implements View.OnClickListener {

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
        initView();

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
//                insertStaff();
                Intent intent = new Intent(this, StaffManager.class);
                startActivity(intent);
                break;
            case R.id.time_manage:
                Intent intent2 = new Intent(this, TimeManageActivity.class);
                startActivity(intent2);
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
