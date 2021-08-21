package com.android.launcher3;
/*
Created by xiaoyu on 2021/8/5

Describe:

*/


import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.recentTaskUtils.AppOpUtils;
import com.android.launcher3.recentTaskUtils.BitmapUtil;
import com.android.launcher3.views.BaseLeftNavigation;
import com.carsyso.mainsdk.manager.KeyManager;

import java.util.ArrayList;
import java.util.List;

import static com.android.launcher3.Launcher.BT_CONNECTING;
import static com.android.launcher3.Launcher.BT_CONNECT_FAIL;
import static com.android.launcher3.Launcher.BT_CONNECT_SUCCESS;
import static com.android.launcher3.Launcher.BT_DISCONNECT;
import static com.carsyso.main.aidl.sdk.Key.KeyAction.KEY_DOWN;
import static com.carsyso.main.aidl.sdk.Key.KeyAction.KEY_UP;
import static com.carsyso.main.aidl.sdk.Key.KeyCode.KEY_BACK;
import static com.carsyso.main.aidl.sdk.Key.KeyCode.KEY_SPEECH;

public class leftnavigationCons extends BaseLeftNavigation implements /*Launcher.LauncherNavigationStatue,*/ View.OnClickListener {
    private static String TAG = "leftnavigationCons";
    // private static Launcher mContext;

    static List<String> deftasklist = new ArrayList<>(); //开机默认常用app图标

    static {
        deftasklist.add("com.android.settings");//设置
        deftasklist.add("com.carsyso.bluetooth");//蓝牙电话
        deftasklist.add("com.google.android.apps.maps");//谷歌地图
    }

    static List<ImageButton> leftTask = new ArrayList<>();//左侧最近任务图标

//    public leftnavigationCons(Launcher context) {
//        this.mContext = context;
//        Log.d(TAG, "111 get launcher ");
//    }

    public leftnavigationCons() {
        Log.d(TAG, "leftnavigationCons");
    }


    private ConstraintLayout layout;

    @Override
    public View getLayout() {
        layout = (ConstraintLayout) LayoutInflater.from(mContext).inflate(R.layout.luzhiyin_left_navigation, mContext.getRootConstrain(), false);
        Log.d(TAG, "getLayout: 获取到view");
        addLayout();
        initChildView();
        registNavigaListener();
        setListener();
        return layout;
    }


    //添加到导航栏里
    private void addLayout() {
        mContext.getRootConstrain().addView(layout);//ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.MATCH_CONSTRAINT
        ConstraintSet set = new ConstraintSet();
        set.clone(mContext.getRootConstrain());
        set.constrainWidth(layout.getId(), ConstraintSet.MATCH_CONSTRAINT);
        set.constrainHeight(layout.getId(), ConstraintSet.MATCH_CONSTRAINT);
        set.connect(layout.getId(), ConstraintSet.TOP, mContext.getRootConstrain().getId(), ConstraintSet.TOP);
        set.connect(layout.getId(), ConstraintSet.BOTTOM, mContext.getRootConstrain().getId(), ConstraintSet.BOTTOM);
        set.connect(layout.getId(), ConstraintSet.START, mContext.getRootConstrain().getId(), ConstraintSet.START);
        set.connect(layout.getId(), ConstraintSet.END, mContext.getRootConstrain().getId(), ConstraintSet.END);
        TransitionManager.beginDelayedTransition(mContext.getRootConstrain());
        set.applyTo(mContext.getRootConstrain());
    }


    private ImageButton imgBtn_task1, imgBtn_task2, imgBtn_task3, img_btn_mic, iv_all_apps, ib_exitAllApps;
    private ImageView img_bt_state, img_ss, img_wifi_state;
    private TextView txv_netType;
    private LinearLayout ll_exitAllApps;

