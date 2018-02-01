package com.zgty.oarobot.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SynthesizerListener;
import com.zgty.oarobot.R;
import com.zgty.oarobot.bean.Account;
import com.zgty.oarobot.bean.Speaking;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.bean.Time;
import com.zgty.oarobot.bean.Visitor;
import com.zgty.oarobot.camera.CameraSourcePreview;
import com.zgty.oarobot.camera.GraphicOverlay;
import com.zgty.oarobot.common.CommonActivity;
import com.zgty.oarobot.common.Constant;
import com.zgty.oarobot.dao.AccountDaoUtils;
import com.zgty.oarobot.dao.SpeekDaoUtils;
import com.zgty.oarobot.dao.StaffDaoUtils;
import com.zgty.oarobot.dao.TimeDaoUtils;
import com.zgty.oarobot.dao.VisitorDaoUtils;
import com.zgty.oarobot.dao.WorkOnOffDaoUtils;
import com.zgty.oarobot.receiver.DateTimeReceiver;
import com.zgty.oarobot.service.RefreshService;
import com.zgty.oarobot.util.FileUtil;
import com.zgty.oarobot.util.FileUtils;
import com.zgty.oarobot.util.IdentifyFace2;
import com.zgty.oarobot.util.JsonParser;
import com.zgty.oarobot.util.LogToastUtils;
import com.zgty.oarobot.util.WXCPUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.zgty.oarobot.common.Constant.MAIN_CHECK_CAMERA_TYPE;
import static com.zgty.oarobot.common.OARobotApplication.canUserGoogleTTS;
import static com.zgty.oarobot.common.OARobotApplication.isNeedId;
import static com.zgty.oarobot.common.OARobotApplication.mSpeech;
import static com.zgty.oarobot.common.OARobotApplication.mTts;

public class MainActivityN extends CommonActivity implements View.OnClickListener {

    private TextView change_mode;
    private TextView name_staff;
    private TextView id_staff;
    private TextView sign_up_time;
    private TextView station_state;
    private TextView waiting_text;
    private TextView robot_state_text;


    private TextView name_part;
    private TextView robot_speak_text;
    private TextView setting_main;
    private IdentifyFace2 identifyFace;  //识别工具，待分离
    private String userid;//用户ID
    private String userid1;//用户ID,用来联系
    private String username;//用户name

    //时间
    private double timeon;
    private double timeonlate;
    private double timeoffearly;
    private double timeoff;
    private double timeadd;
    private double timenow;
    // 语音听写对象
    private SpeechRecognizer mIat;//待归类
    private int noanswer = 0;
    private int time_second;

    private static final String TAG = MainActivityN.class.getSimpleName();

    private SpeekDaoUtils speekDaoUtils;
    private File file;
    private Intent intentRefreshService;
    private RefreshListBroadCast listBroadCast;
    private WXCPUtils wxcpUtils;
    private boolean canConnect = true;
    private Handler handler1;
    private Runnable runnable;
    private int hearingType;
    private boolean isFirstConnect;


    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private RatingBar rb_normal;
    private SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);

    private static final int RC_HANDLE_GMS = 9001;
    private static final int REQUEST_CODE_CAMERA = 102;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    //全部机器人说的话都写到异步中
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://识别成功
                    initData();
                    break;
                case 1:
