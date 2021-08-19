package com.android.launcher3.recentTaskUtils;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;



import java.util.List;

/**
 * @author Deson
 * @ClassName: AppOpUtils
 * @Description: APP操作工具类
 * @date 2018年11月7日 上午10:07:54
 */
public class AppOpUtils {

    public static boolean openApp(Context context, String pkg, String act) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName(pkg, act);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    static ActivityOptions activityOptions;

//    public static boolean openResolveInfo(Context context, LauncherConfigApps.ResolveInfo targetOpApp) {
//        try {
//            String pkgName = targetOpApp.getPkgName();
//            String className = targetOpApp.getClassName();
//
//            if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(className)) {
//                return false;
//            }
//            Intent intent = new Intent();
//
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setPackage(pkgName);
//            intent.setClassName(pkgName, className);
//            context.startActivity(intent);
//            // context.startActivity(intent, activityOptions.toBundle());
//
//            return true;
//        } catch (Exception e) {
//            Log.d("drag", "open app fail maybe target app is not installed: " + e.getMessage());
//            openAppByPkgName(context, targetOpApp.getPkgName());
//        }
//        return false;
//    }

    public static boolean openResolveInfoDirectly(Context context, String pkgName, String className) {
        try {

            if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(className)) {
                return false;
            }
            Intent intent = new Intent();

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(pkgName);
            intent.setClassName(pkgName, className);

            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.d("drag", "open app fail maybe target app is not installed: " + e.getMessage());
        }
        return false;
    }

    public static boolean openAppByClassName(Context context, String action, String pkgName, String className) {
        try {
            if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(className)) {
                return false;
            }
            Intent intent = new Intent();
            if (TextUtils.isEmpty(action)) {
                intent.setAction(action);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(pkgName);
            intent.setClassName(pkgName, className);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.d("drag", "open app fail maybe target app is not installed: " + e.getMessage());
        }
        return false;
    }

    public static boolean openAppByComponent(Context context, String action, String pkgName, String className) {
        try {
            if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(className)) {
                return false;
            }
            Intent intent = new Intent();
            if (TextUtils.isEmpty(action)) {
                intent.setAction(action);
            }
            ComponentName componentName = new ComponentName(pkgName, className);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(pkgName);
            intent.setComponent(componentName);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.d("drag", "open app fail maybe target app is not installed: " + e.getMessage());
        }
        return false;
    }


    /**
     * @param context 必须是activity的对象，否则不能成功
     * @Describe
     * @auther Deson
     * @d2016年3月18日下午6:39:23
     */
    public static void uninstallApk(Context context, String apkPackageName) {
        try {
            Uri uri = Uri.parse("package:" + apkPackageName);// 获取删除包名的URI
            Intent uninstallIntent = new Intent();
            uninstallIntent.setAction(Intent.ACTION_DELETE);// 设置我们要执行的卸载动作
            uninstallIntent.setData(uri);// 设置获取到的URI
            context.startActivity(uninstallIntent);
            Log.d("app", "正在卸载: " + apkPackageName);
        } catch (Exception e) {
        }
    }

    public static boolean openAppByPkgName(Context context, String pkgName) {
        Log.d("app", "open app by pkg name： " + pkgName);
        try {
            PackageManager pmManager = context.getPackageManager();
            String className = "";
            List<PackageInfo> installedApps = pmManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
            for (PackageInfo packageInfo : installedApps) {
                if (pkgName.equals(packageInfo.packageName)) {
                    Intent intent3rd = pmManager.getLaunchIntentForPackage(packageInfo.packageName);
                    if (intent3rd != null) {
                        className = intent3rd.getComponent().getClassName();
                    } else {
                        continue;
                    }

                    break;
                }
            }
            Log.d("app", "找到 class  name： " + className);
            openSrcApp(context, pkgName, className);

        } catch (Exception e) {
            Log.d("app", "异常产生了 " + e.getMessage());
        }

        return true;
    }


    public static boolean openSrcApp(Context context, String pkgName, String className) {
        try {
            if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(className)) {
                return false;
            }
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(pkgName);
            intent.setClassName(pkgName, className);
            context.startActivity(intent);
//            context.startActivityAsUser(intent,Usber);
            return true;
        } catch (Exception e) {
            Log.d("main", "open source  app fail maybe target app is not installed: " + e.getMessage());
        }
        return false;
    }

}
