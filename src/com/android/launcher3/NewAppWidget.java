package com.android.launcher3;

import android.app.Instrumentation;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RemoteViews;

import com.android.launcher3.MediaSession.MusicService;

/**
 * 创建一个音乐播放widget 2021 08 10
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link NewAppWidgetConfigureActivity NewAppWidgetConfigureActivity}
 */
public class NewAppWidget extends AppWidgetProvider implements MusicService.updateMusicInfo {

    private static String TAG = "newWidget";
    private static final String ClickPre = "btnPre";
    private static final String ClickPlay = "btnPlay";
    private static final String ClickNext = "btnNext";
    private static AudioManager audioManager;
    private static final String WIDGET_MUSIC_INFO = "android.appwidget.action.MUSIC_INFO";
    private static AppWidgetManager manager;
    private static Context mContext;
    private static int appid;

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId, CharSequence widgetText, boolean isactivity) {
        manager = appWidgetManager;
        mContext = context;
        appid = appWidgetId;
        // CharSequence widgetText = NewAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        if (isactivity) {
            views.setImageViewResource(R.id.img_Btn_playPause, R.drawable.pause_lzy);
        } else {
            views.setImageViewResource(R.id.img_Btn_playPause, R.drawable.play_lzy);
        }
        //------设置按钮点击事件------------
        views.setOnClickPendingIntent(R.id.imgBtn_pre_one, getPendingSelfIntent(context, ClickPre));
        views.setOnClickPendingIntent(R.id.img_Btn_playPause, getPendingSelfIntent(context, ClickPlay));
        views.setOnClickPendingIntent(R.id.imgBtn_next_one, getPendingSelfIntent(context, ClickNext));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        Log.d(TAG, "onUpdate>>>>>>>isMusicActive:" + audioManager.isMusicActive());
        MusicService.setMusicInfoChangeListener(this::onSetMusicName);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, "", audioManager.isMusicActive());
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
//        for (int appWidgetId : appWidgetIds) {
//            NewAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
//        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(TAG, "onEnabled------");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(TAG, "onDisabled");
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    String musicinfo;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        switch (intent.getAction()) {
            case ClickPre:
                Log.d(TAG, "onReceive: widget上一曲");
                simulateKeystroke(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                break;
            case ClickPlay:
                simulateKeystroke(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                break;
            case ClickNext:
                Log.d(TAG, "onReceive: widget下一曲");
                simulateKeystroke(KeyEvent.KEYCODE_MEDIA_NEXT);
                break;

        }
    }


    public static void simulateKeystroke(final int code) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(code);
                    inst.setInTouchMode(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onSetMusicName(String name) {
        musicinfo = name;
        if (audioManager == null) {
            audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            Log.d(TAG, "onSetMusicName   audioManager重新初始化了");
        }
        SystemClock.sleep(500);
        Log.d(TAG, " 收到歌曲信息：" + musicinfo + "   status:" + audioManager.isMusicActive());
        updateAppWidget(mContext, manager, appid, name, audioManager.isMusicActive());
    }
    /*
    *  AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            watchWidget = new ComponentName(context, Widget.class);

            remoteViews.setTextViewText(R.id.sync_button, "TESTING");

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    *
    * */
}