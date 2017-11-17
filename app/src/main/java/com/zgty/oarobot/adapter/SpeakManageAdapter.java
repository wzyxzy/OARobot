package com.zgty.oarobot.adapter;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zgty.oarobot.R;
import com.zgty.oarobot.bean.Speaking;

import java.util.List;

/**
 * Created by zy on 2017/11/17.
 */

public class SpeakManageAdapter extends WZYBaseAdapter<Speaking> {
    public SpeakManageAdapter(List<Speaking> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, final Speaking speaking, int indexPostion) {
        TextView scene_speak = (TextView) holder.getView(R.id.scene_speak);
        final EditText text_speak = (EditText) holder.getView(R.id.text_speak);
        scene_speak.setText(speaking.getName());
        text_speak.setText(speaking.getText());
        text_speak.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    speaking.setText(text_speak.getText().toString().trim());
                }
            }
        });


    }
}
