package com.zgty.oarobot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zgty.oarobot.R;
import com.zgty.oarobot.common.CommonActivity;

import static com.zgty.oarobot.common.Constant.nowAccount;

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
        Intent intent;
        switch (v.getId()) {
            case R.id.staff_manage:
//                insertStaff();
                intent = new Intent(this, StaffManager.class);
                startActivity(intent);
                break;
            case R.id.time_manage:
                intent = new Intent(this, TimeManageActivity.class);
                startActivity(intent);
                break;
            case R.id.dialog_manage:
                intent = new Intent(this, SpeekManage.class);
                startActivity(intent);
                break;
            case R.id.pass_manage:
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nowAccount = null;
    }


}
