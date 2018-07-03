package com.topandnewapps.newyearzipperlock.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.topandnewapps.newyearzipperlock.Lockscreen;
import com.topandnewapps.newyearzipperlock.LockscreenUtil;
import com.topandnewapps.newyearzipperlock.PowerUtil;
import com.topandnewapps.newyearzipperlock.R;
import com.topandnewapps.newyearzipperlock.SharedPreferencesUtil;
import com.topandnewapps.newyearzipperlock.TimeAndDateSetter;
import com.topandnewapps.newyearzipperlock.TimeChangeReceiver;


/**
 * Created by mugku on 15. 5. 20..
 */
public class LockscreenViewService extends Service {
    private RelativeLayout batteryImg;

    private boolean isPowerReceiverRegistered;
    private boolean isTimeReceiverRegistered;
    private TimeChangeReceiver timeChangeReceiver;
    private TextView tim, date,charger,battery,tvv,smss,ampm;
    private Typeface tf,uf;
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private View mLockscreenView = null;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;

    /*Declar Zipper Functions*/
    private ImageView zipImageView;
    private final int[] IMAGE_UNZIP = {
            R.drawable.l1,R.drawable.l2, R.drawable.l3, R.drawable.l4, R.drawable.l5,
            R.drawable.l6, R.drawable.l7, R.drawable.l8, R.drawable.l9
    };
    private int frameNumber = 0;
    private boolean isDownFromStart = false;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mStartWidthRange;
    private int mEndWidthRange;
    private RelativeLayout patch,ribbon,center;
    private boolean mIsLockEnable = false;
    private boolean mIsSoftkeyEnable = false;
    private int mDeviceWidth = 0;
    private int mDevideDeviceWidth = 0;
    private float mLastLayoutX = 0;
    private int mServiceStartId = 0;
     private MediaPlayer mp;

    private SendMassgeHandler mMainHandler = null;
//    private boolean sIsSoftKeyEnable = false;

