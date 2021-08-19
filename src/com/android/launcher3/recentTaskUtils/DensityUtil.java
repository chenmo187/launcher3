package com.android.launcher3.recentTaskUtils;
/*
Created by xiaoyu on 2020/11/23

Describe: dip 与px之间的转换工具

*/


import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;



public class DensityUtil {
    //把dp值转换为像素
    private static float density;
    //开机启动读取设备的density值并保存，全局使用
    public static void setDeviceDensity(float deviceDensity){
        density = deviceDensity;
        Log.d("initLauncher", "开机设置统一density: "+density);
    }
    public static int dip2px(float dpValue) {
        //float density = getApplicationContext().getResources().getDisplayMetrics().density;
        int px = (int) (dpValue * density + 0.5f);
         //Log.d("zoomImage", "dip2px 当前设备屏幕的density: " + density + "  px值：" + px);
        return px;
    }

    //把像素转换为dp值
    public static int px2dip(float pxValue) {
        //float density = context.getResources().getDisplayMetrics().density;
        int dp = (int) (pxValue / density + 0.5f);
        //Log.d("DensityUtil", "px2dip 当前设备屏幕的density: " + density + "  dp值：" + dp);
        return dp;
    }


    //适配屏幕density
    private static final float WIDTH = 600;//参考设备的宽，单位是dp 320 / 2 = 160
    private static float appDensity;//表示屏幕密度
    private static float appScaleDensity; //字体缩放比例，默认appDensity

    public static void setDensity(final Application context, Activity activity) {
        //获取当前app的屏幕显示信息
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
       // DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (appDensity == 0) {
            //初始化赋值操作
            appDensity = displayMetrics.density;
            appScaleDensity = displayMetrics.scaledDensity;

            //添加字体变化监听回调
            context.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    //字体发生更改，重新对scaleDensity进行赋值
                    if (newConfig != null && newConfig.fontScale > 0) {
                        appScaleDensity = context.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        float targetDensity = 1.0f;
        Configuration mConfiguration = activity.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            targetDensity = displayMetrics.heightPixels / WIDTH; //设计稿高度600px
            // Log.d("DensityUtil", "当前横屏  targetDensity:" + targetDensity + "  屏幕宽:" + displayMetrics.widthPixels + "   高:" + displayMetrics.heightPixels);
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            float widthDensity = displayMetrics.widthPixels / 600f;
            float heightDensity = displayMetrics.heightPixels / 1024f;
            targetDensity = Math.min(widthDensity, heightDensity);
            // Log.d("DensityUtil", "当前竖屏  targetDensity:" + targetDensity + "  屏幕宽:" + displayMetrics.widthPixels + "   高:" + displayMetrics.heightPixels);
        }

        //计算目标值density, scaleDensity, densityDpi
        //targetDensity = displayMetrics.widthPixels / WIDTH; // 1080 / 360 = 3.0
        float targetScaleDensity = targetDensity * (appScaleDensity / appDensity);
        int targetDensityDpi = (int) (targetDensity * 160);
        Log.d("DensityUtil",
                "  设备density:" + appDensity +
                        " 设备scaleDensity:" + appScaleDensity +
                        "   targetDensity:" + targetDensity +
                        "   targetScaleDensity:" + targetScaleDensity +
                        "   targetDensityDpi:" + targetDensityDpi +
                        "   设备宽:" + displayMetrics.widthPixels +
                        "   设备高:" + displayMetrics.heightPixels);
        //替换Activity的density, scaleDensity, densityDpi
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        dm.density = targetDensity;
        dm.scaledDensity = targetScaleDensity;
        dm.densityDpi = targetDensityDpi;
    }




    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
