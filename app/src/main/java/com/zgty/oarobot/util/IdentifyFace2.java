package com.zgty.oarobot.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.IdentityListener;
import com.iflytek.cloud.IdentityResult;
import com.iflytek.cloud.IdentityVerifier;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.zgty.oarobot.bean.UserIdentify;
import com.zgty.oarobot.common.Constant;

import static com.zgty.oarobot.common.Constant.MAIN_CHECK_CAMERA_TYPE;
import static com.zgty.oarobot.common.Constant.pGroupId;
import static com.zgty.oarobot.common.Constant.pScoreDivider;

/**
 * Created by zy on 2018/1/4.
 * 重写IdentifyFace,主要管理人脸以及人员得增删改查，以及讯飞人脸识别功能。
 */

public class IdentifyFace2 {
    private static final String TAG = IdentifyFace2.class.getSimpleName();
//    private DrawFacesView facesView;

    private Context context;
    private ToneGenerator tone;
    private ProgressDialog mProDialog;
    private IdentityVerifier mIdVerifier;
    private OnIdentifyListener onIdentifyListener;
    private byte[] data;
    private int type;
    private boolean safeToTakePicture = false;

    public IdentifyFace2(Context context) {
        this.context = context;
        mIdVerifier = IdentityVerifier.createVerifier(context, new InitListener() {
            @Override
            public void onInit(int errorCode) {
                if (ErrorCode.SUCCESS == errorCode) {
                    LogToastUtils.log(TAG, "引擎初始化成功");
                } else {
                    LogToastUtils.log(TAG, "引擎初始化失败，错误码：" + errorCode);
                }
            }
        });
    }

    public void setData(byte[] data) {
        this.data = data;
        if (MAIN_CHECK_CAMERA_TYPE == type) {

            // 清空参数
            mIdVerifier.setParameter(SpeechConstant.PARAMS, "");
            // 设置业务场景
            mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ifr");
            // 设置业务类型
            mIdVerifier.setParameter(SpeechConstant.MFV_SST, "identify");
            // 设置监听器，开始会话
            mIdVerifier.startWorking(mSearchListener);

            // 子业务执行参数，若无可以传空字符传
            StringBuffer params = new StringBuffer();
            params.append(",group_id=" + Constant.pGroupId + ",topc=3");
            // 向子业务写入数据，人脸数据可以一次写入
            mIdVerifier.writeData("ifr", params.toString(), data, 0, data.length);
            // 写入完毕
            mIdVerifier.stopWrite("ifr");
        } else {


            onIdentifyListener.onCapture();

            //
        }

    }


    public void setOnIdentifyListener(OnIdentifyListener onIdentifyListener) {

        this.onIdentifyListener = onIdentifyListener;
    }