    private void initChildView() {
        imgBtn_task1 = (ImageButton) mContext.findViewById(R.id.iv_music_lzy);
        imgBtn_task2 = (ImageButton) mContext.findViewById(R.id.iv_bluetooth_lzy);
        imgBtn_task3 = (ImageButton) mContext.findViewById(R.id.img_btn_back_lzy);
        img_bt_state = (ImageView) mContext.findViewById(R.id.img_bt_connect_lzy);
        img_ss = (ImageView) mContext.findViewById(R.id.img_s_s_lzy);
        txv_netType = (TextView) mContext.findViewById(R.id.tv_net_type_lzy);
        img_wifi_state = (ImageView) mContext.findViewById(R.id.img_wifi_s_lzy);

        img_btn_mic = (ImageButton) mContext.findViewById(R.id.img_btn_mic_lzy);
        iv_all_apps = (ImageButton) mContext.findViewById(R.id.iv_allapps_lzy);

        ib_exitAllApps = (ImageButton) mContext.findViewById(R.id.ib_exitAllApps);
        ll_exitAllApps = (LinearLayout) mContext.findViewById(R.id.ll_exitAllApps);

        //添加左侧任务栏最近任务容器
        leftTask.clear();
        leftTask.add(imgBtn_task1);
        leftTask.add(imgBtn_task2);
        leftTask.add(imgBtn_task3);

    }

    private void setListener() {
        imgBtn_task1.setOnClickListener(this::onClick);
        imgBtn_task2.setOnClickListener(this::onClick);
        img_btn_mic.setOnClickListener(this::onClick);
        imgBtn_task3.setOnClickListener(this::onClick);
        iv_all_apps.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_allapps_lzy:
                mContext.checkShowAllApp();
                break;
            case R.id.iv_music_lzy:
            case R.id.iv_bluetooth_lzy:
            case R.id.img_btn_back_lzy:
                AppOpUtils.openAppByPkgName(mContext, (String) v.getTag());
                break;
//                try {
//                    KeyManager.getInstance().requestMockKeyEvent(KEY_BACK, KEY_DOWN);
//                    Thread.sleep(20);
//                    KeyManager.getInstance().requestMockKeyEvent(KEY_BACK, KEY_UP);
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                }

            case R.id.img_btn_mic_lzy:

                try {
                    KeyManager.getInstance().requestMockKeyEvent(KEY_SPEECH, KEY_DOWN);
                    Thread.sleep(20);
                    KeyManager.getInstance().requestMockKeyEvent(KEY_SPEECH, KEY_UP);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

//    private void registNavigaListener() {
//        mContext.registNavigationCallBack(this);
//    }

    //放到base中让子类自行上报自己所需任务数
    @Override
    public int getRecentTaskNum() {
        return 3;
    }

    @Override
    public void WifiEnable(int rssi) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                img_wifi_state.setImageResource(R.drawable.wifi_level);
                img_wifi_state.setImageLevel(rssi);
            }
        });
    }

    @Override
    public void WifiConnected(int rssi) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                img_wifi_state.setImageResource(R.drawable.wifi_level);
                img_wifi_state.setImageLevel(rssi);
            }
        });
    }

    @Override
    public void WifiDisable() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                img_wifi_state.setImageResource(R.drawable.wifi_lev0);
            }
        });
    }

    @Override
    public void BluetoothConnected() {

    }

    @Override
    public void BluetoothDisConnected() {

    }

    @Override
    public void BluetoothConnectState(int state) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case BT_CONNECT_SUCCESS:
                        img_bt_state.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.bt_open_icon));
                        Log.d(TAG, "onBluetoothConnectState: 蓝牙已连接");
                        break;
                    case BT_DISCONNECT:
                        img_bt_state.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.bt_close_icon));
                        Log.d(TAG, "onBluetoothConnectState: 蓝牙断开");
                        break;
                    case BT_CONNECT_FAIL:
                        Log.d(TAG, "onBluetoothConnectState: 蓝牙连接失败!!!!!!");
                        break;
                    case BT_CONNECTING:
                        Log.d(TAG, "onBluetoothConnectState: 蓝牙连接中....");
                        break;
                }
            }
        });
    }

    @Override
    public void currentBTState(int connectState) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (connectState) {
                    case BT_CONNECT_SUCCESS:
                        img_bt_state.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.bt_open_icon));
                        Log.d(TAG, "currentBTState:开机 蓝牙已连接");
                        break;
                    case BT_DISCONNECT:
                        img_bt_state.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.bt_close_icon));
                        Log.d(TAG, "currentBTState:开机 蓝牙已断开，请检查");
                        break;
                    case BT_CONNECTING:
                        Log.d(TAG, "currentBTState:开机 蓝牙正在连接,稍后再次查询状态.....");
                        //当前正在连接中，间隔5秒再次询问连接成功或失败
                        mContext.getmHandler().sendEmptyMessageDelayed(0x3022, 15000);
                        break;
                    case BT_CONNECT_FAIL:
                        Log.d(TAG, "currentBTState:开机 蓝牙连接失败，请检查设备");
                        mContext.getmHandler().sendEmptyMessageDelayed(0x3022, 15000);
                        break;
                }
            }
        });
    }

    @Override
    public void PhoneNetLevelChange(int level, String type) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txv_netType.setText(type);

                switch (level) {
                    case 0:
                        // img_ss.setImageResource(R.mipmap.s_icon1);
                        img_ss.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.s_icon1));
                        break;
                    case 1:
                        // img_ss.setImageResource(R.mipmap.s_icon2);
                        img_ss.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.s_icon2));
                        break;
                    case 2:
