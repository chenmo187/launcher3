package com.android.launcher3.recentTaskUtils;
/*
Created by xiaoyu on 2021/1/29

Describe:

*/


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Locale;

public class LanguageUtils extends BroadcastReceiver {
    private static final String TAG = "LanguageUtils";
    public static final String ACTION_WALLPAPER_CHANGED = "android.intent.action.WALLPAPER_CHANGED";
    public static final String ACTION_GLOABAL_TOUCH_EVENT = "com.android.settings.action.event";
    public static final String ACTION_PHONE_NET_LEVEL = "com.suding.system.mobile.signal";
    public static final String ACTION_LAUNCHER_GO_HOME = "action.launcher.gohome";

    public interface languageChange {
       // void onChange(String language);

        void wifiConnected(int rssi);

        void wifiDisconnected();

        void wifiEnabled(int rssi);

        void wifiDisabled();

        void PhoneNetLevel(int level);

       // void onBackgroundImage();

        //void goHome();
    }

    private languageChange languageChange;
    private boolean isConnected = false;

    public void setOnLanguageChangeListener(languageChange listener) {
        languageChange = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Log.d(TAG, "action: " + action);

        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (action.equals(Intent.ACTION_LOCALE_CHANGED)) {
            //当前系统语音被更改了
        } else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
            //wifi连接状态

            SupplicantState state = connectionInfo.getSupplicantState();
            if (state == SupplicantState.ASSOCIATED) {
                Log.d(TAG, "onReceive: 关联wifi成功");
            } else if (state == SupplicantState.COMPLETED) {
                isConnected = true;
                int rssi = connectionInfo.getRssi();

                Log.d(TAG, "onReceive: wifi连接成功,信号强度：" + Math.abs(rssi));
                if (languageChange != null) {
                    languageChange.wifiConnected(Math.abs(rssi));
                }
            } else if (state == SupplicantState.DISCONNECTED) {
                Log.d(TAG, "onReceive: wifi 断开连接");
                isConnected = false;
                if (languageChange != null) {
                    languageChange.wifiDisconnected();
                }
            }
        } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            /**
             * WIFI_STATE_DISABLED    WLAN已经关闭
             * WIFI_STATE_DISABLING   WLAN正在关闭
             * WIFI_STATE_ENABLED     WLAN已经打开
             * WIFI_STATE_ENABLING    WLAN正在打开
             * WIFI_STATE_UNKNOWN     未知
             */
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifistate) {
                case WifiManager.WIFI_STATE_ENABLED:
                    //wifi开

                    if (languageChange != null) {
                        languageChange.wifiEnabled(Math.abs(connectionInfo.getRssi()));
                    }
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    //wifi关闭
                    if (languageChange != null) {
                        languageChange.wifiDisabled();
                    }
                    break;
            }
        }  else if (action.equals(ACTION_PHONE_NET_LEVEL)) {
            Bundle extras = intent.getExtras();
            int level = extras.getInt("level");
            if (languageChange != null) {
                languageChange.PhoneNetLevel(level);
            }
        }

    }
}


