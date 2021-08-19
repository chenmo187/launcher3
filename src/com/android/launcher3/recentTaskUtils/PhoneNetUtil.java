package com.android.launcher3.recentTaskUtils;
/*
Created by xiaoyu on 2021/2/26

Describe:获取当前设备的网络运营商和信号

*/


import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 获取手机数据信号强度值工具类
 * dbm的值为负数
 * 0为最强信号值
 * -85以内为满格信号
 */
public class PhoneNetUtil {
    private static final int DBM_1 = -75;
    private static final int DBM_2 = -85;
    private static final int DBM_3 = -95;
    private static final int DBM_4 = -105;
    private static final int DBM_5 = -115;
    private static final String TAG = "PhoneNetUtil";
    private static PhoneNetUtil phoneNetUtil;
    private Context mcontext;
    private PhoneNetListener phoneNetListener;

    private TelephonyManager mTelephonyManager;


    public interface PhoneNetListener {

        /**
         * 信号强度显示格子数
         * 最强信号5 -- 最弱信号  0
         */

//        void getNetLevel(int level);

        /*
         * 获取运营商名称
         * */

        void getOperatorName(String name);

        /*
         * 网络类型 2G 3G 4G 5G
         * */
        void getCellularType(String type);
    }


    private PhoneNetUtil() {

    }

    public static PhoneNetUtil getInstance() {
        if (phoneNetUtil == null) {
            phoneNetUtil = new PhoneNetUtil();
        }
        return phoneNetUtil;
    }

    public void registerNetInfoChangeListener(Context context, PhoneNetListener listener) {
        this.mcontext = context;
        this.phoneNetListener = listener;
        mTelephonyManager = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 获取手机运营商，监听设备信号变化
     */
    String celluarType;

    public void getNetInfo() {
        Log.d(TAG, ">>>>>>>>>>>>>监听网络信号>>>>>>>>>>>>>>>>>: ");
        //this.phoneNetListener = listener;
        mTelephonyManager = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
        //android 5.1适用。高版本不可以用
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);

                @SuppressLint("MissingPermission")
                List<CellInfo> cellInfoList = mTelephonyManager.getAllCellInfo();
                if (cellInfoList != null) {
                    for (CellInfo cellInfo : cellInfoList) {
                        if (cellInfo instanceof CellInfoLte) {
                            //cast to CellInfoLte and call all the CellInfoLte methods you need
                            int dbm = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();
                            int asu = ((CellInfoLte) cellInfo).getCellSignalStrength().getAsuLevel();
                            String provider = getNetworkOperatorName();
                            celluarType = getCellularType();
                            if (!isMobileDataEnabled()) {
                                Log.d(TAG, "网络数据关闭了 ");
                            }


                            if (phoneNetListener != null) {
                                phoneNetListener.getOperatorName(provider);
                                //  phoneNetListener.getNetLevel(setNetLevel(dbm));
                                phoneNetListener.getCellularType(celluarType);
                            }
                            //String logTxt = String.format("%s %s dbm:%d asu:%d \n", provider, celluarType, dbm, asu);
                            Log.d(TAG, "运营商：" + provider + " 网络类型：" + celluarType + " dbm:" + dbm + " asu:" + asu);

                            break;
                        }
                    }
                }

            }