    /**
     * 初始化人脸识别
     *
     * @param context 上下文
     * @param type    识别/录入 类型
     */
    public IdentifyFace2(final Context context, int type) {
        this.context = context;
//        facesView = new DrawFacesView(context);
        this.type = type;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            facesView = new DrawFacesView(context);
//            camera_preview.addView(facesView);
//        }
//        camera_preview.addView(facesView);

//        activity.addContentView(mPreview,camera_preview.getLayoutParams());
//        activity.addContentView(facesView,null);
        mProDialog = new ProgressDialog(context);
        mProDialog.setCancelable(true);
        mProDialog.setTitle("请稍候");
        mIdVerifier = IdentityVerifier.createVerifier(context, new InitListener() {
            @Override
            public void onInit(int errorCode) {
                if (ErrorCode.SUCCESS == errorCode) {
                    LogToastUtils.log(TAG, "引擎初始化成功");
                } else {
                    LogToastUtils.log(TAG, "引擎初始化失败，错误码：" + errorCode);
                }
            }
        });
        // cancel进度框时，取消正在进行的操作
        mProDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (null != mIdVerifier) {
                    mIdVerifier.cancel();
                }
            }
        });


    }

    /**
     * 添加员工到群组
     *
     * @param id_staff 员工id
     */
    public void addStaff(final String id_staff) {
        // sst=add，auth_id=eqhe，group_id=123456，scope=person
        mIdVerifier.setParameter(SpeechConstant.PARAMS, null);
        // 设置会话场景
        mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ipt");
        // 用户id
        mIdVerifier.setParameter(SpeechConstant.AUTH_ID, id_staff);
        // 设置模型参数，若无可以传空字符传
        StringBuffer params2 = new StringBuffer();
        params2.append("auth_id=" + id_staff);
        params2.append(",scope=person");
        params2.append(",group_id=" + Constant.pGroupId);
        // 执行模型操作
        mIdVerifier.execute("ipt", "add", params2.toString(), new IdentityListener() {
            @Override
            public void onResult(IdentityResult identityResult, boolean b) {
                Log.d(TAG, identityResult.getResultString());
                UserIdentify groupManagerBack = new Gson().fromJson(identityResult.getResultString(), UserIdentify.class);
                if (groupManagerBack.getRet() == ErrorCode.SUCCESS) {
                    registerStaff(id_staff);
                } else {
                    LogToastUtils.toastShort(context, new SpeechError(groupManagerBack.getRet()).getPlainDescription(true));
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                if (speechError.getErrorCode() == 10121) {
//                    registerStaff(id_staff);
//                    deleteFace(id_staff);
                    deleteFromParent(id_staff);
                }
//                LogToastUtils.toastShort(context, speechError.getPlainDescription(true));
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
    }

    /**
     * 注册员工人脸
     *
     * @param id_staff 员工id
     */
    private void registerStaff(String id_staff) {
        if (data != null) {
            // 清空参数
            mIdVerifier.setParameter(SpeechConstant.PARAMS, null);
            // 设置会话场景
            mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ifr");
            // 设置会话类型
            mIdVerifier.setParameter(SpeechConstant.MFV_SST, "enroll");
            // 设置用户id
            mIdVerifier.setParameter(SpeechConstant.AUTH_ID, id_staff);
            // 设置监听器，开始会话
            mIdVerifier.startWorking(mEnrollListener);
            // 子业务执行参数，若无可以传空字符传
            StringBuffer params = new StringBuffer();
            // 向子业务写入数据，人脸数据可以一次写入
            mIdVerifier.writeData("ifr", params.toString(), data, 0, data.length);
            // 停止写入
            mIdVerifier.stopWrite("ifr");
        }

    }

    /**
     * 只删除，不返回
     */
    public void deleteFromParent(final String id_staff) {
        // sst=add，auth_id=eqhe，group_id=123456，scope=person
        mIdVerifier.setParameter(SpeechConstant.PARAMS, null);
        // 设置会话场景
        mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ipt");
        // 用户id
        mIdVerifier.setParameter(SpeechConstant.AUTH_ID, id_staff);

        // 设置模型参数，若无可以传空字符传
        StringBuffer params2 = new StringBuffer();

        params2.append("scope=person");
        params2.append(",auth_id=" + id_staff);
        params2.append(",group_id=" + pGroupId);
        // 执行模型操作
        mIdVerifier.execute("ipt", "delete", params2.toString(), new IdentityListener() {
            @Override
            public void onResult(IdentityResult result, boolean islast) {
                deleteFacePic(id_staff);
            }

            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            }

            @Override
            public void onError(SpeechError error) {

            }
        });
    }

    /**
     * 删除人脸
     *
     * @param id_staff 员工id
     */
    public void deleteFace(final String id_staff) {
        // sst=add，auth_id=eqhe，group_id=123456，scope=person
        mIdVerifier.setParameter(SpeechConstant.PARAMS, null);
        // 设置会话场景
        mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ipt");
        // 用户id
        mIdVerifier.setParameter(SpeechConstant.AUTH_ID, id_staff);

        // 设置模型参数，若无可以传空字符传
        StringBuffer params2 = new StringBuffer();

        params2.append("scope=person");
        params2.append(",auth_id=" + id_staff);
        params2.append(",group_id=" + pGroupId);
        // 执行模型操作
        mIdVerifier.execute("ipt", "delete", params2.toString(), new IdentityListener() {
            @Override
            public void onResult(IdentityResult result, boolean islast) {
                Log.d(TAG, result.getResultString());
                UserIdentify groupManagerBack = new Gson().fromJson(result.getResultString(), UserIdentify.class);
                if (groupManagerBack.getRet() == ErrorCode.SUCCESS) {
                    deleteStaff(id_staff);
                } else {
                    LogToastUtils.log(TAG, new SpeechError(groupManagerBack.getRet()).getPlainDescription(true));
                }
            }

            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            }

            @Override
            public void onError(SpeechError error) {
                LogToastUtils.log(TAG, error.getPlainDescription(true));
                if (error.getErrorCode() == 10142) {
                    deleteStaff(id_staff);
                }
            }
        });

    }

    private void deleteStaff(String id_staff) {
        // 清空参数
        mIdVerifier.setParameter(SpeechConstant.PARAMS, null);
        // 设置会话场景
        mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ifr");
        // 用户id
        mIdVerifier.setParameter(SpeechConstant.AUTH_ID, id_staff);

        // 设置模型参数，若无可以传空字符传
        StringBuffer params = new StringBuffer();
        // 执行模型操作
        mIdVerifier.execute("ifr", "delete", params.toString(), new IdentityListener() {
            @Override
            public void onResult(IdentityResult identityResult, boolean b) {
                Log.d(TAG, identityResult.getResultString());
                UserIdentify groupManagerBack = new Gson().fromJson(identityResult.getResultString(), UserIdentify.class);
                if (groupManagerBack.getRet() == ErrorCode.SUCCESS) {
                    onIdentifyListener.onRegisterSuccess();
//                    deleteStaff(id_staff);
                } else {
                    LogToastUtils.log(TAG, new SpeechError(groupManagerBack.getRet()).getPlainDescription(true));
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                LogToastUtils.log(TAG, speechError.getPlainDescription(true));
                if (speechError.getErrorCode() == 10116) {
                    onIdentifyListener.onRegisterSuccess();
                }
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
    }

    /**
     * 删除人脸
     */
    private void deleteFacePic(final String id_staff) {
        // 清空参数
        mIdVerifier.setParameter(SpeechConstant.PARAMS, null);
        // 设置会话场景
        mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ifr");
        // 用户id
        mIdVerifier.setParameter(SpeechConstant.AUTH_ID, id_staff);

        // 设置模型参数，若无可以传空字符传
        StringBuffer params = new StringBuffer();
        // 执行模型操作
        mIdVerifier.execute("ifr", "delete", params.toString(), new IdentityListener() {
            @Override
            public void onResult(IdentityResult identityResult, boolean b) {
                Log.d(TAG, identityResult.getResultString());
                UserIdentify groupManagerBack = new Gson().fromJson(identityResult.getResultString(), UserIdentify.class);
                if (groupManagerBack.getRet() == ErrorCode.SUCCESS) {
                    addStaff(id_staff);
//                    deleteStaff(id_staff);
                } else {
                    LogToastUtils.log(TAG, new SpeechError(groupManagerBack.getRet()).getPlainDescription(true));
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                LogToastUtils.log(TAG, speechError.getPlainDescription(true));
                if (speechError.getErrorCode() == 10116) {
                    addStaff(id_staff);
                }
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
    }

    /**
     * 人脸注册,删除监听器
     */
    private IdentityListener mEnrollListener = new IdentityListener() {

        @Override
        public void onResult(IdentityResult result, boolean islast) {
            Log.d(TAG, result.getResultString());
            UserIdentify groupManagerBack = new Gson().fromJson(result.getResultString(), UserIdentify.class);
            if (groupManagerBack.getRet() == ErrorCode.SUCCESS) {
                onIdentifyListener.onRegisterSuccess();
            } else {
                LogToastUtils.log(TAG, new SpeechError(groupManagerBack.getRet()).getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

        @Override
        public void onError(SpeechError error) {
            LogToastUtils.log(TAG, error.getPlainDescription(true));
            if (error.getErrorCode() == 11700) {
                onIdentifyListener.onError();
            } else if (error.getErrorCode() == 10121) {
                onIdentifyListener.onRegisterSuccess();
            }
        }

    };


    /**
     * 人脸鉴别监听器
     */
    private IdentityListener mSearchListener = new IdentityListener() {

        @Override
        public void onResult(IdentityResult result, boolean islast) {
            Log.d(TAG, result.getResultString());


            handleResult(result);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
//            mCamera.startPreview();
        }

        @Override
        public void onError(SpeechError error) {

            LogToastUtils.log(TAG, error.getPlainDescription(true));
            if (error.getErrorCode() == 10116) {//没有录入
                onIdentifyListener.onSwitch(data);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mCamera != null)
//                            mCamera.startPreview();
//                    }
//                }, 15000);
            } else {
//                mTts.startSpeaking("没有检测到人脸", null);
//                startCameraView();

            }

        }

    };

    private void dismissProDialog() {
        if (null != mProDialog) {
            mProDialog.dismiss();
        }
    }


    /**
     * 人脸识别返回结果处理
     *
     * @param result 返回结果
     */
    protected void handleResult(IdentityResult result) {
        if (null == result) {
            return;
        }


        String resultStr = result.getResultString();
//            JSONObject resultJson = new JSONObject(resultStr);
        Gson gson = new Gson();

        UserIdentify userIdentify = gson.fromJson(resultStr, UserIdentify.class);
        if (ErrorCode.SUCCESS == userIdentify.getRet()) {
            if (userIdentify.getIfv_result().getCandidates().get(0).getScore() > pScoreDivider) {
                if (userIdentify.getIfv_result().getCandidates().get(0).getUser().startsWith("visitor")){
                    onIdentifyListener.onSuccess(userIdentify.getIfv_result().getCandidates().get(0).getUser(), data);
                }else {
                    onIdentifyListener.onSuccess(userIdentify.getIfv_result().getCandidates().get(0).getUser(), null);
                }


            } else {//没有过阈值
                onIdentifyListener.onSwitch(data);

            }

        } else {
            LogToastUtils.log(TAG, "识别失败！");
            onIdentifyListener.onError();
        }

    }

    public void finisheIdentify() {
        if (null != mIdVerifier) {
            mIdVerifier.destroy();
            mIdVerifier = null;
        }
    }


    //回调接口
    public interface OnIdentifyListener {
        void onSuccess(String user_id, byte[] b);

        void onSwitch(byte[] b);

        void onError();

        void onCapture();

        void onRegisterSuccess();
    }
}
