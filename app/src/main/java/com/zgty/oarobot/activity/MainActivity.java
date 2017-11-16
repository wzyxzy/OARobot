package com.zgty.oarobot.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SynthesizerListener;
import com.zgty.oarobot.R;
import com.zgty.oarobot.bean.Account;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.bean.Time;
import com.zgty.oarobot.common.CommonActivity;
import com.zgty.oarobot.dao.AccountDaoUtils;
import com.zgty.oarobot.dao.StaffDaoUtils;
import com.zgty.oarobot.dao.TimeDaoUtils;
import com.zgty.oarobot.util.IdentifyFace;
import com.zgty.oarobot.util.JsonParser;
import com.zgty.oarobot.util.LogToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.zgty.oarobot.common.Constant.MAIN_CHECK_CAMERA_TYPE;
import static com.zgty.oarobot.common.OARobotApplication.mTts;

public class MainActivity extends CommonActivity implements View.OnClickListener {

    private TextView mode_name;
    private TextView change_mode;
    private TextView name_staff;
    private TextView id_staff;
    private TextView sign_up_time;
    private TextView station_state;
    private FrameLayout camera_preview;

    private TextView name_part;
    private TextView robot_speak_text;
    private TextView setting_main;
    private IdentifyFace identifyFace;
    private String userid = "00000";

    private double timeon;
    private double timeonlate;
    private double timeoffearly;
    private double timeoff;
    private double timeadd;
    private double timenow;
    // 语音听写对象
    private SpeechRecognizer mIat;
    private int noanswer = 0;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CAMERA_CODE = 0x100;

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
                    robotSpeek("我还不认识您，请问您找谁?", 1);

                    break;
                case 2:
                    robotSpeek("欢迎使用中广通业打卡系统", 0);
                    mIat = SpeechRecognizer.createRecognizer(getApplicationContext(), mInitListener);
                    onResume();
                    //启动本地引擎
//					String path = ResourceUtil.TTS_RES_PATH+"="+ ResourceLoader.getResPath(this,mSharedPreferences,"tts")+","+ResourceUtil.ENGINE_START+"=tts";
//					Boolean  ret = SpeechUtility.getUtility().setParameter(ResourceUtil.ENGINE_START,path);
//					showTip("启动本地引擎结果："+ret);
                    break;
            }

        }
    };


    /**
     * 合成回调监听。
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
//            // 合成进度
//            mPercentForBuffering = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
//            // 播放进度
//            mPercentForPlaying = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
                startHearing();
//                robot_speak_text.setText(null);
            } else if (error != null) {
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
                for (int i1 = 0; i1 < staffList.size(); i1++) {
                    if (text.contains(staffList.get(i1).getName_user())) {
                        robotSpeek(String.format("正在为您联系%s,请稍后！", staffList.get(i1).getName_user()), 2);
                        hasPerson = true;
                        break;
                    }
                }
                if (!hasPerson) {
                    robotSpeek("没有找到您所找的人，请您自行联系", 0);
                }
                break;
            case 1:
                noanswer++;
                if (noanswer < 3) {
                    robotSpeek("我没有听清您说的话，请问您找谁？", 1);
                }

                break;
        }
    }

    private void robotSpeek(String s, int type) {
        switch (type) {
            case 0:
                mTts.startSpeaking(s, null);

                break;
            case 1:
                //mTtsListener
                mTts.startSpeaking(s, mTtsListener);

                break;
            case 2:
                //微信联系
                mTts.startSpeaking(s, null);
                break;
        }
        robot_speak_text.setTextColor(getResources().getColor(R.color.colorAccent));
        robot_speak_text.setText(s);
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
        setContentView(R.layout.activity_main);
        requestPermissions();
        initView();
        handler.sendEmptyMessage(2);

    }


    @Override
    protected void onResume() {
        super.onResume();
        initTime();
        initAcoount();
        if (identifyFace == null) {
            identifyFace = new IdentifyFace(camera_preview, this, MAIN_CHECK_CAMERA_TYPE);
            identifyFace.openSurfaceView();
        }
        identifyFace.setOnIdentifyListener(new IdentifyFace.OnIdentifyListener() {
            @Override
            public void onSuccess(String user_id) {
                LogToastUtils.log(TAG, "success");
                handler.sendEmptyMessage(0);
                userid = user_id;
//                onResume();
            }

            @Override
            public void onSwitch() {
                LogToastUtils.log(TAG, "switch");
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onError() {
                LogToastUtils.log(TAG, "error");
            }

            @Override
            public void onCapture() {

            }

            @Override
            public void onRegisterSuccess() {

            }
        });

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        mode_name = findViewById(R.id.mode_name);
        change_mode = findViewById(R.id.change_mode);
        name_staff = findViewById(R.id.name_staff);
        id_staff = findViewById(R.id.id_staff);
        sign_up_time = findViewById(R.id.sign_up_time);
        station_state = findViewById(R.id.station_state);
        camera_preview = findViewById(R.id.camera_preview);


        change_mode.setOnClickListener(this);
        name_part = findViewById(R.id.name_part);
        robot_speak_text = findViewById(R.id.robot_speak_text);
        setting_main = findViewById(R.id.setting_main);
        setting_main.setOnClickListener(this);

    }

    //识别完毕后，数据加载，写在异步线程
    private void initData() {
//        userid = "10003";
        List<Staff> staffList = new StaffDaoUtils(this).queryStaffList(userid);
        if (staffList != null && staffList.size() > 0) {
            Staff staff = staffList.get(0);
            name_staff.setText(staff.getName_user());
            id_staff.setText(staff.getId_clerk());
            name_part.setText(staff.getName_part());
            String nowtime = getNowTime();
            timenow = changeTimeToDouble(nowtime);
            sign_up_time.setText(nowtime);
            station_state.setText(getType(staff));

        } else {
            LogToastUtils.toastShort(this, "没有录入该信息");
//            mTts.startSpeaking("没有录入该信息", null);
        }

    }

    //获取打卡类型
    private String getType(Staff staff) {
        String type = "";
        if (timenow >= timeadd) {
            type = "加班";
            robotSpeek(String.format("%s，您辛苦了，都加班这么晚了！", staff.getName_user()), 0);
        } else if (timenow >= timeoff) {
            type = "正常下班";
            robotSpeek(String.format("%s，工作辛苦了，下班后让自己放松下！", staff.getName_user()), 0);
        } else if (timenow > timeoffearly) {
            type = "早退";
            robotSpeek(String.format("%s，还没有下班呢，不能早退哦！", staff.getName_user()), 0);
        } else if (timenow > timeonlate) {
            type = "中途外出";
            robotSpeek(String.format("%s，请您通过！", staff.getName_user()), 0);
        } else if (timenow > timeon) {
            type = "迟到";
            robotSpeek(String.format("%s，您今天迟到了，相信您以后不会再迟到了！", staff.getName_user()), 0);
        } else {
            type = "正常上班";
            robotSpeek(String.format("%s，早上好，新的一天开始了，祝您工作好心情！", staff.getName_user()), 0);
        }


        return type;
    }

    //获取当前时间
    private String getNowTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINA);
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
                intent = new Intent(this, AdminActivity.class);
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
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        identifyFace.finisheIdentify();
        identifyFace = null;

    }
}