    private class SendMassgeHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        SharedPreferencesUtil.init(mContext);
//        sIsSoftKeyEnable = SharedPreferencesUtil.get(Lockscreen.ISSOFTKEY);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMainHandler = new SendMassgeHandler();
        if (isLockScreenAble()) {
            if (null != mWindowManager) {
                if (null != mLockscreenView) {
                    mWindowManager.removeView(mLockscreenView);
                }
                mWindowManager = null;
                mParams = null;
                mInflater = null;
                mLockscreenView = null;
            }
            initState();
            initView();
            attachLockScreenView();
        }
        return LockscreenViewService.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        dettachLockScreenView();
    }


    private void initState() {

        mIsLockEnable = LockscreenUtil.getInstance(mContext).isStandardKeyguardState();
        if (mIsLockEnable) {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                    PixelFormat.TRANSLUCENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mIsLockEnable && mIsSoftkeyEnable) {
                mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            } else {
                mParams.flags = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            }
        } else {
            mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }

        if (null == mWindowManager) {
            mWindowManager = ((WindowManager) mContext.getSystemService(WINDOW_SERVICE));
        }
    }

    private void initView() {
        if (null == mInflater) {
            mInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (null == mLockscreenView) {
            mLockscreenView = mInflater.inflate(R.layout.activity_zipper, null);

        }
    }

    private boolean isLockScreenAble() {
        boolean isLock = SharedPreferencesUtil.get(Lockscreen.ISLOCK);
        if (isLock) {
            isLock = true;
        } else {
            isLock = false;
        }
        return isLock;
    }


    private void attachLockScreenView() {

        if (null != mWindowManager && null != mLockscreenView && null != mParams) {
            mLockscreenView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            mWindowManager.addView(mLockscreenView, mParams);
            settingLockView();
        }

    }


    private boolean dettachLockScreenView() {
        if (null != mWindowManager && null != mLockscreenView) {
            mWindowManager.removeView(mLockscreenView);
            mLockscreenView = null;
            mWindowManager = null;
            stopSelf(mServiceStartId);
            return true;
        } else {
            return false;
        }
    }


    private void settingLockView() {


        AdView adView = (AdView)mLockscreenView.findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());


        tf = Typeface.createFromAsset(getAssets(),"roboto.ttf");
        uf = Typeface.createFromAsset(getAssets(),"roboto.ttf");
        date=(TextView) mLockscreenView.findViewById(R.id.tv_date);
        tim=(TextView)mLockscreenView.findViewById(R.id.tv_time);
        ampm=(TextView)mLockscreenView.findViewById(R.id.tv_ampm);
        charger = (TextView)mLockscreenView.findViewById(R.id.charger);
        battery = (TextView)mLockscreenView.findViewById(R.id.bettry);
        tim.setTypeface(tf);
        date.setTypeface(uf);
        ampm.setTypeface(uf);
        registerReceiver(mbatinforeceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        isPowerReceiverRegistered=true;
        IntentFilter localIntentFilter1 = new IntentFilter();
        localIntentFilter1.addAction("android.intent.action.TIME_TICK");

        timeChangeReceiver = new TimeChangeReceiver(tim, date,ampm);
        registerReceiver(timeChangeReceiver, localIntentFilter1);
        isTimeReceiverRegistered = true;


        new TimeAndDateSetter(tim, date,ampm).setTimeAndDate();

        zipImageView = (ImageView)mLockscreenView.findViewById(R.id.zipImageView);
        zipImageView.setBackgroundResource(IMAGE_UNZIP[0]);
        new Handler(new Handler.Callback() {

            public boolean handleMessage(Message paramMessage) {
                zipImageView.setBackgroundResource(IMAGE_UNZIP[paramMessage.arg1]);
                frameNumber = paramMessage.arg1;
                return false;
            }
        });
        zipImageView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int i = 0;

                mScreenHeight = zipImageView.getHeight();
                mScreenWidth = zipImageView.getWidth();

                mStartWidthRange = (2 * (mScreenWidth / 5));
                mEndWidthRange = (3 * (mScreenWidth / 5));

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:


                        if ((event.getY() < mScreenHeight / 4)
                                && (event.getX() > mStartWidthRange)
                                && (event.getX() < mEndWidthRange)) {

                            isDownFromStart = true;

                        } else
                            isDownFromStart = false;

                        break;
                    case MotionEvent.ACTION_MOVE:


                        if (isDownFromStart)

                            if ((event.getX() > mStartWidthRange)
                                    && (event.getX() < mEndWidthRange)
                                    && isDownFromStart) {

                                i = (int) (event.getY() / (mScreenHeight / 9));
                                setImage(i);
                            }

                        break;

                    case MotionEvent.ACTION_UP:


                        if (frameNumber >= 8) {
                            frameNumber = 0;
                            isDownFromStart = true;

                            mp = MediaPlayer.create(getApplicationContext(),
                                    R.raw.iphone);


                            mp.start();


                            dettachLockScreenView();

                        } else {
                            frameNumber = 0;
                            setImage(0);


                        }

                        break;
                    default:
                        break;
                }
                return true;
            }
        });




    }

    public BroadcastReceiver mbatinforeceiver = new BroadcastReceiver() {
        @SuppressWarnings("static-access")
        public void onReceive(Context c, Intent i) {

            int level = i.getIntExtra("level", 0);

            battery.setText("" + Integer.toString(level) + "%");
            battery.setTypeface(tf,tf.BOLD);
            if (i.getAction().equals("android.intent.action.BATTERY_CHANGED"))
            {
                if(PowerUtil.isConnected(c))
                {
                    charger.setBackgroundResource(R.drawable.battery_plugin);

                }
                else
                {
                    charger.setBackgroundResource(R.drawable.charger);


                }


            }

        }

    };

    /*Changing Frames of Zipper*/
    private void setImage(int paramInt) {
        switch (paramInt) {

            case 0:
                zipImageView.setBackgroundResource(IMAGE_UNZIP[0]);
                frameNumber = 1;

                break;
            case 1:
                zipImageView.setBackgroundResource(IMAGE_UNZIP[1]);
                frameNumber = 2;


                break;
            case 2:
                zipImageView.setBackgroundResource(IMAGE_UNZIP[2]);
                frameNumber = 3;

                break;
            case 3:
                zipImageView.setBackgroundResource(IMAGE_UNZIP[3]);
                frameNumber = 4;


                break;
            case 4:
                zipImageView.setBackgroundResource(IMAGE_UNZIP[4]);
                frameNumber = 5;

                break;

            case 5:
                zipImageView.setBackgroundResource(IMAGE_UNZIP[5]);
                frameNumber = 6;

                break;
            case 6:
                zipImageView.setBackgroundResource(IMAGE_UNZIP[6]);
                frameNumber = 7;



                break;
            case 7:
                zipImageView.setBackgroundResource(IMAGE_UNZIP[7]);
                frameNumber = 8;
                //patch.setVisibility(View.VISIBLE);
                break;
            case 8:
                zipImageView.setBackgroundResource(IMAGE_UNZIP[8]);
                frameNumber = 9;
                break;
		/*case 9:
			zipImageView.setBackgroundResource(IMAGE_UNZIP[9]);
			frameNumber = 10;
			break;*/

            default:
                return;
        }
    }
    class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    dettachLockScreenView();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    System.out.println("call Activity off hook");
                    dettachLockScreenView();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    dettachLockScreenView();
                    break;
            }
        }
    };
}
