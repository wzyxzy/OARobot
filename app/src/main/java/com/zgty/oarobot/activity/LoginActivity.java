package com.zgty.oarobot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zgty.oarobot.R;
import com.zgty.oarobot.bean.Account;
import com.zgty.oarobot.common.CommonActivity;
import com.zgty.oarobot.dao.AccountDaoUtils;
import com.zgty.oarobot.util.LogToastUtils;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static com.zgty.oarobot.common.Constant.nowAccount;

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
    private int type;
    private LinearLayout pass_again_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

    private void initData() {
        type = getIntent().getIntExtra("type", 1);
        switch (type) {
            case 1:
                title_name.setText(getResources().getString(R.string.account_access));
                account_name.setText(getResources().getString(R.string.login_account));
                login_account.setInputType(TYPE_CLASS_TEXT);
                pass_again_layout.setVisibility(View.INVISIBLE);
                edit_sure.setText(getResources().getString(R.string.login_goto));
                break;
            case 2:
                title_name.setText(getResources().getString(R.string.pass_manage));
                account_name.setText(getResources().getString(R.string.input_orial_pass));
                login_account.setInputType(TYPE_TEXT_VARIATION_PASSWORD);
                pass_again_layout.setVisibility(View.VISIBLE);
                edit_sure.setText(getResources().getString(R.string.sure_name));
                break;
        }
    }

    private void initView() {
        back_admin = findViewById(R.id.back_admin);
        title_name = findViewById(R.id.title_name);
        account_name = findViewById(R.id.account_name);
        login_account = findViewById(R.id.login_account);
        password_name = findViewById(R.id.password_name);
        login_password = findViewById(R.id.login_password);
        input_new_pass_again = findViewById(R.id.input_new_pass_again);
        edit_cancel = findViewById(R.id.edit_cancel);
        edit_sure = findViewById(R.id.edit_sure);
        edit_sure_cancel = findViewById(R.id.edit_sure_cancel);

        back_admin.setOnClickListener(this);
        edit_cancel.setOnClickListener(this);
        edit_sure.setOnClickListener(this);
        pass_again_layout = findViewById(R.id.pass_again_layout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_cancel:
            case R.id.back_admin:
                finish();
                break;
            case R.id.edit_sure:
                submit();
                break;
        }
    }

    private void submit() {
        // validate
        String account = login_account.getText().toString().trim();
        String password = login_password.getText().toString().trim();
        String again = input_new_pass_again.getText().toString().trim();
        switch (type) {
            case 1:
                if (TextUtils.isEmpty(account)) {
                    Toast.makeText(this, "请输入账户", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case 2:
                if (TextUtils.isEmpty(account)) {
                    Toast.makeText(this, "请输入原密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(again)) {
                    Toast.makeText(this, "请再次输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }


        // TODO validate success, do something
        switch (type) {
            case 1:
                try {
                    if (new AccountDaoUtils(this).queryAccountPass(account).equals(password)) {
                        nowAccount = account;
                        Intent intent = new Intent(this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        LogToastUtils.toastShort(this, "密码错误!");
                    }
                } catch (IndexOutOfBoundsException e) {
                    LogToastUtils.toastShort(this, "账号不存在!");

                }
                break;
            case 2:
                try {
                    AccountDaoUtils accountDaoUtils = new AccountDaoUtils(this);
                    if (accountDaoUtils.queryAccountPass(nowAccount).equals(account)) {
                        if (password.equals(again)) {
                            Account account1 = new Account(nowAccount, password);
                            accountDaoUtils.updateAccount(account1);
                            LogToastUtils.toastShort(this, "密码修改成功!");
                        } else {
                            LogToastUtils.toastShort(this, "新密码两次输入不一致!");
                        }
                    } else {
                        LogToastUtils.toastShort(this, "原密码错误!");
                    }
                } catch (IndexOutOfBoundsException e) {
//                    LogToastUtils.toastShort(this, "账号不存在!");

                }
                break;
        }


    }
}
