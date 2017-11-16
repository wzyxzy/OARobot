package com.zgty.oarobot.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.zgty.oarobot.R;
import com.zgty.oarobot.bean.Time;
import com.zgty.oarobot.common.CommonActivity;
import com.zgty.oarobot.dao.TimeDaoUtils;
import com.zgty.oarobot.widget.MyDialog;

import java.util.Calendar;
import java.util.List;

public class TimeManageActivity extends CommonActivity implements View.OnClickListener {

    private TextView back_admin;
    private TextView time_on_time;
    private TextView time_off_time;
    private EditText time_late_min;
    private EditText time_early_min;
    private TextView time_add_time;
    private TextView edit_cancel;
    private TextView edit_sure;
    private LinearLayout edit_sure_cancel;
    private List<Time> times;
    private TimeDaoUtils timeDaoUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_manage);
        initView();
        initData();
    }

    private void initData() {
        timeDaoUtils = new TimeDaoUtils(this);
        times = timeDaoUtils.queryTimeList();
        for (int i = 0; i < times.size(); i++) {
            switch (times.get(i).getId()) {
                case "time_on":
                    time_on_time.setText(times.get(i).getTime());
                    break;
                case "time_off":
                    time_off_time.setText(times.get(i).getTime());
                    break;
                case "late_min":
                    time_late_min.setText(times.get(i).getTime());
                    break;
                case "early_min":
                    time_early_min.setText(times.get(i).getTime());
                    break;
                case "time_add":
                    time_add_time.setText(times.get(i).getTime());
                    break;

            }
        }

    }

    private void initView() {
        back_admin = findViewById(R.id.back_admin);
        time_on_time = findViewById(R.id.time_on_time);
        time_off_time = findViewById(R.id.time_off_time);
        time_late_min = findViewById(R.id.time_late_min);
        time_early_min = findViewById(R.id.time_early_min);
        time_add_time = findViewById(R.id.time_add_time);
        edit_cancel = findViewById(R.id.edit_cancel);
        edit_sure = findViewById(R.id.edit_sure);
        edit_sure_cancel = findViewById(R.id.edit_sure_cancel);

        edit_cancel.setOnClickListener(this);
        back_admin.setOnClickListener(this);
        edit_sure.setOnClickListener(this);
        time_on_time.setOnClickListener(this);
        time_off_time.setOnClickListener(this);
        time_add_time.setOnClickListener(this);
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
                myDialog.setMessage("您确定要保存修改的时间吗？");
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
            case R.id.time_on_time:
                showtime(time_on_time);
                break;
            case R.id.time_off_time:
                showtime(time_off_time);
                break;
            case R.id.time_add_time:
                showtime(time_add_time);
                break;
        }
    }


    private void showtime(final TextView timetext) {

//        c.setTimeInMillis(System.currentTimeMillis());
        final StringBuilder str = new StringBuilder("");

        Calendar time = Calendar.getInstance();
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if (minute < 10) {
                    str.append(" " + hour + ":0" + minute);
                    timetext.setText(str);
                } else {
                    str.append(" " + hour + ":" + minute);
                    timetext.setText(str);
                }
            }
        }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true).show();
    }


    private void submit() {
        // validate
        String time_late = time_late_min.getText().toString().trim();
        if (TextUtils.isEmpty(time_late)) {
            Toast.makeText(this, "min不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String time_early = time_early_min.getText().toString().trim();
        if (TextUtils.isEmpty(time_early)) {
            Toast.makeText(this, "min不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String time_on_time = this.time_on_time.getText().toString().trim();
        String time_off_time = this.time_off_time.getText().toString().trim();
        String time_add_time = this.time_add_time.getText().toString().trim();
        // TODO validate success, do something
        for (int i = 0; i < times.size(); i++) {
            switch (times.get(i).getId()) {
                case "time_on":
                    times.get(i).setTime(time_on_time);
                    break;
                case "time_off":
                    times.get(i).setTime(time_off_time);

                    break;
                case "late_min":
                    times.get(i).setTime(time_late);

                    break;
                case "early_min":
                    times.get(i).setTime(time_early);

                    break;
                case "time_add":
                    times.get(i).setTime(time_add_time);

                    break;

            }
        }
        timeDaoUtils.updateStaff(times);
        finish();
    }
}
