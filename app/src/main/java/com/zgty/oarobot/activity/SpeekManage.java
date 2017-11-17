package com.zgty.oarobot.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.zgty.oarobot.R;
import com.zgty.oarobot.adapter.SpeakManageAdapter;
import com.zgty.oarobot.bean.Speaking;
import com.zgty.oarobot.common.CommonActivity;
import com.zgty.oarobot.dao.SpeekDaoUtils;
import com.zgty.oarobot.widget.MyDialog;

import java.util.List;

public class SpeekManage extends CommonActivity implements View.OnClickListener {

    private TextView back_admin;
    private ListView speek_listview;
    private TextView edit_cancel;
    private TextView edit_sure;
    private List<Speaking> speakings;
    private SpeekDaoUtils speekDaoUtils;
    private SpeakManageAdapter speakManageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speek_manage);
        initView();
        initData();
    }

    private void initData() {
        speekDaoUtils = new SpeekDaoUtils(this);
        speakings = speekDaoUtils.querySpeekList();
        speakManageAdapter.updateRes(speakings);
    }

    private void initView() {
        back_admin = findViewById(R.id.back_admin);
        speek_listview = findViewById(R.id.speek_listview);
        edit_cancel = findViewById(R.id.edit_cancel);
        edit_sure = findViewById(R.id.edit_sure);

        edit_cancel.setOnClickListener(this);
        back_admin.setOnClickListener(this);
        edit_sure.setOnClickListener(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.item_speek, null);
        speek_listview.addHeaderView(inflate);
        speakManageAdapter = new SpeakManageAdapter(speakings, this, R.layout.item_speek);
        speek_listview.setAdapter(speakManageAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_cancel:
            case R.id.back_admin:
                finish();
                break;
            case R.id.edit_sure:
                final MyDialog myDialog = new MyDialog(this);
                myDialog.setMessage("您确定要保存修改的会话吗？");
                myDialog.setYesOnclickListener("确定", new MyDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        myDialog.dismiss();
                        submit();
                    }
                });
                myDialog.setNoOnclickListener("取消", new MyDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        myDialog.dismiss();
                    }
                });
                myDialog.show();
                break;
        }
    }

    private void submit() {
        edit_sure.requestFocusFromTouch();
        speekDaoUtils.updateSpeeking(speakings);
        finish();
    }
}
