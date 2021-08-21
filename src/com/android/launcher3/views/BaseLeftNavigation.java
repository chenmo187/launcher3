package com.android.launcher3.views;
/*
Created by xiaoyu on 2021/8/20

Describe:左侧任务栏

*/


import android.content.BroadcastReceiver;
import android.view.View;

import com.android.launcher3.Launcher;
import com.android.launcher3.chelianyiLeftNavigation;
import com.android.launcher3.leftnavigationCons;

import java.util.List;

public class BaseLeftNavigation implements Launcher.LauncherNavigationStatue {
    public static final String LUZHIYIN = "luzhiyin";
    public static final String CHELIANYI = "chelianyi";
    private static BaseLeftNavigation instance;
    public static Launcher mContext;

    public BaseLeftNavigation() {

    }

    //对外提供
    public static BaseLeftNavigation get() {
        if (instance != null) {
            return instance;
        }
        return new BaseLeftNavigation();
    }

    // private static final String type = "luzhiyin";
    private static final String type = "chelianyi";

    public static String getCurrentType() {
        return type;
    }

    public static BaseLeftNavigation getInstance(Launcher launcher) {
        mContext = launcher;
        if (instance == null) {
            synchronized (BaseLeftNavigation.class) {
                if (type.equals(LUZHIYIN)) {
                    instance = new leftnavigationCons();
                } else if (type.equals(CHELIANYI)) {
                    instance = new chelianyiLeftNavigation();
                }

            }
        }
        return instance;
    }


    //--------------------------------------

    public View getLayout() {
        return null;
    }

    public void WifiEnable(int rssi) {
    }

    public void WifiConnected(int rssi) {
    }

    public void WifiDisable() {
    }

    public void BluetoothConnected() {
    }

    public void BluetoothDisConnected() {
    }

    public void BluetoothConnectState(int state) {
    }

    public void currentBTState(int connectState) {
    }

    public void PhoneNetLevelChange(int level, String type) {
    }

    public void back2Car(String result) {
    }

    public void recent(List<String> task) {
    }

    public void initDefTaskIcon() {
    }

    public int getRecentTaskNum() {
        return 0;
    }

    //---------end--------------------------


    public void registNavigaListener() {
        mContext.registNavigationCallBack(this);
    }

    @Override
    public void onWifiEnable(int rssi) {
        WifiEnable(rssi);
    }

    @Override
    public void onWifiConnected(int rssi) {
        WifiConnected(rssi);
    }

    @Override
    public void onWifiDisable() {
        WifiDisable();
    }

    @Override
    public void onBluetoothConnected() {
        BluetoothConnected();
    }

    @Override
    public void onBluetoothDisConnected() {
        BluetoothDisConnected();
    }

    @Override
    public void onBluetoothConnectState(int state) {
        BluetoothConnectState(state);
    }

    @Override
    public void oncurrentBTState(int connectState) {
        currentBTState(connectState);
    }

    @Override
    public void onPhoneNetLevelChange(int level, String type) {
        PhoneNetLevelChange(level, type);
    }

    @Override
    public void back2CarResult(String result) {
        back2Car(result);
    }

    @Override
    public void recentTask(List<String> task) {
        recent(task);
    }
}
