package com.zgty.oarobot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.zgty.oarobot.R;
import com.zgty.oarobot.common.CommonActivity;

public class ChatActivity extends CommonActivity implements View.OnClickListener {

    private TextView mode_name;
    private TextView change_mode;
    private ListView chat_goon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();

    }


    private void initView() {
        mode_name = (TextView) findViewById(R.id.mode_name);
        change_mode = (TextView) findViewById(R.id.change_mode);
        chat_goon = (ListView) findViewById(R.id.chat_goon);

        change_mode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_mode:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
