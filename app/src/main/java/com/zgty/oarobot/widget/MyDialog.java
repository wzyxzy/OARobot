package com.zgty.oarobot.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgty.oarobot.R;


/**
 * Created by YaoChen on 2016/12/26.
 */

public class MyDialog extends Dialog {
    private Button yes;//确定按钮  
    private Button no;//取消按钮  
    private TextView messageTv;//消息提示文本  
    private String messageStr;//从外界设置的消息文本  
    private String yesStr, noStr;
    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器  
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器  
    private boolean showYes = true, showNo = true;
    private Context context;
    private View view;

    private LinearLayout layout_receipt_hint;//收款提示
    private TextView dialog_receipe_bills_count_content;//收款提示
    private TextView dialog_receipe_bills_money_content;//收款提示
    private String bills_count_content;
    private String bills_money_content;
    private boolean layout_IsShow = false;

    public MyDialog setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
        return this;
    }

    public MyDialog setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
        return this;
    }

    public MyDialog(Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mydialog);
        setCanceledOnTouchOutside(true);
        initView();
        initData();
        initEvent();
        WindowManager windowManager = getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        lp.height = (int) (display.getHeight());
        getWindow().setAttributes(lp);
    }

    public void setTextLeftImg(int id, Activity context) {
        Drawable topDrawable = context.getResources().getDrawable(id);
        topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
        messageTv.setCompoundDrawables(topDrawable, null, null, null);
    }

    public void setButtonVisible(boolean yesVisible, boolean cancelVisible) {
        this.showYes = yesVisible;
        this.showNo = cancelVisible;
        view.setVisibility(View.GONE);
        if (yes != null) {
            yes.setVisibility(showYes ? View.VISIBLE : View.GONE);
        }
        if (no != null) {
            no.setVisibility(showNo ? View.VISIBLE : View.GONE);
        }
    }

    private void initEvent() {
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                    dismiss();
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                    dismiss();
                }
            }
        });
    }

    private void initData() {

        if (messageStr != null) {
            messageTv.setText(messageStr);
        }
        if (yesStr != null) {
            yes.setText(yesStr);
        }
        if (noStr != null) {
            no.setText(noStr);
        }

        if (bills_money_content != null) {
            dialog_receipe_bills_money_content.setText(bills_money_content);
        }

        if (bills_count_content != null) {
            dialog_receipe_bills_count_content.setText(bills_count_content);
        }
    }

    private void initView() {
        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        yes.setVisibility(showYes ? View.VISIBLE : View.GONE);
        no.setVisibility(showNo ? View.VISIBLE : View.GONE);
        messageTv = (TextView) findViewById(R.id.message);
        view = (View) findViewById(R.id.view_dialog);
        if (layout_IsShow) {
            layout_receipt_hint = (LinearLayout) findViewById(R.id.layout_receipt_hint);
            layout_receipt_hint.setVisibility(View.VISIBLE);
            dialog_receipe_bills_count_content = (TextView) findViewById(R.id.dialog_receipe_bills_count_content);
            dialog_receipe_bills_money_content = (TextView) findViewById(R.id.dialog_receipe_bills_money_content);
        }
    }

    public MyDialog setMessage(String message) {
        messageStr = message;
        return this;
    }

    public interface onYesOnclickListener {
        public void onYesClick();
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }


    public MyDialog setReceiptHint(String count, String money, boolean isshow) {
        this.bills_count_content = count;
        this.bills_money_content = money;
        this.layout_IsShow = isshow;
        return this;
    }

    //弹框的操作
//    selfDialog = new MyDialog(Login.this);
//    selfDialog.setMessage("Do You want to delete this item?");
//    selfDialog.setYesOnclickListener("OK", new MyDialog.onYesOnclickListener() {
//        @Override
//        public void onYesClick() {
//            Toast.makeText(Login.this, "点击了--确定--按钮", Toast.LENGTH_LONG).show();
//            selfDialog.dismiss();
//        }
//    });
//    selfDialog.setNoOnclickListener("Cancel", new MyDialog.onNoOnclickListener() {
//        @Override
//        public void onNoClick() {
//            Toast.makeText(Login.this, "点击了--取消--按钮", Toast.LENGTH_LONG).show();
//            selfDialog.dismiss();
//        }
//    });
//    selfDialog.show();
}