            @Override
            public void onServiceStateChanged(ServiceState serviceState) {
                super.onServiceStateChanged(serviceState);
            }
        };


        // 开始监听
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        } else {
            Log.d(TAG, "get4GNetDBM: TelephonyManager为空，获取手机状态信息失败，无法开启监听");
        }

        @SuppressLint("MissingPermission")
        List<CellInfo> cellInfoList = mTelephonyManager.getAllCellInfo();
        if (cellInfoList != null) {
            for (CellInfo cellInfo : cellInfoList) {
                if (cellInfo instanceof CellInfoLte) {
                    //cast to CellInfoLte and call all the CellInfoLte methods you need
                    int dbm = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();
                    int asu = ((CellInfoLte) cellInfo).getCellSignalStrength().getAsuLevel();

                    String provider = getNetworkOperatorName();
                    String celluarType = getCellularType();
                    // String logTxt = String.format("%s %s dbm:%d asu:%d \n", provider, celluarType, dbm, asu);
                    //logFile(logTxt, true);
                    //Log.i(TAG, provider + "   " + celluarType + " dbm:" + dbm + " asu:" + asu);

                    break;
                }
            }
        }

    }


    /**
     * 获取网络运营商名称
     * <p>中国移动、如中国联通、中国电信</p>
     *
     * @return 运营商名称
     */

    private String getNetworkOperatorName() {
        String opeType = "无SIM卡";
        // No sim
        if (!hasSim()) {
            return opeType;
        }

        String operator = mTelephonyManager.getSimOperator();
        if ("46001".equals(operator) || "46006".equals(operator) || "46009".equals(operator)) {
            opeType = "中国联通";
        } else if ("46000".equals(operator) || "46002".equals(operator) || "46004".equals(operator) || "46007".equals(operator)) {
            opeType = "中国移动";

        } else if ("46003".equals(operator) || "46005".equals(operator) || "46011".equals(operator)) {
            opeType = "中国电信";
        } else {
            opeType = "unknown";
        }
        return opeType;
    }

    /**
     * 检查手机是否有sim卡
     */
    public static boolean hasSIM;

    private boolean hasSim() {

        String operator = mTelephonyManager.getSimOperator();
        if (TextUtils.isEmpty(operator)) {
            hasSIM = false;
            return false;
        }
        hasSIM = true;
        return true;
    }


    /*
     * 获取当前网络类型 2G  3G 4G 5G
     * */
   // @SuppressLint("MissingPermission")
    public String getCellularType() {

        String cellularType = "4G";
        int nSubType = mTelephonyManager.getNetworkType();
        if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
                || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                || nSubType == TelephonyManager.NETWORK_TYPE_GSM) {
            cellularType = "2G";
        } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0) {
            cellularType = "3G";
        } else if (nSubType == TelephonyManager.NETWORK_TYPE_LTE) {
            cellularType = "4G";
        }
//        else if (nSubType == TelephonyManager.NETWORK_TYPE_NR) {
//            cellularType = "5G";
//        }
        else if (nSubType == TelephonyManager.NETWORK_TYPE_UNKNOWN) {

            cellularType = "   ";//unknow
        } else
            cellularType = String.valueOf(nSubType);

        return cellularType;
    }


    /**
     * 1、当信号大于等于 - 75d Bm时候，信号显示满格
     * 2、当信号大于等于 - 85d Bm时候，而小于 - 85d Bm时，信号显示4格
     * 3、当信号大于等于 - 95d Bm时候，而小于 - 95d Bm时，信号显示3格，不好捕捉到。
     * 4、当信号大于等于 - 105d Bm时候，而小于 - 105d Bm时，信号显示2格，不好捕捉到。
     * 5、当信号大于等于 - 115d Bm时候，而小于 - 115d Bm时，信号显示1格，不好捕捉到。
     *
     * @param dbm
     */
    private int setNetLevel(int dbm) {
        int level = -1;
        if (phoneNetListener != null) {
            if (dbm > DBM_1) {
                level = 5;
            } else if (DBM_2 < dbm && dbm < DBM_1) {
                level = 4;
            } else if (DBM_3 < dbm && dbm < DBM_2) {
                level = 3;
            } else if (DBM_4 < dbm && dbm < DBM_3) {
                level = 2;
            } else if (DBM_5 < dbm && dbm < DBM_4) {
                level = 1;
            } else {
                level = 0;
            }
        }
        return level;
    }


    /*
     * 判断当前移动网络开关是否打开了
     *
     * */
    public boolean isMobileDataEnabled() {

        try {
            Method getDataEnabled = mTelephonyManager.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getDataEnabled) {
                return (Boolean) getDataEnabled.invoke(mTelephonyManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}




