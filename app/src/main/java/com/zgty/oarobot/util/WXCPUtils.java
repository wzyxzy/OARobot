package com.zgty.oarobot.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.zgty.oarobot.R;
import com.zgty.oarobot.bean.AccessTokenWX;
import com.zgty.oarobot.bean.MessageBuilder;
import com.zgty.oarobot.bean.TokenBack;
import com.zgty.oarobot.dao.TokenDaoUtils;

import java.io.File;

/**
 * Created by zy on 2017/11/28.
 * 微信企业号发送文字的工具类
 */

public class WXCPUtils {

    private static final String TAG = WXCPUtils.class.getSimpleName();
    private Context context;
    private String token;
    private String media_id;
    private String text;
    private String type;
    private File file;
    private String userid;
    private TokenDaoUtils tokenDaoUtils;
    private AccessTokenWX accessTokenWX;
    private OnWXCPUtilsListener onWXCPUtilsListener;
    private int handmessge;

    public void setOnWXCPUtilsListener(OnWXCPUtilsListener onWXCPUtilsListener) {
        this.onWXCPUtilsListener = onWXCPUtilsListener;
    }


    public WXCPUtils(Context context) {
        this.context = context;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://发送文本
                    String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token;
                    MessageBuilder builder = new MessageBuilder();
                    builder.setAgentid(1000002);
                    builder.setTouser(userid);
                    builder.setToparty("");
                    builder.setTotag("");
                    builder.setMsgtype("text");
                    builder.setSafe(0);
                    MessageBuilder.TextBean textBean = new MessageBuilder.TextBean();
                    textBean.setContent(text);
                    builder.setText(textBean);
                    String json = new Gson().toJson(builder);
                    OkGo.<String>post(url).upJson(json).execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            TokenBack tokenBack = new Gson().fromJson(response.body(), TokenBack.class);
                            if (tokenBack.getErrcode() != 0) {
                                onWXCPUtilsListener.onError();
                                LogToastUtils.log(TAG, "errorCode=" + tokenBack.getErrcode() + ",and errorMsg=" + tokenBack.getErrmsg());
                            } else {
                                if (type.equalsIgnoreCase("text_out")) {
                                    onWXCPUtilsListener.onSuccess();
                                } else if (!type.equalsIgnoreCase("text")) {
                                    handler.sendEmptyMessage(1);
                                }

                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            onWXCPUtilsListener.onError();
                            LogToastUtils.log(TAG, response.message());
                        }
                    });
                    break;
                case 1://上传临时文件
                    String urlPic = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=" + token + "&type=" + type;
                    OkGo.<String>post(urlPic)
                            .params("media", file)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    TokenBack tokenBack = new Gson().fromJson(response.body(), TokenBack.class);
                                    if (tokenBack.getErrcode() != 0) {
                                        onWXCPUtilsListener.onError();
                                        LogToastUtils.log(TAG, "errorCode=" + tokenBack.getErrcode() + ",and errorMsg=" + tokenBack.getErrmsg());
                                    } else {
                                        media_id = tokenBack.getMedia_id();
                                        handler.sendEmptyMessage(2);

                                    }
                                }

                                @Override
                                public void onError(Response<String> response) {
                                    super.onError(response);
                                    onWXCPUtilsListener.onError();
                                    LogToastUtils.log(TAG, response.message());
                                }
                            });
                    break;
                case 2://发送图片信息
                    String urlImg = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token;
                    MessageBuilder messageBuilder = new MessageBuilder();
                    messageBuilder.setAgentid(1000002);
                    messageBuilder.setTouser(userid);
                    messageBuilder.setToparty("");
                    messageBuilder.setTotag("");
                    messageBuilder.setMsgtype(type);
                    messageBuilder.setSafe(0);
                    switch (type) {
                        case "image":
                            MessageBuilder.ImageBean imageBean = new MessageBuilder.ImageBean();
                            imageBean.setMedia_id(media_id);
                            messageBuilder.setImage(imageBean);
                            break;
                        case "file":
                            MessageBuilder.FileBean fileBean = new MessageBuilder.FileBean();
                            fileBean.setMedia_id(media_id);
                            messageBuilder.setFile(fileBean);
                            break;
                    }
                    String jsonMsg = new Gson().toJson(messageBuilder);
                    OkGo.<String>post(urlImg).upJson(jsonMsg).execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            TokenBack tokenBack = new Gson().fromJson(response.body(), TokenBack.class);
                            if (tokenBack.getErrcode() != 0) {
                                onWXCPUtilsListener.onError();
                                LogToastUtils.log(TAG, "errorCode=" + tokenBack.getErrcode() + ",and errorMsg=" + tokenBack.getErrmsg());
                            } else {
                                onWXCPUtilsListener.onSuccess();
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            onWXCPUtilsListener.onError();
                            LogToastUtils.log(TAG, response.message());
                        }
                    });

                    break;
            }
        }
    };


    public void sendText(File file, String userid, String text, String type) {
        this.file = file;
        this.userid = userid;
        this.text = text;
        this.type = type;
        getToken();
        handmessge = 0;
    }

    private void getToken() {
        tokenDaoUtils = new TokenDaoUtils(context);
        accessTokenWX = tokenDaoUtils.queryAccessToken(context.getString(R.string.weixin_corp_id));
        if (accessTokenWX != null) {
            if (accessTokenWX.getTime() + 7200 > System.currentTimeMillis() / 1000) {
                token = accessTokenWX.getToken();
                handler.sendEmptyMessage(handmessge);
            } else {
                getTokenFromWX(0);
            }
        } else {
            getTokenFromWX(1);
        }
    }

    private void getTokenFromWX(final int type) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + context.getString(R.string.weixin_corp_id) + "&corpsecret=" + context.getString(R.string.weixin_corpsecret);
        OkGo.<String>get(url).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                TokenBack tokenBack = new Gson().fromJson(response.body(), TokenBack.class);
                if (tokenBack.getErrcode() != 0) {
                    LogToastUtils.log(TAG, "errorCode=" + tokenBack.getErrcode() + ",and errorMsg=" + tokenBack.getErrmsg());
                    onWXCPUtilsListener.onError();
                } else {
                    token = tokenBack.getAccess_token();
                    if (type == 0) {
                        accessTokenWX.setTime(System.currentTimeMillis() / 1000);
                        accessTokenWX.setToken(token);
                        tokenDaoUtils.updateAccessToken(accessTokenWX);
                    } else {
                        accessTokenWX = new AccessTokenWX();
                        accessTokenWX.setTime(System.currentTimeMillis() / 1000);
                        accessTokenWX.setToken(token);
                        accessTokenWX.setCorpid(context.getString(R.string.weixin_corp_id));
                        tokenDaoUtils.insertAccessToken(accessTokenWX);
                    }
                    handler.sendEmptyMessage(handmessge);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                LogToastUtils.log(TAG, response.message());
                onWXCPUtilsListener.onError();
            }
        });
    }


    //回调接口
    public interface OnWXCPUtilsListener {
        void onSuccess();

        void onError();

    }
}
