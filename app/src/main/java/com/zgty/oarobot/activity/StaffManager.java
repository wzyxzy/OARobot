package com.zgty.oarobot.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.zgty.oarobot.R;
import com.zgty.oarobot.adapter.StaffChooseAdapter;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.common.CommonActivity;
import com.zgty.oarobot.dao.StaffDaoUtils;

import java.util.List;

public class StaffManager extends CommonActivity {

    private TextView back_admin;
    private ListView staff_listview;
    private TextView staff_add;
    private List<Staff> staffList;
    private StaffChooseAdapter staffChooseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_manager);
        initView();
        initData();

    }

    private void initData() {
        StaffDaoUtils staffDaoUtils = new StaffDaoUtils(this);
        staffList = staffDaoUtils.queryStaffList();
        staffChooseAdapter.updateRes(staffList);

    }

    private void initView() {
        back_admin = findViewById(R.id.back_admin);
        staff_listview = findViewById(R.id.staff_listview);
        staff_add = findViewById(R.id.staff_add);
        staffChooseAdapter = new StaffChooseAdapter(staffList, this, R.layout.staff_item);
        staff_listview.setAdapter(staffChooseAdapter);
    }


}
