/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;

import com.android.launcher3.graphics.IconShapeOverride;
import com.android.launcher3.logging.FileLog;

import org.texustek.mirror.support.ams.AmsHelperManager;
import org.texustek.mirror.support.tool.MirrorSystemProperties;

/**
 * Utility class to handle one time initializations of the main process
 */
public class MainProcessInitializer {

    public static void initialize(Context context) {
        checkScreenReboot(context);

        Utilities.getOverrideObject(
                MainProcessInitializer.class, context, R.string.main_process_initializer_class)
                .init(context);
    }


    private static void checkScreenReboot(Context context) {
        String height = MirrorSystemProperties.getSystemProperty("persist.panel.height", "0");
        String width = MirrorSystemProperties.getSystemProperty("persist.panel.width", "0");

        String screenHeight = MirrorSystemProperties.getSystemProperty("persist.panel.height.launcher", "1");
        String screenWidth = MirrorSystemProperties.getSystemProperty("persist.panel.width.launcher", "1");


        //查询重启次数
        String rebootNumber = MirrorSystemProperties.getSystemProperty("persist.panel.reboot.launcher.number", "0");
        Log.d("Maininit", "initialize: read width:" + width + "  read height:"
                + height + " save width:" + screenWidth +
                "  save height:" + screenHeight + "   rebootNum:" + rebootNumber);

        if (rebootNumber.equals("1")) {
            MirrorSystemProperties.setSystemProperty("persist.panel.height.launcher", height);
            MirrorSystemProperties.setSystemProperty("persist.panel.width.launcher", width);

            MirrorSystemProperties.setSystemProperty("persist.panel.reboot.launcher.number", "0");

            Log.d("Maininit", "initialize: 开机 分辨率变化，清空缓存");
            AmsHelperManager.getInstance().clearApplicationCacheData(context, context.getPackageName());
            AmsHelperManager.getInstance().clearApplicationUserData(context, context.getPackageName());

            android.os.Process.killProcess(android.os.Process.myPid());
        }

        if (!height.equals(screenHeight) || !width.equals(screenWidth)) {
            //屏幕变化。清理数据库
            Log.d("Maininit", "checkScreenReboot: 等待开机后重新适配屏幕");
            MirrorSystemProperties.setSystemProperty("persist.panel.reboot.launcher.number", "1");

        }
    }

    protected void init(Context context) {
        FileLog.setDir(context.getApplicationContext().getFilesDir());
        IconShapeOverride.apply(context);
        SessionCommitReceiver.applyDefaultUserPrefs(context);

//        int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//        NewAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
    }
}
