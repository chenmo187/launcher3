package com.android.launcher3.recentTaskUtils;
/*
Created by xiaoyu on 2021/1/27

Describe:bitmap的效果处理

*/


import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.android.launcher3.Launcher;

import java.util.ArrayList;
import java.util.List;

public class BitmapUtil {
    private static String TAG = "BitmapUtil";

    //定义需要替换图标的apk容器
    public static List<String> userDefauatIconCach = new ArrayList();

    public static Bitmap zoomBitmap(Bitmap bitmap) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 55, 55, false);
        //这个写法有锯齿
        return scaledBitmap;
    }


    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Log.d("zoomImage", "图标width :" + width + "   图标height:" + height);
        Log.d("zoomImage", "newWidth :" + newWidth + "   newHeight:" + newHeight);
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }


    /**
     * 代码：功能性代码；非功能性代码。图像绘制成圆形
     *
     * @param source
     * @return
     */
    public static Bitmap circleBitmap(Bitmap source) {
        //获取Bitmap的宽度
        int width = source.getWidth();
        //以Bitmap的宽度值作为新的bitmap的宽高值。
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        //以此bitmap为基准，创建一个画布
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //在画布上画一个圆
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);

        //设置图片相交情况下的处理方式
        //setXfermode：设置当绘制的图像出现相交情况时候的处理方式的,它包含的常用模式有：
        //PorterDuff.Mode.SRC_IN 取两层图像交集部分,只显示上层图像
        //PorterDuff.Mode.DST_IN 取两层图像交集部分,只显示下层图像
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //在画布上绘制bitmap
        canvas.drawBitmap(source, 0, 0, paint);
        return bitmap;

    }

    public static Bitmap createLauncherIcon(Bitmap appIcon, Bitmap backgroundIcon) {
        int width = backgroundIcon.getWidth();
        int height = backgroundIcon.getHeight();
        //Log.d("BitmapUtil", "背景宽高: " + width + "  " + height);
        int bgWidth = backgroundIcon.getWidth();
        int bgHeight = backgroundIcon.getHeight();
        //create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        //draw bg into
        cv.drawBitmap(backgroundIcon, 0, 0, null);//在 0，0坐标开始画入bg
        //draw fg into
        cv.drawBitmap(appIcon, 0, 0, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
        //save all clip
        cv.save();//保存
        //store
        cv.restore();//存储
        return newbmp;
    }


    /*
     * 将drawable转换为bitmap位图
     * */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        //System.out.println("Drawable转Bitmap");
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }


    //根据最近任务的包名取出图标
    private static Bitmap createIconBitmap(String pkgName, PackageManager mPackageManager) {
        try {
            Drawable drawable = mPackageManager.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES).applicationInfo.loadIcon(mPackageManager);
            Bitmap icon = drawableToBitmap(drawable);
            return icon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "not find this application  icon!!!!!! ");
        }
        return null;
    }

    //把apk图标压缩到指定尺寸
    public static Bitmap decodeBitmap(String pkgName, PackageManager mPackageManager) {
        Bitmap iconBitmap = createIconBitmap(pkgName, mPackageManager);
        if (iconBitmap != null) {
            Bitmap zoomImage = zoomImage(iconBitmap,
                    DensityUtil.dip2px(49),
                    DensityUtil.dip2px(49));
            return zoomImage;
        }

        return null;
    }

    //自定义压缩尺寸
    public static Bitmap decodeBitmapInSize(String pkgName, PackageManager mPackageManager, int dpWidth, int dpHeight) {
        Bitmap iconBitmap = createIconBitmap(pkgName, mPackageManager);
        if (iconBitmap != null) {
            Bitmap zoomImage = zoomImage(iconBitmap,
                    DensityUtil.dip2px(dpWidth),
                    DensityUtil.dip2px(dpHeight));
            return zoomImage;
        }

        return null;
    }

    //获取当前实际显示在屏幕的图标，非系统图标
    public static Bitmap decodeBitmapInSize(String pkgName, Launcher context, int dpWidth, int dpHeight) {
        Bitmap iconBitmap = context.getAppsView().getAppsStore().getCurrentApkIcon(pkgName);
        if (iconBitmap != null) {
            Bitmap zoomImage = zoomImage(iconBitmap,
                    DensityUtil.dip2px(dpWidth),
                    DensityUtil.dip2px(dpHeight));
            return zoomImage;
        }
        Log.d(TAG, "decodeBitmapInSize: 图标不存在");
        return null;
    }
}