//                        img_ss.setImageResource(R.mipmap.s_icon3);
//                        break;//不显示2格信号
                    case 3:
                        // img_ss.setImageResource(R.mipmap.s_icon4);
                        img_ss.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.s_icon4));
                        break;
                    case 4:
                    case 5:
                        //img_ss.setImageResource(R.mipmap.s_icon5);
                        img_ss.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.s_icon5));
                        break;
                }


            }
        });


    }

    //返回原车
    @Override
    public void back2Car(String result) {

    }

    @Override
    public void recent(List<String> tasks) {

        //接受最近任务变化
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tasks.size() == 1) {
                    Bitmap zoomImage = BitmapUtil.decodeBitmapInSize(tasks.get(0), mContext, 55, 55);
                    if (zoomImage != null) {
                        leftTask.get(0).setImageBitmap(zoomImage);
                        leftTask.get(0).setTag(tasks.get(0));
                        leftTask.get(0).setVisibility(View.VISIBLE);
                    } else {
                        leftTask.get(0).setImageBitmap(null);
                        leftTask.get(0).setTag(null);
                        leftTask.get(0).setVisibility(View.INVISIBLE);
                    }

                    //校验第二个位置
                    String pkgTag = (String) leftTask.get(1).getTag();
                    if (pkgTag == null) {
                        //该位置无app,找一个补齐
                        for (String name : deftasklist) {
                            if (!name.equals(tasks.get(0))) {
                                leftTask.get(1).setImageBitmap(BitmapUtil.decodeBitmapInSize(name, mContext, 55, 55));  //找出一个与第一个不同的显示
                                leftTask.get(1).setTag(name);
                                leftTask.get(1).setVisibility(View.VISIBLE);
                                break;
                            } else {
                                //默认三个app里都没有找到符合的，该位置就设置为隐藏
                                leftTask.get(1).setImageBitmap(null);
                                leftTask.get(1).setTag(null);
                                leftTask.get(1).setVisibility(View.INVISIBLE);
                            }
                        }
                    } else if (pkgTag.equals(tasks.get(0))) {
                        //校验该位置现有的app图标不应该与前面重复!!!!!! 若有重复，找一个替换，或者隐藏

                        for (String name : deftasklist) {
                            if (!name.equals(tasks.get(0))) {
                                leftTask.get(1).setImageBitmap(BitmapUtil.decodeBitmapInSize(name, mContext, 55, 55));  //找出一个与第一个不同的显示
                                leftTask.get(1).setTag(name);
                                leftTask.get(1).setVisibility(View.VISIBLE);
                                break;
                            } else {
                                //默认三个app里都没有找到符合的，该位置就设置为隐藏
                                leftTask.get(1).setImageBitmap(null);
                                leftTask.get(1).setTag(null);
                                leftTask.get(1).setVisibility(View.INVISIBLE);
                            }
                        }


                    }


                } else if (tasks.size() == 2) {
                    Bitmap zoomImage = BitmapUtil.decodeBitmapInSize(tasks.get(0), mContext, 55, 55);
                    if (zoomImage != null) {
                        leftTask.get(0).setImageBitmap(zoomImage);
                        leftTask.get(0).setTag(tasks.get(0));
                        leftTask.get(0).setVisibility(View.VISIBLE);
                    } else {
                        leftTask.get(0).setImageBitmap(null);
                        leftTask.get(0).setTag(null);
                        leftTask.get(0).setVisibility(View.INVISIBLE);
                    }

                    Bitmap zoomImage1 = BitmapUtil.decodeBitmapInSize(tasks.get(1), mContext, 55, 55);
                    if (zoomImage1 != null) {
                        leftTask.get(1).setImageBitmap(zoomImage1);
                        leftTask.get(1).setTag(tasks.get(1));
                        leftTask.get(1).setVisibility(View.VISIBLE);
                    } else {
                        leftTask.get(1).setImageBitmap(null);
                        leftTask.get(1).setTag(null);
                        leftTask.get(1).setVisibility(View.INVISIBLE);
                    }


                } else if (tasks.size() == 3) {
                    Bitmap zoomImage = BitmapUtil.decodeBitmapInSize(tasks.get(0), mContext, 55, 55);
                    if (zoomImage != null) {
                        leftTask.get(0).setImageBitmap(zoomImage);
                        leftTask.get(0).setTag(tasks.get(0));
                        leftTask.get(0).setVisibility(View.VISIBLE);
                    } else {
                        leftTask.get(0).setImageBitmap(null);
                        leftTask.get(0).setTag(null);
                        leftTask.get(0).setVisibility(View.INVISIBLE);
                    }
                    Bitmap zoomImage1 = BitmapUtil.decodeBitmapInSize(tasks.get(1), mContext, 55, 55);
                    if (zoomImage1 != null) {
                        leftTask.get(1).setImageBitmap(zoomImage1);
                        leftTask.get(1).setTag(tasks.get(1));
                        leftTask.get(1).setVisibility(View.VISIBLE);
                    } else {
                        leftTask.get(1).setImageBitmap(null);
                        leftTask.get(1).setTag(null);
                        leftTask.get(1).setVisibility(View.INVISIBLE);
                    }
                    Bitmap zoomImage2 = BitmapUtil.decodeBitmapInSize(tasks.get(2), mContext, 55, 55);
                    if (zoomImage2 != null) {
                        leftTask.get(2).setImageBitmap(zoomImage2);
                        leftTask.get(2).setTag(tasks.get(2));
                        leftTask.get(2).setVisibility(View.VISIBLE);
                    } else {
                        leftTask.get(2).setImageBitmap(null);
                        leftTask.get(2).setTag(null);
                        leftTask.get(2).setVisibility(View.INVISIBLE);
                    }
                } else if (tasks.size() == 0) {
                    initDefTaskIcon();
                }
            }
        });
    }


    //开机设置左侧任务栏的默认推荐图标
    @Override
    public void initDefTaskIcon() {

        Bitmap zoomImage = BitmapUtil.decodeBitmapInSize(deftasklist.get(0), mContext, 55, 55);
        if (zoomImage != null) {
            leftTask.get(0).setImageBitmap(zoomImage);
            leftTask.get(0).setTag(deftasklist.get(0));
            leftTask.get(0).setVisibility(View.VISIBLE);

        } else {

            leftTask.get(0).setImageBitmap(null);
            leftTask.get(0).setTag(null);
            leftTask.get(0).setVisibility(View.INVISIBLE);
        }
        Bitmap zoomImage1 = BitmapUtil.decodeBitmapInSize(deftasklist.get(1), mContext, 55, 55);
        if (zoomImage1 != null) {
            leftTask.get(1).setImageBitmap(zoomImage1);
            leftTask.get(1).setTag(deftasklist.get(1));
            leftTask.get(1).setVisibility(View.VISIBLE);
        } else {
            leftTask.get(1).setImageBitmap(null);
            leftTask.get(1).setTag(null);
            leftTask.get(1).setVisibility(View.INVISIBLE);
        }


        Bitmap zoomImage2 = BitmapUtil.decodeBitmapInSize(deftasklist.get(2), mContext, 55, 55);
        if (zoomImage2 != null) {
            leftTask.get(2).setImageBitmap(zoomImage2);
            leftTask.get(2).setTag(deftasklist.get(2));
            leftTask.get(2).setVisibility(View.VISIBLE);
        } else {
            leftTask.get(2).setImageBitmap(null);
            leftTask.get(2).setTag(null);
            leftTask.get(2).setVisibility(View.INVISIBLE);
        }
    }


}
