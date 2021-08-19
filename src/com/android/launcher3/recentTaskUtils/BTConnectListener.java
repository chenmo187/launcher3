package com.android.launcher3.recentTaskUtils;
/*
Created by xiaoyu on 2021/2/1

Describe:用于接收蓝牙服务绑定状态

*/


import android.os.RemoteException;
import android.util.Log;

import com.tech.bt_libray.TechBTSettingServer;
import com.techbt.core.TechBTClient;
import com.techbt.iface.TechServiceBindListener;

public class BTConnectListener implements TechServiceBindListener {
    private static final String TAG = "BTConnect";
    private boolean isConnectServiceSuccess = false;
    TechBTSettingServer settingServer;

    public interface IBTEnableStateCallBack {
        void currentBTState(int powerState, int connectState);
    }

    private static BTConnectListener instance = null;

    public static BTConnectListener getInstance() {
        synchronized (BTConnectListener.class) {
            if (instance == null) {
                instance = new BTConnectListener();
            }
        }
        return instance;
    }

    private IBTEnableStateCallBack listener;

    public void registerBTEnable(IBTEnableStateCallBack callBack) {
        this.listener = callBack;
        Log.d(TAG, "registerBTEnable success ");
    }

    int state, connectState;


    public void getConnectState() {
        if (isConnectServiceSuccess) {
            try {
                connectState = settingServer.getHfpConnectState();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (listener != null) {
            Log.d(TAG, "getConnectState: 重新获取到的蓝牙最新连接状态为:" + connectState);
            listener.currentBTState(state, connectState);
        }

    }

    @Override
    public void onServiceBind() {
        settingServer = TechBTClient.getInstance().getSettingServer();
        BTSetting setting = BTSetting.getInstance();
        try {
            if (settingServer != null) {
                settingServer.registerCallback(setting);
                isConnectServiceSuccess = true;
                state = settingServer.getBTEnabledState();
                connectState = settingServer.getHfpConnectState();
                if (listener != null) {
                    listener.currentBTState(state, connectState);
                }
                Log.d(TAG, "onServiceBind ---开机绑定蓝牙服务成功了 获取到蓝牙开关状态为:" + state + "  当前连接状态为:" + connectState);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onServiceUnBind() {
        isConnectServiceSuccess = false;
        Log.d(TAG, "onServiceUnBind 解除绑定蓝牙服务");

    }
}
