package com.android.launcher3.recentTaskUtils;
/*
Created by xiaoyu on 2021/3/3

Describe:屏幕虚拟按键点击事件回调

*/


import android.util.Log;

import com.carsyso.mainsdk.release.iface.SmartBoxCarPlayTool;
import com.carsyso.mainsdk.release.iface.SystemObserver;

public class LauncherKeyManager extends SystemObserver implements SmartBoxCarPlayTool {
    private static LauncherKeyManager instance;
    private static String TAG = "LauncherKeyManager";


    public static LauncherKeyManager getinstance() {
        if (instance == null) {
            instance = new LauncherKeyManager();
        }

        return instance;
    }

    //--------------------------------------SmartBoxCarPlayTool--------------
    @Override
    public void onCarPlayUserInterfaceChange(boolean b) {

    }

    @Override
    public void onReverseCarPlayConnectedChange(boolean b) {

    }

    @Override
    public void onReverseCarPlayRequestApplyMockAudioFocus() {

    }

    @Override
    public void onReverseCarPlayRequestRebootDevice(int i) {

    }

    @Override
    public void onReverseCarPlayReturnNativeIcon(int i) {
        Log.d(TAG, "onReverseCarPlayReturnNativeIcon: Launcher收到原车状态：" + i);
        if (carBoxReturnListener != null) {
            carBoxReturnListener.returnCarBoxIconValue(String.valueOf(i));
        }
    }

    IsmartCarBoxReturn carBoxReturnListener;

    public void setSmartCarBoxReturnChangeListener(IsmartCarBoxReturn listener) {
        carBoxReturnListener = listener;
    }

    public interface IsmartCarBoxReturn {
        public void returnCarBoxIconValue(String value);
    }
}
