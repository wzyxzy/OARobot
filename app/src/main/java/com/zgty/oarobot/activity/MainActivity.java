package com.zgty.oarobot.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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
import com.zgty.oarobot.bean.Speaking;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.bean.Time;
import com.zgty.oarobot.common.CommonActivity;
import com.zgty.oarobot.common.Constant;
import com.zgty.oarobot.dao.AccountDaoUtils;
import com.zgty.oarobot.dao.SpeekDaoUtils;
import com.zgty.oarobot.dao.StaffDaoUtils;
import com.zgty.oarobot.dao.TimeDaoUtils;
import com.zgty.oarobot.dao.WorkOnOffDaoUtils;
import com.zgty.oarobot.receiver.DateTimeReceiver;
import com.zgty.oarobot.service.RefreshService;
import com.zgty.oarobot.util.FileUtils;
import com.zgty.oarobot.util.IdentifyFace;
import com.zgty.oarobot.util.JsonParser;
import com.zgty.oarobot.util.LogToastUtils;
import com.zgty.oarobot.util.WXCPUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private TextView waiting_text;

    private TextView name_part;
    private TextView robot_speak_text;
    private TextView setting_main;
    private IdentifyFace identifyFace;  //识别工具，待分离
    private String userid;//用户ID

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

    private static final String TAG = MainActivity.class.getSimpleName();

    private SpeekDaoUtils speekDaoUtils;
    private File file;
    private Intent intentRefreshService;
    private RefreshListBroadCast listBroadCast;
    private WXCPUtils wxcpUtils;
    private boolean canConnect = true;
    private Handler handler1;
    private Runnable runnable;
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
                    robotSpeek(speekDaoUtils.querySpeekingText("cannotRecognise"), 1);

                    break;
                case 2:
//                    robotSpeek(speekDaoUtils.querySpeekingText("welcomeText"), 0);
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
                if (text.contains("打卡")) {
                    robotSpeek("现在已经可以打卡了!", 0);
                    return;
                }
                for (int i1 = 0; i1 < staffList.size(); i1++) {
                    if (text.contains(staffList.get(i1).getName_user())) {

                        if (canConnect) {
                            robotSpeek(String.format(speekDaoUtils.querySpeekingText("connectForYou"), staffList.get(i1).getName_user()), 2);
                            userid = staffList.get(i1).getId();
                        } else {
                            robotSpeek("前面已有联系任务，您还需等待" + time_second + "秒", 0);
                        }
                        hasPerson = true;
                        break;
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
                    identifyFace.startCameraView();
                }

                break;
        }
    }

    private void robotSpeek(String s, int type) {
        switch (type) {
            case 0:

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
//                            identifyFace.openSurfaceView();
                            identifyFace.startCameraView();

//                            identifyFace.openSurfaceView();

                        } else if (speechError != null) {
                            showTip(speechError.getPlainDescription(true));
                        }
                    }

                    @Override
                    public void onEvent(int i, int i1, int i2, Bundle bundle) {

                    }
                });
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
                            wxcpUtils.sendText(file, userid, "前台有人找您，您是否同意让他进来？", "image");
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
                mTts.startSpeaking(s, null);
                break;
        }
        robot_speak_text.setTextColor(getResources().getColor(R.color.colorAccent));
        robot_speak_text.setText(s);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                robot_speak_text.setText("");

            }
        }, 5000);
    }

    private void successConnect() {
        robotSpeek("已经为您通知，请等候!", 0);
        intentRefreshService = new Intent();
        intentRefreshService.setClass(getApplicationContext(), RefreshService.class);
        intentRefreshService.putExtra("userid", userid);
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
        setContentView(R.layout.activity_main);
        requestPermissions();
        initView();
        initAlarm();

        wxcpUtils = new WXCPUtils(this);
        handler.sendEmptyMessage(2);
        requestPermissions();
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
        if (identifyFace == null) {
            identifyFace = new IdentifyFace(camera_preview, this, MAIN_CHECK_CAMERA_TYPE,this);
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
            public void onSwitch(byte[] data) {
                file = FileUtils.getFileFromBytes(data);
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
            Speaking speaking1 = new Speaking("welcomeText", "打开软件欢迎语", "欢迎使用中广通业打卡系统");
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

        waiting_text = findViewById(R.id.waiting_text);

    }

    //识别完毕后，数据加载，写在异步线程
    private void initData() {
//        userid = "10003";
        List<Staff> staffList = new StaffDaoUtils(this).queryStaffList(userid);
        if (staffList != null && staffList.size() > 0) {
            Staff staff = staffList.get(0);
            if (staff.getUser_type().equals("1")) {
                robotSpeek(String.format(speekDaoUtils.querySpeekingText("getOffStaff"), staff.getName_user()), 3);
                return;
            }
            name_staff.setText(staff.getName_user());
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
//            mTts.startSpeaking("没有录入该信息", null);
        }

    }

    //获取打卡类型
    private String getType(Staff staff) {
        String type = "";

        if (timenow >= timeadd) {
            type = "加班";
            robotSpeek(String.format(speekDaoUtils.querySpeekingText("timeAddNormal"), staff.getName_user()), 0);
        } else if (timenow >= timeoff) {
            type = "正常下班";
            robotSpeek(String.format(speekDaoUtils.querySpeekingText("timeOffNormal"), staff.getName_user()), 0);
        } else if (timenow > timeoffearly) {
            type = "早退";
            robotSpeek(String.format(speekDaoUtils.querySpeekingText("timeOffEarly"), staff.getName_user()), 0);
        } else if (timenow > timeonlate) {
            type = "中途外出";
            robotSpeek(String.format(speekDaoUtils.querySpeekingText("goOutNormal"), staff.getName_user()), 0);
        } else if (timenow > timeon) {
            type = "迟到";
            robotSpeek(String.format(speekDaoUtils.querySpeekingText("timeOnLate"), staff.getName_user()), 0);
        } else {
            type = "正常上班";
            robotSpeek(String.format(speekDaoUtils.querySpeekingText("timeOnNormal"), staff.getName_user()), 0);
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
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        if (mTts != null) {
            mTts.stopSpeaking();
        }
        if (mIat != null) {
            mIat.stopListening();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                    wxcpUtils.sendText(null, userid, "已经让对方通过！","text");
                    break;
                case "combig":
                    robotSpeek("对方已经接受了您的请求，请您到大会议室等候!", 0);
                    wxcpUtils.sendText(null, userid, "已经让对方通过！","text");
                    break;
                case "comsmall":
                    robotSpeek("对方已经接受了您的请求，请您到小会议室等候!", 0);
                    wxcpUtils.sendText(null, userid, "已经让对方通过！","text");
                    break;
                case "talk1":
                    robotSpeek("对方已经接受了您的请求，请您到洽谈室1等候!", 0);
                    wxcpUtils.sendText(null, userid, "已经让对方通过！","text");
                    break;
                case "talk2":
                    robotSpeek("对方已经接受了您的请求，请您到洽谈室2等候!", 0);
                    wxcpUtils.sendText(null, userid, "已经让对方通过！","text");
                    break;
                case "reject":
                    robotSpeek("对方已经拒绝了您的请求，请您自行联系!", 0);
                    wxcpUtils.sendText(null, userid, "已经拒绝对方！","text");
                    break;
                case "timeout":
                    robotSpeek("超过一分钟没有答复，请您自行联系!", 0);
                    wxcpUtils.sendText(null, userid, "您超过一分钟没有回复，该信息已经失效！","text");
                    break;
            }


        }
    }
}
