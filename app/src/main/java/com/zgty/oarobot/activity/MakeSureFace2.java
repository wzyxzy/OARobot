package com.zgty.oarobot.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.zgty.oarobot.R;
import com.zgty.oarobot.camera.CameraSourcePreview;
import com.zgty.oarobot.camera.GraphicOverlay;
import com.zgty.oarobot.common.CommonSActivity;
import com.zgty.oarobot.util.CameraSource2;
import com.zgty.oarobot.util.FileUtils;
import com.zgty.oarobot.util.IdentifyFace;
import com.zgty.oarobot.util.IdentifyFace2;
import com.zgty.oarobot.util.LogToastUtils;

import java.io.IOException;

import static com.zgty.oarobot.common.Constant.MAIN_CHECK_CAMERA_TYPE;
import static com.zgty.oarobot.common.Constant.MAIN_RECORD_CAMERA_TYPE;
import static com.zgty.oarobot.common.OARobotApplication.mTts;

public class MakeSureFace2 extends CommonSActivity implements View.OnClickListener {

    private static final String TAG = MakeSureFace2.class.getSimpleName();
    private TextView edit_cancel;
    private TextView edit_sure;
    private LinearLayout edit_sure_cancel;
    private String staff_id;
    private IdentifyFace2 identifyFace;
    private CameraSource2 mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://识别成功
                    edit_sure_cancel.setVisibility(View.VISIBLE);
                    break;
                case 1://重录
                    edit_sure_cancel.setVisibility(View.INVISIBLE);
                    if (mCameraSource != null) {
                        mCameraSource.release();
                    }
                    createCameraSource();
                    startCameraSource();

//                    new MakeSureFace2.GraphicFaceTrackerFactory();\isFirst
//                    isFirst = true;
                    break;
                case 2://上传
                    identifyFace.addStaff(staff_id);

                    break;
            }

        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_sure_face2);
        initView();
        initData();
        createCameraSource();
    }

    private void initData() {
        staff_id = getIntent().getStringExtra("staff_id");
    }

    private void initView() {
        mGraphicOverlay = findViewById(R.id.faceOverlay);
        mPreview = findViewById(R.id.preview);
        edit_cancel = findViewById(R.id.edit_cancel);
        edit_sure = findViewById(R.id.edit_sure);
        edit_sure_cancel = findViewById(R.id.edit_sure_cancel);
        edit_cancel.setOnClickListener(this);
        edit_sure.setOnClickListener(this);
    }


    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setProminentFaceOnly(true)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new MakeSureFace2.GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource2.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource2.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.finish();
        return true;
    }


    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
        if (identifyFace == null) {
            identifyFace = new IdentifyFace2(this, MAIN_RECORD_CAMERA_TYPE);
        }
        identifyFace.setOnIdentifyListener(new IdentifyFace2.OnIdentifyListener() {
            @Override
            public void onSuccess(String user_id, byte[] b) {
            }

            @Override
            public void onSwitch(byte[] b) {
            }

            @Override
            public void onError() {
                LogToastUtils.toastShort(getApplicationContext(), "没有检测到人脸!");
            }

            @Override
            public void onCapture() {
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onRegisterSuccess() {
                LogToastUtils.toastShort(getApplicationContext(), "录入成功!");

                setResult(22);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_cancel:
                handler.sendEmptyMessage(1);
                break;
            case R.id.edit_sure:
                handler.sendEmptyMessage(2);
                break;
        }
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new MakeSureFace2.GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;
        private boolean isFirst;
        private float lastSmile;
        private float firstSmile;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay, MakeSureFace2.this);


        }


        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
            isFirst = true;
//            mFaceGraphic.setIsFirst(isFirst);
//            if (item.getIsSmilingProbability() > 0) {
//                firstSmile = item.getIsSmilingProbability();
//            } else {
//                firstSmile = 0;
//            }
            firstSmile = item.getIsSmilingProbability();
            mFaceGraphic.setIsFirst(isFirst);

//            mSpeech.speak("请您微笑", TextToSpeech.QUEUE_FLUSH, null);
//            mSpeech.speak("please smile", TextToSpeech.QUEUE_FLUSH, null);

//            @SuppressLint("HandlerLeak")
//            Handler handler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    switch (msg.what){
//                        case 0:
//
//                            break;
//                    }
//                }
//            };
//            handler.sendEmptyMessage(0);


        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
            Log.e("test", "test---------------" + detectionResults.detectorIsOperational());
            Log.e("test", "test---------------" + face.getIsSmilingProbability());
            lastSmile = face.getIsSmilingProbability();
            if (firstSmile <= 0) {
                firstSmile = lastSmile;
            }
            if (lastSmile <= 0) {
                return;
            }
            if (lastSmile > 0 && lastSmile < firstSmile) {
                firstSmile = lastSmile;
            }
            if (isFirst && lastSmile - firstSmile >= 0.6) {
                Log.e("smile", "lastSmile is " + lastSmile + ", firstSmile is " + firstSmile);
                mCameraSource.takePicture(new CameraSource2.ShutterCallback() {
                    @Override
                    public void onShutter() {

                    }
                }, new CameraSource2.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        if (bytes != null) {

                            identifyFace.setData(bytes);
//                            FileUtils.getFileFromBytes(bytes);
//                            mTts.startSpeaking("打卡成功", null);
//                            mSpeech.speak("success!", TextToSpeech.QUEUE_FLUSH, null);
                            isFirst = false;
                            mFaceGraphic.setIsFirst(isFirst);
                            mPreview.stop();

                        }
                    }
                });

            }
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }
}
