package com.zgty.oarobot.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zgty.oarobot.R;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.common.CommonActivity;
import com.zgty.oarobot.dao.StaffDaoUtils;
import com.zgty.oarobot.util.LogToastUtils;

import java.util.List;

public class StaffDetail extends CommonActivity implements View.OnClickListener {

    private TextView back_admin;
    private EditText name_staff;
    private EditText name_staff_english;
    private EditText id_staff;
    private EditText telephone_num;
    private EditText name_part;
    private EditText staff_position;
    private Spinner welcome_type;
    private TextView record_face_or_no;
    private TextView edit_cancel;
    private TextView edit_sure;
    private LinearLayout edit_sure_cancel;
    private TextView delete_staff;
    private TextView edit_staff;
    private TextView record_face;
    private String staff_id;
    private int detail_type;
    private Staff staff;
    private boolean firstAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detail);
        initView();
        initData();
    }

    private void initData() {
        detail_type = getIntent().getIntExtra("detail_type", 0);
        staff_id = getIntent().getStringExtra("staff_id");

        switch (detail_type) {
            case 0:
                resetAllEdit();
                makeStaffCanEdit();
                firstAdd = true;
                break;
            case 1:
                setDetails();
                firstAdd = false;
                break;
        }
    }

    private void setDetails() {
        List<Staff> staffList = new StaffDaoUtils(this).queryStaffList(staff_id);
        if (staffList != null && staffList.size() > 0) {
            staff = staffList.get(0);
            name_staff.setText(staff.getName_user());
            name_staff_english.setText(staff.getId_user());
            id_staff.setText(staff.getId_clerk());
            name_part.setText(staff.getName_part());
            telephone_num.setText(staff.getCall_num());
            staff_position.setText(staff.getName_position());
//            welcome_type.setText(staff.getName_position());
//            mTts.startSpeaking(staff.getName_user() + "，早上好！新的一天开始了，好好工作哦！", null);

        } else {
            LogToastUtils.toastShort(this, "信息错误");
            finish();
//            mTts.startSpeaking("没有录入该信息", null);
        }
    }

    private void resetAllEdit() {
        name_staff.setText("");
        name_staff_english.setText("");
        id_staff.setText("");
        telephone_num.setText("");
        name_part.setText("");
        staff_position.setText("");
    }

    private void initView() {
        back_admin = findViewById(R.id.back_admin);
        back_admin.setOnClickListener(this);
        name_staff = findViewById(R.id.name_staff);
        name_staff_english = findViewById(R.id.name_staff_english);
        id_staff = findViewById(R.id.id_staff);
        telephone_num = findViewById(R.id.telephone_num);
        name_part = findViewById(R.id.name_part);
        staff_position = findViewById(R.id.staff_position);
        welcome_type = findViewById(R.id.welcome_type);
        record_face_or_no = findViewById(R.id.record_face_or_no);
        edit_cancel = findViewById(R.id.edit_cancel);
        edit_cancel.setOnClickListener(this);
        edit_sure = findViewById(R.id.edit_sure);
        edit_sure.setOnClickListener(this);
        edit_sure_cancel = findViewById(R.id.edit_sure_cancel);
        delete_staff = findViewById(R.id.delete_staff);
        delete_staff.setOnClickListener(this);
        edit_staff = findViewById(R.id.edit_staff);
        edit_staff.setOnClickListener(this);
        record_face = findViewById(R.id.record_face);
        record_face.setOnClickListener(this);

    }

    private void submit() {
        // validate
        String staff_name = name_staff.getText().toString().trim();
        if (TextUtils.isEmpty(staff_name)) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String english = name_staff_english.getText().toString().trim();
        if (TextUtils.isEmpty(english)) {
            Toast.makeText(this, "全拼不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String id_clerk = id_staff.getText().toString().trim();
        if (TextUtils.isEmpty(id_clerk)) {
            Toast.makeText(this, "工号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String num = telephone_num.getText().toString().trim();
        if (TextUtils.isEmpty(num)) {
            Toast.makeText(this, "电话不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String part = name_part.getText().toString().trim();
        if (TextUtils.isEmpty(part)) {
            Toast.makeText(this, "部门不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String position = staff_position.getText().toString().trim();
        if (TextUtils.isEmpty(position)) {
            Toast.makeText(this, "职位不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something
        if (firstAdd) {
            staff = new Staff();
        }
        staff.setName_user(staff_name);
        staff.setId_user(english);
        staff.setId_clerk(id_clerk);
        staff.setCall_num(num);
        staff.setName_part(part);
        staff.setName_position(position);
        staff_id = english + id_clerk;
        staff.setId(staff_id);
        if (firstAdd) {
            new StaffDaoUtils(this).insertStaff(staff);
        } else {
            try{
            new StaffDaoUtils(this).updateStaff(staff);
            }catch(Exception e){
                LogToastUtils.toastShort(this,e.getMessage());
            }
        }
        firstAdd = false;
        makeStaffCannotEdit();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_admin:
                finish();
                break;
            case R.id.edit_cancel:
                //取消编辑
                if (firstAdd) {
                    finish();
                } else {
                    makeStaffCannotEdit();
                }
                break;
            case R.id.edit_sure:
                //确认编辑
                submit();
                break;
            case R.id.delete_staff:
                //删除员工
                if (firstAdd) {
                    LogToastUtils.toastShort(this, "请先保存后再操作！");
                } else {
                    new StaffDaoUtils(this).deleteStaff(staff);
                    finish();
                }

                break;
            case R.id.edit_staff:
                //编辑员工
                if (firstAdd) {
                    LogToastUtils.toastShort(this, "请先保存后再操作！");
                } else {
                    makeStaffCanEdit();
                }
                break;
            case R.id.record_face:
                //人脸录入
                break;

        }
    }

    private void makeStaffCanEdit() {
        edit_sure_cancel.setVisibility(View.VISIBLE);
        name_staff.setFocusableInTouchMode(true);
        name_staff.setFocusable(true);
        name_staff_english.setFocusableInTouchMode(true);
        name_staff_english.setFocusable(true);
        id_staff.setFocusableInTouchMode(true);
        id_staff.setFocusable(true);
        telephone_num.setFocusableInTouchMode(true);
        telephone_num.setFocusable(true);
        name_part.setFocusableInTouchMode(true);
        name_part.setFocusable(true);
        staff_position.setFocusableInTouchMode(true);
        staff_position.setFocusable(true);
        welcome_type.setFocusableInTouchMode(true);
        welcome_type.setFocusable(true);
    }

    private void makeStaffCannotEdit() {
        edit_sure_cancel.setVisibility(View.INVISIBLE);
        name_staff.setFocusableInTouchMode(false);
        name_staff.setFocusable(false);
        name_staff_english.setFocusableInTouchMode(false);
        name_staff_english.setFocusable(false);
        id_staff.setFocusableInTouchMode(false);
        id_staff.setFocusable(false);
        telephone_num.setFocusableInTouchMode(false);
        telephone_num.setFocusable(false);
        name_part.setFocusableInTouchMode(false);
        name_part.setFocusable(false);
        staff_position.setFocusableInTouchMode(false);
        staff_position.setFocusable(false);
        welcome_type.setFocusableInTouchMode(false);
        welcome_type.setFocusable(false);
    }
}
