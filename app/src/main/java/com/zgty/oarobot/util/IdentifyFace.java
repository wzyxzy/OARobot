package com.zgty.oarobot.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
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
import com.zgty.oarobot.widget.DrawFacesView;

import java.io.IOException;

import static com.zgty.oarobot.common.Constant.pScoreDivider;
import static com.zgty.oarobot.common.OARobotApplication.mTts;

/**
 * Created by zy on 2017/11/6.
 */

public class IdentifyFace {
    private static final String TAG = IdentifyFace.class.getSimpleName();
    private DrawFacesView facesView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private SurfaceView mPreview;
    private FrameLayout camera_preview;
    private Context context;
    private ToneGenerator tone;
    private ProgressDialog mProDialog;
    private IdentityVerifier mIdVerifier;
    private OnIdentifyListener onIdentifyListener;

    public void setOnIdentifyListener(OnIdentifyListener onIdentifyListener) {

        this.onIdentifyListener = onIdentifyListener;
    }


    public IdentifyFace(FrameLayout camera_preview, final Context context) {
        this.camera_preview = camera_preview;
        this.context = context;
        mPreview = new SurfaceView(context);
        facesView = new DrawFacesView(context);
        camera_preview.addView(mPreview);
        camera_preview.addView(facesView);
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
     * 把摄像头的图像显示到SurfaceView
     */
    public void openSurfaceView() {
        if (null == mIdVerifier) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            LogToastUtils.toastShort(context, "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }
        mHolder = mPreview.getHolder();
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (mCamera == null) {
                    mCamera = Camera.open();
                    try {
                        mCamera.setFaceDetectionListener(new FaceDetectorListener());
                        mCamera.setPreviewDisplay(holder);
                        startFaceDetection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mHolder.getSurface() == null) {
                    // preview surface does not exist
                    Log.e(TAG, "mHolder.getSurface() == null");
                    return;
                }

                try {
                    mCamera.stopPreview();

                } catch (Exception e) {
                    // ignore: tried to stop a non-existent preview
                    Log.e(TAG, "Error stopping camera preview: " + e.getMessage());
                }

                try {
                    mCamera.setPreviewDisplay(mHolder);
                    int measuredWidth = mPreview.getWidth();
                    int measuredHeight = mPreview.getHeight();
                    setCameraParms(mCamera, measuredWidth, measuredHeight);
                    mCamera.startPreview();

                    startFaceDetection(); // re-start face detection feature

                } catch (Exception e) {
                    // ignore: tried to stop a non-existent preview
                    Log.d(TAG, "Error starting camera preview: " + e.getMessage());
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                holder = null;
            }
        });
    }

    /**
     * 在摄像头启动前设置参数
     *
     * @param camera
     * @param width
     * @param height
     */
    private void setCameraParms(Camera camera, int width, int height) {
        // 获取摄像头支持的pictureSize列表
        Camera.Parameters parameters = camera.getParameters();
        /*List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        // 从列表中选择合适的分辨率
        Camera.Size pictureSize = getProperSize(pictureSizeList, (float) height / width);
        if (null == pictureSize) {
            pictureSize = parameters.getPictureSize();
        }
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = pictureSize.width;
        float h = pictureSize.height;
        parameters.setPictureSize(pictureSize.width, pictureSize.height);

        surfaceView.setLayoutParams(new FrameLayout.LayoutParams((int) (height * (h / w)), height));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        Camera.Size preSize = getProperSize(previewSizeList, (float) height / width);
        if (null != preSize) {
            parameters.setPreviewSize(preSize.width, preSize.height);
        }
*/
        parameters.setJpegQuality(100);
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            // 连续对焦
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        camera.cancelAutoFocus();
        camera.setDisplayOrientation(0);
        camera.setParameters(parameters);
    }

    public void startFaceDetection() {
        // Try starting Face Detection
        Camera.Parameters params = mCamera.getParameters();
        // start face detection only *after* preview has started
        if (params.getMaxNumDetectedFaces() > 0) {
            // mCamera supports face detection, so can start it:
            mCamera.startFaceDetection();
        } else {
            Log.e("tag", "【FaceDetectorActivity】类的方法：【startFaceDetection】: " + "不支持");
        }
    }

    /**
     * 脸部检测接口
     */
    private class FaceDetectorListener implements Camera.FaceDetectionListener {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if (faces.length > 0) {
                Camera.Face face = faces[0];
                Rect rect = face.rect;
                Log.d("FaceDetection", "可信度：" + face.score + "face detected: " + faces.length +
                        " Face 1 Location X: " + rect.centerX() +
                        "Y: " + rect.centerY() + "   " + rect.left + " " + rect.top + " " + rect.right + " " + rect.bottom);
                Log.e("tag", "【FaceDetectorListener】类的方法：【onFaceDetection】: ");
                Matrix matrix = updateFaceRect();
                facesView.updateFaces(matrix, faces);
                //拍照，并上传到讯飞

                try {
//                    camera.stopPreview();
                    camera.takePicture(shutterCallback, null, jpegCallback);
                    camera.stopPreview();

                } catch (Exception e) {
                    // ignore: tried to stop a non-existent preview
                    Log.e(TAG, "Error stopping camera preview: " + e.getMessage());
                }
            } else {
                // 只会执行一次
                Log.e("tag", "【FaceDetectorListener】类的方法：【onFaceDetection】: " + "没有脸部");
                facesView.removeRect();
            }
        }
    }

