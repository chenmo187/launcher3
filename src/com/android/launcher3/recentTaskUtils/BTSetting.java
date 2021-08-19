package com.android.launcher3.recentTaskUtils;
/*
Created by xiaoyu on 2021/2/1

Describe:接收蓝牙状态的改变

*/


import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.tech.bt_libray.BTSettingClient;
import com.techbt.pojo.DeviceItem;

import java.util.List;

public class BTSetting extends BTSettingClient.Stub {
    private static final String TAG = "BTSetting";
    private static BTSetting instance;

    public interface updateBlueToothState {
        void onBluetoothEnableState(int state);

        void onBluetoothConnectState(int state);
    }

    private updateBlueToothState listener;

    public void registerBluetoothStateListener(updateBlueToothState updateBlueToothState) {
        this.listener = updateBlueToothState;
    }

    public static BTSetting getInstance() {
        if (instance == null) {
            instance = new BTSetting();
        }
        return instance;
    }

    @Override
    public void onPairedDeviceListLoadFinished(List<DeviceItem> list) throws RemoteException {

    }

    @Override
    public void onPairedDeviceListLoading(DeviceItem deviceItem) throws RemoteException {

    }

    @Override
    public void onBTEnabledStateChanged(int i) throws RemoteException {
        Log.d(TAG, "onBTEnabledStateChanged 蓝牙开关状态: " + i);//1 蓝牙开启    0关闭
        if (listener != null) {
            listener.onBluetoothEnableState(i);
        }
    }

    @Override
    public void onHfpStateChanged(int i) throws RemoteException {
        Log.d(TAG, "onHfpStateChanged:蓝牙连接状态： " + i);//1 蓝牙已连接   0 蓝牙断开连接/失败     3 连接中   8连接失败
        if (listener !=null){
            listener.onBluetoothConnectState(i);
        }
    }

    @Override
    public void onSearchingBTDevice(DeviceItem deviceItem) throws RemoteException {

    }

    @Override
    public void onSearchBTDevicesFinished(List<DeviceItem> list) throws RemoteException {

    }

    @Override
    public void onRemotePairedDeviceNameChanged(String s) throws RemoteException {

    }

    @Override
    public void onRemotePairedDeviceMacAddressChanged(String s) throws RemoteException {

    }

    @Override
    public void onModifyLocalBTNameSuccess(String s) throws RemoteException {

    }

    @Override
    public void onModifyLocalBTPasswordSuccess(String s) throws RemoteException {

    }

    @Override
    public void onModifyPairedResult(boolean b) throws RemoteException {

    }

    @Override
    public void onPairedDeviceModelChange(String s) throws RemoteException {

    }

    @Override
    public void onSignalStrengthChange(int i) throws RemoteException {

    }

    @Override
    public void onBatteryLevelChange(int i) throws RemoteException {

    }

    @Override
    public void onTeleComServiceNameChange(String s) throws RemoteException {

    }

    @Override
    public IBinder asBinder() {
        return this;
    }
}
