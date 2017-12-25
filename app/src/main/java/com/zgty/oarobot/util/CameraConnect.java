package com.zgty.oarobot.util;

import android.content.Context;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.iflytek.cloud.FaceDetector;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.util.Accelerometer;
import com.zgty.oarobot.widget.DrawFacesView;

/**
 * Created by zy on 2017/12/21.
 */

public class CameraConnect {
    private Context context;
    private FrameLayout camera_preview;
    private final static String TAG = CameraConnect.class.getSimpleName();
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private SurfaceView mPreview;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    // Camera nv21格式预览帧的尺寸，默认设置640*480
    private int PREVIEW_WIDTH = 640;
    private int PREVIEW_HEIGHT = 480;

    public CameraConnect(Context context, FrameLayout camera_preview) {
        this.context = context;
        this.camera_preview = camera_preview;

    }

    private void initCamera() {
        if (Camera.getNumberOfCameras() == 1) {
            mCameraId = 0;
        }

    }

    public void openCamera() {
        initCamera();
        mPreview = new SurfaceView(context);
        camera_preview.addView(mPreview);

    }

}
