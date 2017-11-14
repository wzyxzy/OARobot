package com.zgty.oarobot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.zgty.oarobot.R;
import com.zgty.oarobot.bean.Staff;

import java.util.List;

/**
 * Created by zy on 2017/11/7.
 */

public class StaffChooseAdapter extends WZYBaseAdapter<Staff> {
    private final Context context;

    public StaffChooseAdapter(List<Staff> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindData(ViewHolder holder, Staff staff, int indexPostion) {
        TextView staff_name = (TextView) holder.getView(R.id.staff_name);
        TextView staff_depart = (TextView) holder.getView(R.id.staff_depart);
        TextView staff_num = (TextView) holder.getView(R.id.staff_num);
        TextView staff_no_face = (TextView) holder.getView(R.id.staff_no_face);
        staff_name.setText(staff.getName_user());
        staff_depart.setText(staff.getName_part());
        staff_num.setText("工号：" + staff.getId_clerk());
        if (staff.getIsRecordFace()) {
            staff_no_face.setTextColor(context.getResources().getColor(R.color.greenText));
            staff_no_face.setText(context.getResources().getText(R.string.face_has_record));
        } else {
            staff_no_face.setTextColor(context.getResources().getColor(R.color.redText));
            staff_no_face.setText(context.getResources().getText(R.string.face_no_record));
        }
    }
}
