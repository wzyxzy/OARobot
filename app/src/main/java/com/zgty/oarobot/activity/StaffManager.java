package com.zgty.oarobot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zgty.oarobot.R;
import com.zgty.oarobot.adapter.StaffChooseAdapter;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.common.CommonActivity;
import com.zgty.oarobot.dao.StaffDaoUtils;

import java.util.List;

public class StaffManager extends CommonActivity implements View.OnClickListener {

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        StaffDaoUtils staffDaoUtils = new StaffDaoUtils(this);
        staffList = staffDaoUtils.queryStaffList();
        staffChooseAdapter.updateRes(staffList);

    }

    private void initView() {
        back_admin = findViewById(R.id.back_admin);
        back_admin.setOnClickListener(this);
        staff_listview = findViewById(R.id.staff_listview);
        staff_add = findViewById(R.id.staff_add);
        staff_add.setOnClickListener(this);
        staffChooseAdapter = new StaffChooseAdapter(staffList, this, R.layout.staff_item);
        staff_listview.setAdapter(staffChooseAdapter);
        staff_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), StaffDetail.class);
                intent.putExtra("detail_type", 1);
                intent.putExtra("staff_id", staffList.get(position).getId());
                startActivity(intent);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_admin:
                finish();
                break;
            case R.id.staff_add:
                Intent intent = new Intent(getApplicationContext(), StaffDetail.class);
                intent.putExtra("detail_type", 0);
                intent.putExtra("staff_id", "");
                startActivity(intent);
                break;
        }
    }




}
