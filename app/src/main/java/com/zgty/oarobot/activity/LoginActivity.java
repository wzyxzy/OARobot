package com.zgty.oarobot.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zgty.oarobot.R;
import com.zgty.oarobot.common.CommonActivity;

public class LoginActivity extends CommonActivity implements View.OnClickListener {

    private TextView back_admin;
    private TextView title_name;
    private TextView account_name;
    private EditText login_account;
    private TextView password_name;
    private EditText login_password;
    private EditText input_new_pass_again;
    private TextView edit_cancel;
    private TextView edit_sure;
    private LinearLayout edit_sure_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        back_admin =  findViewById(R.id.back_admin);
        title_name =  findViewById(R.id.title_name);
        account_name =  findViewById(R.id.account_name);
        login_account =  findViewById(R.id.login_account);
        password_name =  findViewById(R.id.password_name);
        login_password =  findViewById(R.id.login_password);
        input_new_pass_again =  findViewById(R.id.input_new_pass_again);
        edit_cancel =  findViewById(R.id.edit_cancel);
        edit_sure =  findViewById(R.id.edit_sure);
        edit_sure_cancel =  findViewById(R.id.edit_sure_cancel);

        edit_cancel.setOnClickListener(this);
        edit_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_cancel:

                break;
            case R.id.edit_sure:

                break;
        }
    }

    private void submit() {
        // validate
        String account = login_account.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "account不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = login_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "password不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

//        String again = input_new_pass_again.getText().toString().trim();
//        if (TextUtils.isEmpty(again)) {
//            Toast.makeText(this, "again不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // TODO validate success, do something


    }
}
