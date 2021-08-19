package com.android.launcher3.MediaSession;
/*
Created by xiaoyu on 2021/8/11

Describe:

*/


import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.SpannableString;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

public class MusicService extends NotificationListenerService {
    private static String TAG = "MusicService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }


    @Override
    public void onListenerConnected() {
        //super.onListenerConnected();
        Log.d(TAG, "onListenerConnected");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // super.onNotificationPosted(sbn);
        Bundle extras = sbn.getNotification().extras;
        Notification notification = sbn.getNotification();
        String packageName = sbn.getPackageName();
        Object ticktext = notification.tickerText;
        if (extras != null) {
            Object titles = extras.get("android.title");
            String extratitle = extras.getString(Notification.EXTRA_TITLE);
            Object msgText = extras.getCharSequence(Notification.EXTRA_TEXT);//可能是短信
            Object text = extras.get("android.text");
            Log.d(TAG, "收到歌曲信息: ticktext:" + ticktext + "   titles:" +
                    titles + "  extraTitle:" + extratitle + "  msgText:" + msgText +
                    "  text:" + text + "   packageName:" + packageName);
            if (msgText instanceof SpannableString) {
                Log.d(TAG, "收到短信 ...." + ((SpannableString) msgText).subSequence(0, ((SpannableString) msgText).length()));

            }

            if (listener!=null){
                listener.onSetMusicName((String) ticktext);
            }
//            Intent intent = new Intent("android.appwidget.action.MUSIC_INFO");
//            if (ticktext != null) {
//                intent.putExtra("WIDGET_MUSIC", (String) ticktext);
//                sendBroadcast(intent);
//            }


        }


    }

    private static updateMusicInfo listener;

    public static void setMusicInfoChangeListener(updateMusicInfo info) {
        listener = info;
    }

    public interface updateMusicInfo {
        void onSetMusicName(String name);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //super.onNotificationRemoved(sbn);
        Log.d(TAG, "onNotificationRemoved");
    }
}