//                    robotSpeek(speekDaoUtils.querySpeekingText("cannotRecognise"), 1);
                    soundPool.play(8,1, 1, 0, 0, 1);
                    hearingType = 0;
                    isFirstConnect = true;
                    break;
                case 2:
                    mIat = SpeechRecognizer.createRecognizer(getApplicationContext(), mInitListener);
                    initLoadSound();
                    break;
                case 3:
                    List<Visitor> visitors = new VisitorDaoUtils(getApplicationContext()).queryVisitorList(userid);
                    if (visitors != null && visitors.size() > 0) {
                        Visitor visitor = visitors.get(0);
                        String id = visitor.getVisit_id();
                        userid1 = id;
                        isFirstConnect = false;
                        Staff staff = new StaffDaoUtils(getApplicationContext()).queryStaffList(id).get(0);
                        username = staff.getName_user();
                        robotSpeek(String.format("您是否联系%s?", staff.getName_user()), 1);
                        hearingType = 1;
                    }
                    break;
            }

        }
    };

    private void initLoadSound() {
        soundPool.load(this,R.raw.oarobot1,1);
        soundPool.load(this,R.raw.oarobot2,1);
        soundPool.load(this,R.raw.oarobot3,1);
        soundPool.load(this,R.raw.oarobot4,1);
        soundPool.load(this,R.raw.oarobot5,1);
        soundPool.load(this,R.raw.oarobot6,1);
        soundPool.load(this,R.raw.oarobot7,1);
        soundPool.load(this,R.raw.oarobot8,1);
        soundPool.load(this,R.raw.oarobot9,1);
        soundPool.load(this,R.raw.oarobot10,1);
        soundPool.load(this,R.raw.oarobot11,1);
        soundPool.load(this,R.raw.oarobot12,1);
        soundPool.load(this,R.raw.oarobot13,1);
        soundPool.load(this,R.raw.oarobot14,1);
        soundPool.load(this,R.raw.oarobot15,1);
        soundPool.load(this,R.raw.oarobot16,1);
        soundPool.load(this,R.raw.oarobot17,1);
        soundPool.load(this,R.raw.oarobot18,1);
        soundPool.load(this,R.raw.oarobot19,1);
        soundPool.load(this,R.raw.oarobot20,1);
        soundPool.load(this,R.raw.oarobot21,1);
        soundPool.load(this,R.raw.oarobot22,1);
        soundPool.load(this,R.raw.oarobot23,1);
        soundPool.load(this,R.raw.oarobot24,1);
        soundPool.load(this,R.raw.oarobot25,1);
        soundPool.load(this,R.raw.oarobot26,1);
        soundPool.load(this,R.raw.oarobot27,1);
        soundPool.load(this,R.raw.oarobot28,1);
        soundPool.load(this,R.raw.oarobot29,1);
        soundPool.load(this,R.raw.oarobot30,1);
        soundPool.load(this,R.raw.oarobot31,1);
        soundPool.load(this,R.raw.oarobot32,1);
        soundPool.load(this,R.raw.oarobot33,1);
    }


    /**
     * 合成回调监听。播放完成后进行聆听，适用于请问您找谁？
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {

        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
                startHearing();
            } else {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };


    private void startHearing() {
        robot_speak_text.setTextColor(getResources().getColor(R.color.greenText));
        robot_state_text.setText("请开始说话");
        robot_state_text.setTextColor(getResources().getColor(R.color.greenText));
        // 不显示听写对话框
        int ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            showTip("听写失败,错误码：" + ret);
        } else {
            showTip("请开始说话");
        }
    }

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogToastUtils.log(TAG, str);

            }
        });
    }


    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
            robot_state_text.setText("正在聆听,请说话>>>");
//            robot_state_text.setTextColor(getResources().getColor(R.color.greenText));
            robot_speak_text.setText(null);

        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
//            if (mTranslateEnable && error.getErrorCode() == 14002) {
//                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
//            } else {
            if (error.getErrorCode() == 10118) {
                checkYourSpeech(1);
            }
            showTip(error.getPlainDescription(true));
//            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
            robot_state_text.setText("聆听结束");
            robot_state_text.setTextColor(getResources().getColor(R.color.grey4Text));
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
//            if (mTranslateEnable) {
//                printTransResult(results);
//            } else {

            String text = JsonParser.parseIatResult(results.getResultString());
            robot_speak_text.append(text);
//            }

            if (isLast) {
                //TODO 最后的结果
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkYourSpeech(0);
                    }
                }, 2000);

            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void checkYourSpeech(int i) {
//        robot_speak_text.setTextColor(getResources().getColor(R.color.colorAccent));
        switch (i) {
            case 0:
                noanswer = 0;
                String text = String.valueOf(robot_speak_text.getText());
                List<Staff> staffList = new StaffDaoUtils(this).queryStaffList();
                boolean hasPerson = false;
                if (text.contains("打卡")) {
                    robotSpeek("现在已经可以打卡了!", 0);
                    return;
                }
                if (hearingType == 1) {
                    hasPerson = true;
                    if (text.contains("是")) {
                        if (canConnect) {
                            robotSpeek(String.format(speekDaoUtils.querySpeekingText("connectForYou"), username), 2);
//                            userid1 = userid;
                        } else {
                            robotSpeek("前面已有联系任务，您还需等待" + time_second + "秒", 0);
                        }
                    } else {
                        robotSpeek("请说出您要找的人的名字", 1);
                        hearingType = 0;
                    }

                } else {
                    for (int i1 = 0; i1 < staffList.size(); i1++) {
                        if (text.contains(staffList.get(i1).getName_user())) {

                            if (canConnect) {
                                username = staffList.get(i1).getName_user();

                                if (isNeedId) {
                                    robotSpeek("请您出示身份证！", 0);
                                    takeIdCard();
                                } else {
                                    robotSpeek(String.format(speekDaoUtils.querySpeekingText("connectForYou"), staffList.get(i1).getName_user()), 2);
                                }

                                userid1 = staffList.get(i1).getId();

                            } else {
                                robotSpeek("前面已有联系任务，您还需等待" + time_second + "秒", 0);
                            }
                            hasPerson = true;
                            break;
                        }
                    }
                }

                if (!hasPerson) {
                    robotSpeek(speekDaoUtils.querySpeekingText("connectByYourself"), 0);
                }
                break;
            case 1:
                noanswer++;
                if (noanswer < 3) {
                    robotSpeek(speekDaoUtils.querySpeekingText("cannotHearingClear"), 1);
                } else {
//                    identifyFace.startCameraView();
                }

                break;
        }
    }

    private void robotSpeek(String s, int type) {
        switch (type) {
            case 0:
                if (canUserGoogleTTS) {
                    mSpeech.speak(s, TextToSpeech.QUEUE_ADD, null);
                } else {
                    mTts.startSpeaking(s, null);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        robot_speak_text.setText("");

                    }
                }, 5000);
                break;
            case 1:
                //mTtsListener
                mTts.startSpeaking(s, mTtsListener);

                break;
            case 2:
                //微信联系
                mTts.startSpeaking(s, new SynthesizerListener() {
                    @Override
                    public void onSpeakBegin() {

                    }

                    @Override
                    public void onBufferProgress(int i, int i1, int i2, String s) {

                    }

                    @Override
                    public void onSpeakPaused() {

                    }

                    @Override
                    public void onSpeakResumed() {

                    }

                    @Override
                    public void onSpeakProgress(int i, int i1, int i2) {

                    }

                    @Override
                    public void onCompleted(SpeechError speechError) {
                        if (speechError == null) {
                            showTip("播放完成");
//                            WeiXinUtils weiXinUtils = new WeiXinUtils(getApplicationContext());
//                            weiXinUtils.SendText("前台有人找您，他的照片发给您");
                            VisitorDaoUtils visitorDaoUtils = new VisitorDaoUtils(getApplicationContext());
                            if (isFirstConnect) {

                                String visitorId = "visitor" + visitorDaoUtils.findVisitorNum();
                                identifyFace.addStaff(visitorId);
                                Visitor visitor = new Visitor();
                                visitor.setId(visitorId);
                                visitor.setTime(getNowDate());
                                visitor.setVisit_id(userid1);
                                visitor.setInfos("");
                                visitorDaoUtils.insertVisitor(visitor);
                            } else {
                                Visitor visitor = new Visitor();
                                visitor.setId(userid);
                                visitor.setTime(getNowDate());
                                visitor.setVisit_id(userid1);
                                visitor.setInfos("");
                                visitorDaoUtils.updateVisitor(visitor);
                            }
                            wxcpUtils.sendText(file, userid1, "前台有人找您，您是否同意让他进来？", "image");
                            wxcpUtils.setOnWXCPUtilsListener(new WXCPUtils.OnWXCPUtilsListener() {
                                @Override
                                public void onSuccess() {
                                    successConnect();
                                }

                                @Override
                                public void onError() {
                                    robotSpeek("不好意思，没有为您通知成功!", 0);
                                }
                            });

                        } else {
                            showTip(speechError.getPlainDescription(true));
                        }
                    }

                    @Override
                    public void onEvent(int i, int i1, int i2, Bundle bundle) {

                    }
                });

                break;
            case 3:
                //微信联系人事
                mTts.startSpeaking(s, new SynthesizerListener() {
                    @Override
                    public void onSpeakBegin() {

                    }

                    @Override
                    public void onBufferProgress(int i, int i1, int i2, String s) {

                    }

                    @Override
                    public void onSpeakPaused() {

                    }

                    @Override
                    public void onSpeakResumed() {

                    }

                    @Override
                    public void onSpeakProgress(int i, int i1, int i2) {

                    }

                    @Override
                    public void onCompleted(SpeechError speechError) {
                        if (speechError == null) {
                            showTip("播放完成");
//                            WeiXinUtils weiXinUtils = new WeiXinUtils(getApplicationContext());
//                            weiXinUtils.SendText("前台有人找您，他的照片发给您");
                            userid1 = "wuzhiying16";
                            wxcpUtils.sendText(null, userid1, "离职人员" + username + "，正在前台等候，是否同意让他进来？", "text_out");
                            wxcpUtils.setOnWXCPUtilsListener(new WXCPUtils.OnWXCPUtilsListener() {
                                @Override
                                public void onSuccess() {
                                    successConnect();
                                }

                                @Override
                                public void onError() {
                                    robotSpeek("不好意思，没有为您通知成功!", 0);
                                }
                            });

                        } else {
                            showTip(speechError.getPlainDescription(true));
                        }
                    }

                    @Override
                    public void onEvent(int i, int i1, int i2, Bundle bundle) {

                    }
                });
                break;
        }
        robot_speak_text.setTextColor(getResources().getColor(R.color.colorAccent));
        robot_speak_text.setText(s);

    }

    private void successConnect() {
        robotSpeek("已经为您通知，请等候!", 0);
        intentRefreshService = new Intent();
        intentRefreshService.setClass(getApplicationContext(), RefreshService.class);
        intentRefreshService.putExtra("userid", userid1);
        startService(intentRefreshService);
        listBroadCast = new RefreshListBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BROADCASTACTION);
        registerReceiver(listBroadCast, filter);
        handler1 = new Handler();
        time_second = 60;
        runnable = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //要做的事情

                waiting_text.setVisibility(View.VISIBLE);
                canConnect = false;
                waiting_text.setText("正在为您联系中，剩余" + time_second-- + "秒");
                handler1.postDelayed(this, 1000);
            }
        };
        handler1.postDelayed(runnable, 1000);//每两秒执行一次runnable.
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                handler1.removeCallbacks(runnable);
//                waiting_text.setVisibility(View.GONE);
//                canConnect = true;
//            }
//        }, 60000);

    }

    /**
     * 初始化上下班排班表
     */
    private void initTime() {
        TimeDaoUtils timeDaoUtils = new TimeDaoUtils(this);
        List<Time> times = timeDaoUtils.queryTimeList();
        if (times == null || times.size() == 0) {
            times = new ArrayList<>();
            Time time1 = new Time("time_on", "上班时间", "9:00");
            Time time2 = new Time("time_off", "下班时间", "17:00");
            Time time3 = new Time("late_min", "迟到区间", "30");
            Time time4 = new Time("early_min", "早退区间", "30");
            Time time5 = new Time("time_add", "加班时间", "19:00");
            times.add(time1);
            times.add(time2);
            times.add(time3);
            times.add(time4);
            times.add(time5);
            timeDaoUtils.insertTimeList(times);
        }
        timeon = changeTimeToDouble(timeDaoUtils.queryTimeList("time_on").get(0).getTime());
        timeonlate = timeon + changeTimeToDouble(timeDaoUtils.queryTimeList("late_min").get(0).getTime());
        timeoff = changeTimeToDouble(timeDaoUtils.queryTimeList("time_off").get(0).getTime());
        timeoffearly = timeoff - changeTimeToDouble(timeDaoUtils.queryTimeList("early_min").get(0).getTime());
        timeadd = changeTimeToDouble(timeDaoUtils.queryTimeList("time_add").get(0).getTime());
    }

    private double changeTimeToDouble(String time_on) {
        double d = 0.0;
        if (time_on.contains(":")) {
            String[] split = time_on.split(":");
            d = Double.valueOf(split[0]) + Double.valueOf(split[1]) / 60;
        } else {
            d = Double.valueOf(time_on) / 60;
        }
        return d;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_n);
        initView();
        createCameraSource();
        requestPermissions();

        initAlarm();
        handler.sendEmptyMessage(2);
        wxcpUtils = new WXCPUtils(this);
    }


    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setProminentFaceOnly(true)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
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

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();
    }

    /**
     * 闹钟事件，每月1号生成一个cvs
     */
    private void initAlarm() {
        final int INTERVAL = 1000 * 60 * 60 * 24;//每天检查一次
        Intent intent = new Intent(this, DateTimeReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, sender);

    }


    @Override
    protected void onResume() {
        super.onResume();
        initInfo();
        initTime();
        initAcoount();
        startCameraSource();

        if (identifyFace == null) {
            identifyFace = new IdentifyFace2(this, MAIN_CHECK_CAMERA_TYPE);
            identifyFace.setOnIdentifyListener(new IdentifyFace2.OnIdentifyListener() {
                @Override
                public void onSuccess(String user_id, byte[] b) {
                    if (b != null) {
                        file = FileUtils.getFileFromBytes(b);
                        handler.sendEmptyMessage(3);
                        userid = user_id;
                    } else {
                        userid = user_id;
                        handler.sendEmptyMessage(0);
                    }

                }

                @Override
                public void onSwitch(byte[] b) {
                    file = FileUtils.getFileFromBytes(b);
                    handler.sendEmptyMessage(1);
                }

                @Override
                public void onError() {

                }

                @Override
                public void onCapture() {

                }

                @Override
                public void onRegisterSuccess() {

                }
            });
        }

//        if (identifyFace == null) {
//            identifyFace = new IdentifyFace(camera_preview, this, MAIN_CHECK_CAMERA_TYPE, this);
//            identifyFace.openSurfaceView();
//        }
//        identifyFace.setOnIdentifyListener(new IdentifyFace.OnIdentifyListener() {
//            @Override
//            public void onSuccess(String user_id) {
//                LogToastUtils.log(TAG, "success");
//                handler.sendEmptyMessage(0);
//                userid = user_id;
////                onResume();
//            }
//
//            @Override
//            public void onSwitch(byte[] data) {
//                file = FileUtils.getFileFromBytes(data);
//                LogToastUtils.log(TAG, "switch");
//                handler.sendEmptyMessage(1);
//            }
//
//            @Override
//            public void onError() {
//                LogToastUtils.log(TAG, "error");
//            }
//
//            @Override
//            public void onCapture() {
//
//            }
//
//            @Override
//            public void onRegisterSuccess() {
//
//            }
//        });

    }


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

    private void clearText() {
        name_staff.setText("");
        id_staff.setText("");
        name_part.setText("");
        sign_up_time.setText("");
        station_state.setText("");
    }

    private void initInfo() {
        clearText();
        speekDaoUtils = new SpeekDaoUtils(this);
        List<Speaking> speakings = speekDaoUtils.querySpeekList();
        if (speakings == null || speakings.size() == 0) {
            speakings = new ArrayList<>();
            Speaking speaking1 = new Speaking("welcomeText", "打开软件欢迎语", "欢迎使用中广通业考勤接待系统");
            Speaking speaking2 = new Speaking("timeOnNormal", "正常上班", "%s，早上好，新的一天开始了，祝您工作好心情！");
            Speaking speaking3 = new Speaking("timeOnLate", "上班迟到", "%s，您今天迟到了，相信您以后不会再迟到了！");
            Speaking speaking4 = new Speaking("goOutNormal", "中途外出", "%s，请您通过！");
            Speaking speaking5 = new Speaking("timeOffEarly", "下班早退", "%s，还没有下班呢，不能早退哦！");
            Speaking speaking6 = new Speaking("timeOffNormal", "正常下班", "%s，工作辛苦了，下班后让自己放松下！");
            Speaking speaking7 = new Speaking("timeAddNormal", "正常加班", "%s，您辛苦了，都加班这么晚了！");
            Speaking speaking8 = new Speaking("cannotRecognise", "陌生人", "我还不认识您，请问您找谁?");
            Speaking speaking9 = new Speaking("cannotHearingClear", "没有听清", "我没有听清您说的话，请问您找谁？");
            Speaking speaking10 = new Speaking("connectForYou", "开始联系", "正在为您联系%s,请稍后！");
            Speaking speaking11 = new Speaking("connectedSuccess", "联系成功", "请到%s！");
            Speaking speaking12 = new Speaking("connectByYourself", "查无此人", "没有找到您所找的人，请您自行联系");
            Speaking speaking13 = new Speaking("connectFailed", "没有应答", "对方没有应答，请您自行联系");
            Speaking speaking14 = new Speaking("getOffStaff", "离职人员", "%s，您已离职，正在为您联系人事！");
            speakings.add(speaking1);
            speakings.add(speaking2);
            speakings.add(speaking3);
            speakings.add(speaking4);
            speakings.add(speaking5);
            speakings.add(speaking6);
            speakings.add(speaking7);
            speakings.add(speaking8);
            speakings.add(speaking9);
            speakings.add(speaking10);
            speakings.add(speaking11);
            speakings.add(speaking12);
            speakings.add(speaking13);
            speakings.add(speaking14);

            speekDaoUtils.insertSpeekList(speakings);
        }
    }

    private void initAcoount() {
        AccountDaoUtils accountDaoUtils = new AccountDaoUtils(this);
        if (accountDaoUtils.queryAccountSize() == 0) {
            Account account = new Account("admin", "zgtyadmin");
            accountDaoUtils.insertAccountList(account);
        }
    }


    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        handler.sendEmptyMessage(2);
    }

    private void initView() {
        change_mode = findViewById(R.id.change_mode);
        name_staff = findViewById(R.id.name_staff);
        id_staff = findViewById(R.id.id_staff);
        sign_up_time = findViewById(R.id.sign_up_time);
        station_state = findViewById(R.id.station_state);


        change_mode.setOnClickListener(this);
        name_part = findViewById(R.id.name_part);
        robot_speak_text = findViewById(R.id.robot_speak_text);
        setting_main = findViewById(R.id.setting_main);
        setting_main.setOnClickListener(this);

        waiting_text = findViewById(R.id.waiting_text);

        mGraphicOverlay = findViewById(R.id.faceOverlay);
        mPreview = findViewById(R.id.preview);
        rb_normal = findViewById(R.id.rb_normal);

        robot_state_text = findViewById(R.id.robot_state_text);
    }

    //识别完毕后，数据加载，写在异步线程
    private void initData() {
//        userid = "10003";
        List<Staff> staffList = new StaffDaoUtils(this).queryStaffList(userid);
        if (staffList != null && staffList.size() > 0) {
            Staff staff = staffList.get(0);
            username = staff.getName_user();
            if (staff.getUser_type().equals("1")) {
                if (canConnect) {
                    robotSpeek(String.format(speekDaoUtils.querySpeekingText("getOffStaff"), username), 3);
                } else {
                    robotSpeek(username + "，您已离职，但前面已有联系任务，您还需等待" + time_second + "秒", 0);
                }

                return;
            }
            name_staff.setText(username);
            id_staff.setText(staff.getId_clerk());
            name_part.setText(staff.getName_part());
            String nowtime = getNowTime();
            timenow = changeTimeToDouble(nowtime);
            sign_up_time.setText(nowtime);
            String type = getType(staff);
            station_state.setText(type);
            WorkOnOffDaoUtils workOnOffDaoUtils = new WorkOnOffDaoUtils(this);
            if (timenow <= 12) {
                workOnOffDaoUtils.updateWorkOn(staff.getId(), nowtime);
            } else {
                workOnOffDaoUtils.updateWorkOff(staff.getId(), nowtime);
            }
            robot_state_text.setText("打卡成功");
//            if (!type.equalsIgnoreCase("中途外出")) {
//                wxcpUtils.sendText(null, userid, "姓名：" + staff.getName_user() + "\n工号：" + staff.getId_clerk() + "\n部门：" + staff.getName_part() + "\n打卡时间：" + nowtime + "\n打卡类型：" + type);
//            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearText();
                    robot_speak_text.setText("");

                }
            }, 5000);

        } else {
            LogToastUtils.toastShort(this, "没有录入该信息");
        }

    }

    //获取打卡类型
    private String getType(Staff staff) {
        String type = "";

        if (timenow >= timeadd) {
            type = "加班";
//            robotSpeek(String.format(speekDaoUtils.querySpeekingText("timeAddNormal"), staff.getName_user()), 0);
            soundPool.play(7,1, 1, 0, 0, 1);
        } else if (timenow >= timeoff) {
            type = "正常下班";
//            robotSpeek(String.format(speekDaoUtils.querySpeekingText("timeOffNormal"), staff.getName_user()), 0);
            soundPool.play(6,1, 1, 0, 0, 1);
        } else if (timenow > timeoffearly) {
            type = "早退";
//            robotSpeek(String.format(speekDaoUtils.querySpeekingText("timeOffEarly"), staff.getName_user()), 0);
            soundPool.play(5,1, 1, 0, 0, 1);
        } else if (timenow > timeonlate) {
            type = "中途外出";
//            robotSpeek(String.format(speekDaoUtils.querySpeekingText("goOutNormal"), staff.getName_user()), 0);
            soundPool.play(4,1, 1, 0, 0, 1);
        } else if (timenow > timeon) {
            type = "迟到";
//            robotSpeek(String.format(speekDaoUtils.querySpeekingText("timeOnLate"), staff.getName_user()), 0);
            soundPool.play(3,1, 1, 0, 0, 1);
        } else {
            type = "正常上班";
//            robotSpeek(String.format(speekDaoUtils.querySpeekingText("timeOnNormal"), staff.getName_user()), 0);
            soundPool.play(2,1, 1, 0, 0, 1);
        }


        return type;
    }

    //获取当前时间
    private String getNowTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    //获取当前日期时间
    private String getNowDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.change_mode:
                intent = new Intent(this, ChatActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_main:
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            } else {
                robot_state_text.setText("准备就绪");
            }
        }
    };


    /**
     * 应用挂起时，停止语音合成与识别
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
        if (mTts != null) {
            mTts.stopSpeaking();
        }
        if (mIat != null) {
            mIat.stopListening();

        }
    }

    /**
     * 销毁应用，同时销毁语音合成，识别，服务，广播
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
        identifyFace.finisheIdentify();
        identifyFace = null;
        if (mTts != null) {
            mTts.destroy();
        }
        if (mIat != null) {
            mIat.destroy();

        }
        if (intentRefreshService != null) {
            stopService(intentRefreshService);
            intentRefreshService = null;
        }
        if (listBroadCast != null) {
            unregisterReceiver(listBroadCast);
            listBroadCast = null;
        }
    }


    /**
     * 接收广播，等待联系人回复。
     */
    private class RefreshListBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intentRefreshService != null) {

                stopService(intentRefreshService);
                intentRefreshService = null;
            }
            if (listBroadCast != null) {
                unregisterReceiver(listBroadCast);
                listBroadCast = null;
            }
            handler1.removeCallbacks(runnable);
            waiting_text.setVisibility(View.GONE);
            canConnect = true;
            String result = intent.getStringExtra("result");
            switch (result) {
                case "worktable":
                    robotSpeek("对方已经接受了您的请求，请您直接去他的工位或办公室!", 0);
                    wxcpUtils.sendText(null, userid1, "已经让对方通过！", "text");
                    break;
                case "combig":
                    robotSpeek("对方已经接受了您的请求，请您到大会议室等候!", 0);
                    wxcpUtils.sendText(null, userid1, "已经让对方通过！", "text");
                    break;
                case "comsmall":
                    robotSpeek("对方已经接受了您的请求，请您到小会议室等候!", 0);
                    wxcpUtils.sendText(null, userid1, "已经让对方通过！", "text");
                    break;
                case "talk1":
                    robotSpeek("对方已经接受了您的请求，请您到洽谈室1等候!", 0);
                    wxcpUtils.sendText(null, userid1, "已经让对方通过！", "text");
                    break;
                case "talk2":
                    robotSpeek("对方已经接受了您的请求，请您到洽谈室2等候!", 0);
                    wxcpUtils.sendText(null, userid1, "已经让对方通过！", "text");
                    break;
                case "wangxiaodi04":
                    robotSpeek("对方不方便与您联系，已将您转接至我公司行政!", 2);
                    wxcpUtils.sendText(null, userid1, "已经为对方转接至行政王晓迪！", "text");
                    userid1 = "wangxiaodi04";
                    break;
                case "yuantong05":
                    robotSpeek("对方不方便与您联系，已将您转接至我公司行政!", 2);
                    wxcpUtils.sendText(null, userid1, "已经为对方转接至行政袁彤！", "text");
                    userid1 = "yuantong05";
                    break;
                case "chenjingyi29":
                    robotSpeek("对方不方便与您联系，已将您转接至我公司人事!", 2);
                    wxcpUtils.sendText(null, userid1, "已经为对方转接至人事陈静怡！", "text");
                    userid1 = "chenjingyi29";
                    break;
                case "weixin12":
                    robotSpeek("对方不方便与您联系，已将您转接至我公司开发!", 2);
                    wxcpUtils.sendText(null, userid1, "已经为对方转接至开发魏鑫", "text");
                    userid1 = "weixin12";
                    break;
                case "wuzhiying16":
                    robotSpeek("对方不方便与您联系，已将您转接至我公司开发!", 2);
                    wxcpUtils.sendText(null, userid1, "已经为对方转接至开发巫志英！", "text");
                    userid1 = "wuzhiying16";
                    break;
                case "reject":
                case "other":
                    robotSpeek("对方已经拒绝了您的请求，请您自行联系!", 0);
                    wxcpUtils.sendText(null, userid1, "已经拒绝对方！", "text");
                    break;
                case "waiting":
                    robotSpeek("对方暂时不方便与您联系，请您稍等几分钟后再试!", 0);
                    wxcpUtils.sendText(null, userid1, "已经拒绝对方，并提示对方等待！", "text");
                    break;
                case "changing":
                    robotSpeek("对方暂时无法与您联系，请您修改预约时间!", 0);
                    wxcpUtils.sendText(null, userid1, "已经拒绝对方,并提示改预约时间！", "text");
                    break;
                case "telephone":
                    robotSpeek("对方希望您通过电话联系!电话号码是：" + new StaffDaoUtils(getApplicationContext()).queryStaffList(userid1).get(0).getCall_num(), 0);
                    robot_state_text.setText("电话号码是：" + new StaffDaoUtils(getApplicationContext()).queryStaffList(userid1).get(0).getCall_num());
                    wxcpUtils.sendText(null, userid1, "已经拒绝对方，并提示电话联系！", "text");
                    break;
                case "timeout":
                    robotSpeek("超过一分钟没有答复，请您自行联系!", 0);
                    wxcpUtils.sendText(null, userid1, "您超过一分钟没有回复，该信息已经失效！", "text");
                    break;
            }


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
            return new GraphicFaceTracker(mGraphicOverlay);
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
            mFaceGraphic = new FaceGraphic(overlay, MainActivityN.this);


        }


        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
            isFirst = true;
            mFaceGraphic.setIsFirst(isFirst);