    //返回照片的JPEG格式的数据
    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            if (null != data) {
                mProDialog.setMessage("正在识别...");
                mProDialog.show();
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
                LogToastUtils.toastShort(context, "请选择图片后再鉴别");
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

            dismissProDialog();

            handleResult(result);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
//            mCamera.startPreview();
        }

        @Override
        public void onError(SpeechError error) {
            dismissProDialog();

            LogToastUtils.toastShort(context, error.getPlainDescription(true));
            mTts.startSpeaking("没有检测到人脸", null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCamera.startPreview();
                }
            }, 3000);
        }

    };

    private void dismissProDialog() {
        if (null != mProDialog) {
            mProDialog.dismiss();
        }
    }


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

                LogToastUtils.toastShort(context, userIdentify.getIfv_result().getCandidates().get(0).getUser() + "您已经上班");
                onIdentifyListener.onSuccess(userIdentify.getIfv_result().getCandidates().get(0).getUser());
                mTts.startSpeaking(userIdentify.getIfv_result().getCandidates().get(0).getUser() + "，早上好！新的一天开始了，好好工作哦！", null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCamera.startPreview();
                    }
                }, 3000);

            } else {
                LogToastUtils.toastShort(context, "不存在此人，是否跳转到互动模式");
                mTts.startSpeaking("不存在此人，是否跳转到互动模式", null);
                onIdentifyListener.onSwitch();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCamera.startPreview();
                    }
                }, 3000);
            }
//                LogToastUtils.toastShort(context, resultStr);
//                LogToastUtils.log(TAG, resultStr);

        } else {
            LogToastUtils.toastShort(context, "识别失败！");
            onIdentifyListener.onError();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCamera.startPreview();
                }
            }, 3000);
        }

    }

    public void finisheIdentify() {
        if (null != mIdVerifier) {
            mIdVerifier.destroy();
            mIdVerifier = null;
        }
    }

    //快门按下的时候onShutter()被回调
    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            if (tone == null)
                //发出提示用户的声音
                tone = new ToneGenerator(AudioManager.STREAM_MUSIC,
                        ToneGenerator.MAX_VOLUME);
            tone.startTone(ToneGenerator.TONE_PROP_BEEP2);
        }
    };

    /**
     * 因为对摄像头进行了旋转，所以同时也旋转画板矩阵
     * 详细请查看{@link Camera.Face#rect}
     *
     * @return
     */
    private Matrix updateFaceRect() {
        Matrix matrix = new Matrix();
        Camera.CameraInfo info = new Camera.CameraInfo();
        // Need mirror for front camera.
//        boolean mirror = (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
//        matrix.setScale(mirror ? -1 : 1, 1);
        matrix.setScale(-1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(0);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        if (mPreview != null) {
            matrix.postScale(mPreview.getWidth() / 2000f, mPreview.getHeight() / 2000f);
            matrix.postTranslate(mPreview.getWidth() / 2f, mPreview.getHeight() / 2f);
        }

        return matrix;
    }

    public interface OnIdentifyListener {
        void onSuccess(String user_id);

        void onSwitch();

        void onError();
    }
}
