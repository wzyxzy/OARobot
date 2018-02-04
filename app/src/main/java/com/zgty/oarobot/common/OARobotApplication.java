package com.zgty.oarobot.common;

import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.facebook.stetho.Stetho;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.zgty.oarobot.R;
import com.zgty.oarobot.util.LogToastUtils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

import static com.zgty.oarobot.common.Constant.pSpeeker_xiaolin;
import static com.zgty.oarobot.common.Constant.pSpeeker_xiaoqi;
import static com.zgty.oarobot.common.Constant.pSpeeker_xiaoyan;

/**
 * Created by zy on 2017/10/31.
 * 初始化配置
 */

public class OARobotApplication extends Application {
    private static final String TAG = OARobotApplication.class.getSimpleName();
    // 语音合成对象
    public static SpeechSynthesizer mTts;

    //是否通过身份证验证
    public static final boolean isNeedId = false;
    // 默认云端发音人
    public static String voicerCloud = pSpeeker_xiaoyan;
    // 默认本地发音人
    public static String voicerLocal = pSpeeker_xiaoyan;
    //缓冲进度
    private int mPercentForBuffering = 0;
    //播放进度
    private int mPercentForPlaying = 0;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private static OARobotApplication instance;

    public static TextToSpeech mSpeech;
    public static boolean canUserGoogleTTS;

    @Override
    public void onCreate() {
        super.onCreate();
        initOARobot();
        initStetho();
        initOKGo();
        initTTS();
        initBaiduId();
        instance = this;


    }

    private void initBaiduId() {

        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                String token = result.getAccessToken();
                Log.e("success", token);
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
                Log.e("failed", error.getLocalizedMessage());
            }
        }, getApplicationContext(), "0eWNc2Lf8dChWjI89XEFxzf5", "r4T0RrOY3NElBrdCcWEHeofoB0saGgBE");

    }

    private void initOKGo() {
//        OkGo.getInstance().init(this);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        //使用sp保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
//        //使用数据库保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
//        //使用内存保持cookie，app退出后，cookie消失
//        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
//log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
//log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);
        OkGo.getInstance().init(this)                             //必须调用初始化
                .setOkHttpClient(builder.build())                 //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)                //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)                //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3);                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers)                     //全局公共头
//                .addCommonParams(params);                      //全局公共参数

    }

    private void initTTS() {

        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        if (null == mTts) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            LogToastUtils.toastShort(getApplicationContext(), "创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化");
            return;
        }
        //设置使用云端引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicerCloud);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "55");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "45");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
        //谷歌语音使用，已忽略
//        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//
//            @Override
//            public void onInit(int status) {
//                // TODO Auto-generated method stub
//                if (status == TextToSpeech.SUCCESS) {
//                    mSpeech.setLanguage(Locale.CHINA);    //设置语言为英语
////                    mSpeech.speak("welcome to use '中广同业' OA System", TextToSpeech.QUEUE_FLUSH, null);
//                    mSpeech.speak("欢迎使用中广通业考勤接待系统", TextToSpeech.QUEUE_FLUSH, null);
//                    canUserGoogleTTS = true;
//                } else {
        canUserGoogleTTS = false;
        mTts.startSpeaking("欢迎使用中广通业考勤接待系统", null);
//                }
//            }
//        });
//
    }

    private void initOARobot() {
        StringBuffer param = new StringBuffer();
        param.append(SpeechConstant.APPID + "=" + getString(R.string.app_id));
        SpeechUtility.createUtility(getApplicationContext(), param.toString());
//        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=59f2e233");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                LogToastUtils.toastShort(getApplicationContext(), "初始化失败,错误码：" + code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    private void initStetho() {
        Stetho.initializeWithDefaults(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    /**
     * 合成回调监听。
     */
    public static SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

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

    public static OARobotApplication getInstance() {
        return instance;
    }

}
