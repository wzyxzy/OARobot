package com.zgty.oarobot.util;

import android.content.Context;

import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zgty.oarobot.R;

/**
 * Created by zy on 2017/11/21.
 */

public class WeiXinUtils {
    private Context context;
    private IWXAPI iwxapi;

    public WeiXinUtils(Context context) {
        this.context = context;
        iwxapi = WXAPIFactory.createWXAPI(context, context.getString(R.string.weixin_app_id), true);
        iwxapi.registerApp(context.getString(R.string.weixin_app_id));
    }

    public void SendText(String text) {
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;
    }
}