//            if (item.getIsSmilingProbability() > 0) {
//                firstSmile = item.getIsSmilingProbability();
//            } else {
//                firstSmile = 0;
//            }
            firstSmile = item.getIsSmilingProbability();
            robot_state_text.setText("请您微笑");
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
            rb_normal.setRating(lastSmile * 5);
            if (isFirst && lastSmile - firstSmile >= 0.6) {
                Log.e("smile", "lastSmile is " + lastSmile + ", firstSmile is " + firstSmile);
                mCameraSource.takePicture(new CameraSource.ShutterCallback() {
                    @Override
                    public void onShutter() {

                    }
                }, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        if (bytes != null) {
                            identifyFace.setData(bytes);
                            robot_state_text.setText("识别成功");
                            FileUtils.getFileFromBytes(bytes);
//                            mTts.startSpeaking("打卡成功", null);
//                            mSpeech.speak("success!", TextToSpeech.QUEUE_FLUSH, null);
                            isFirst = false;
                            mFaceGraphic.setIsFirst(isFirst);

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
//            robot_state_text.setText("准备就绪");
            rb_normal.setRating(0);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
            rb_normal.setRating(0);
        }
    }

    //请出示身份证的跳转方法
    private void takeIdCard() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(getApplication()).getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_NATIVE_TOKEN,
                OCR.getInstance().getLicense());
        intent.putExtra(CameraActivity.KEY_NATIVE_ENABLE,
                true);
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    //扫描身份证回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                    }
                }
            }
        }
    }

    private void recIDCard(String idCardSide, String filePath) {
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);

        OCR.getInstance().recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (result != null) {
                    LogToastUtils.toastShort(getApplicationContext(), result.toString());
                    robotSpeek(String.format(speekDaoUtils.querySpeekingText("connectForYou"), username), 2);
                }
            }

            @Override
            public void onError(OCRError error) {
                LogToastUtils.toastShort(getApplicationContext(), error.getMessage());
            }
        });
    }
}
