/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.launcher3.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;

import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.ShortcutConfigActivityInfo.ShortcutConfigActivityInfoVL;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.PackageUserKey;

import java.util.ArrayList;
import java.util.List;

public class LauncherAppsCompatVL extends LauncherAppsCompat {

    protected final LauncherApps mLauncherApps;
    protected final Context mContext;

    private final String PKG_SETTINGS = "com.android.settings";
    private final String PKG_SPEEDPLAY = "com.suding.speedplay";
    private final String PKG_BTPHONE = "com.carsyso.bluetooth";
    private final String PKG_BTMUSIC = "com.suding.onstepbtmusic";
    private final String PKG_APKINSTALLER = "com.suding.apkinstaller";
    private final String PKG_SPOTIFY = "com.spotify.music";
    private final String PKG_GALLERY = "org.codeaurora.gallery";
    private final String PKG_FILEMANAGER = "com.cyanogenmod.filemanager";
    private final String PKG_NETFLIX = "com.netflix.mediaclient";
    private final String PKG_WAZE = "com.waze";
    private final String PKG_GBOARD = "com.google.android.inputmethod.latin";
    private final String PKG_GOOGLE_MAPS = "com.google.android.apps.maps";
    private final String PKG_YOUTUBE_MUSIC = "com.google.android.apps.youtube.music";
    ArrayList<String> blackList = new ArrayList<>();

    ArrayList<String> quickAppList = new ArrayList<>();

    {
        quickAppList.add(PKG_SETTINGS);
        quickAppList.add(PKG_SPEEDPLAY);
        quickAppList.add(PKG_BTPHONE);
        quickAppList.add(PKG_BTMUSIC);
        quickAppList.add(PKG_SPOTIFY);
        quickAppList.add(PKG_GOOGLE_MAPS);
//        quickAppList.add(PKG_WAZE);
//        quickAppList.add(PKG_NETFLIX);
//        quickAppList.add(PKG_GBOARD);
//        quickAppList.add(PKG_YOUTUBE_MUSIC);
//        quickAppList.add(PKG_GALLERY);
//        quickAppList.add(PKG_FILEMANAGER);
        //------------------黑名单 禁止显示的app----------
        blackList.add("org.codeaurora.dialer");//电话app
    }

    List<LauncherActivityInfo> quickInfo = new ArrayList<>();
    List<LauncherActivityInfo> otherInfo = new ArrayList<>();
    List<LauncherActivityInfo> allInfo = new ArrayList<>();


    private final ArrayMap<OnAppsChangedCallbackCompat, WrappedCallback> mCallbacks =
            new ArrayMap<>();

    LauncherAppsCompatVL(Context context) {
        mContext = context;
        mLauncherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
    }

    @Override
    public List<LauncherActivityInfo> getActivityList(String packageName, UserHandle user) {
        //---------------对桌面app进行排序，将指定的app显示靠前----2021 08 19 add by xiaoyu---------
        quickInfo.clear();
        otherInfo.clear();
        allInfo.clear();
        List<LauncherActivityInfo> activityList = mLauncherApps.getActivityList(packageName, user);
        for (LauncherActivityInfo info : activityList) {
            if (blackList.contains(info.getApplicationInfo().packageName)){
                continue;
            }
            if (quickAppList.contains(info.getApplicationInfo().packageName)) {
                quickInfo.add(info);
            } else {
                otherInfo.add(info);
            }
        }
        allInfo.addAll(quickInfo);
        allInfo.addAll(otherInfo);
        Log.d("LauncherApps", " quickInfo size:" + quickInfo.size() +
                "  otherInfo size:" + otherInfo.size() + " totle size:" + allInfo.size());
        return allInfo;
        //-----------------------------------end----------------------------------------
        // return mLauncherApps.getActivityList(packageName, user);
    }

    @Override
    public LauncherActivityInfo resolveActivity(Intent intent, UserHandle user) {
        return mLauncherApps.resolveActivity(intent, user);
    }

    @Override
    public void startActivityForProfile(ComponentName component, UserHandle user,
                                        Rect sourceBounds, Bundle opts) {
        mLauncherApps.startMainActivity(component, user, sourceBounds, opts);
    }

    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags, UserHandle user) {
        final boolean isPrimaryUser = Process.myUserHandle().equals(user);
        if (!isPrimaryUser && (flags == 0)) {
            // We are looking for an installed app on a secondary profile. Prior to O, the only
            // entry point for work profiles is through the LauncherActivity.
            List<LauncherActivityInfo> activityList =
                    mLauncherApps.getActivityList(packageName, user);
            return activityList.size() > 0 ? activityList.get(0).getApplicationInfo() : null;
        }
        try {
            ApplicationInfo info =
                    mContext.getPackageManager().getApplicationInfo(packageName, flags);
            // There is no way to check if the app is installed for managed profile. But for
            // primary profile, we can still have this check.
            if (isPrimaryUser && ((info.flags & ApplicationInfo.FLAG_INSTALLED) == 0)
                    || !info.enabled) {
                return null;
            }
            return info;
        } catch (PackageManager.NameNotFoundException e) {
            // Package not found
            return null;
        }
    }

    @Override
    public void showAppDetailsForProfile(ComponentName component, UserHandle user,
                                         Rect sourceBounds, Bundle opts) {
        mLauncherApps.startAppDetailsActivity(component, user, sourceBounds, opts);
    }

    @Override
    public void addOnAppsChangedCallback(LauncherAppsCompat.OnAppsChangedCallbackCompat callback) {
        WrappedCallback wrappedCallback = new WrappedCallback(callback);
        synchronized (mCallbacks) {
            mCallbacks.put(callback, wrappedCallback);
        }
        mLauncherApps.registerCallback(wrappedCallback);
    }

    @Override
    public void removeOnAppsChangedCallback(OnAppsChangedCallbackCompat callback) {
        final WrappedCallback wrappedCallback;
        synchronized (mCallbacks) {
            wrappedCallback = mCallbacks.remove(callback);
        }
        if (wrappedCallback != null) {
            mLauncherApps.unregisterCallback(wrappedCallback);
        }
    }

    @Override
    public boolean isPackageEnabledForProfile(String packageName, UserHandle user) {
        return mLauncherApps.isPackageEnabled(packageName, user);
    }

    @Override
    public boolean isActivityEnabledForProfile(ComponentName component, UserHandle user) {
        return mLauncherApps.isActivityEnabled(component, user);
    }

    private static class WrappedCallback extends LauncherApps.Callback {
        private final LauncherAppsCompat.OnAppsChangedCallbackCompat mCallback;

        public WrappedCallback(LauncherAppsCompat.OnAppsChangedCallbackCompat callback) {
            mCallback = callback;
        }

        @Override
        public void onPackageRemoved(String packageName, UserHandle user) {
            mCallback.onPackageRemoved(packageName, user);
        }

        @Override
        public void onPackageAdded(String packageName, UserHandle user) {
            mCallback.onPackageAdded(packageName, user);
        }

        @Override
        public void onPackageChanged(String packageName, UserHandle user) {
            mCallback.onPackageChanged(packageName, user);
        }

        @Override
        public void onPackagesAvailable(String[] packageNames, UserHandle user, boolean replacing) {
            mCallback.onPackagesAvailable(packageNames, user, replacing);
        }

        @Override
        public void onPackagesUnavailable(String[] packageNames, UserHandle user,
                                          boolean replacing) {
            mCallback.onPackagesUnavailable(packageNames, user, replacing);
        }

        @Override
        public void onPackagesSuspended(String[] packageNames, UserHandle user) {
            mCallback.onPackagesSuspended(packageNames, user);
        }

        @Override
        public void onPackagesUnsuspended(String[] packageNames, UserHandle user) {
            mCallback.onPackagesUnsuspended(packageNames, user);
        }

        @Override
        public void onShortcutsChanged(@NonNull String packageName,
                                       @NonNull List<ShortcutInfo> shortcuts,
                                       @NonNull UserHandle user) {
            List<ShortcutInfoCompat> shortcutInfoCompats = new ArrayList<>(shortcuts.size());
            for (ShortcutInfo shortcutInfo : shortcuts) {
                shortcutInfoCompats.add(new ShortcutInfoCompat(shortcutInfo));
            }

            mCallback.onShortcutsChanged(packageName, shortcutInfoCompats, user);
        }
    }

    @Override
    public List<ShortcutConfigActivityInfo> getCustomShortcutActivityList(
            @Nullable PackageUserKey packageUser) {
        List<ShortcutConfigActivityInfo> result = new ArrayList<>();
        if (packageUser != null && !packageUser.mUser.equals(Process.myUserHandle())) {
            return result;
        }
        PackageManager pm = mContext.getPackageManager();
        for (ResolveInfo info :
                pm.queryIntentActivities(new Intent(Intent.ACTION_CREATE_SHORTCUT), 0)) {
            if (packageUser == null || packageUser.mPackageName
                    .equals(info.activityInfo.packageName)) {
                result.add(new ShortcutConfigActivityInfoVL(info.activityInfo, pm));
            }
        }
        return result;
    }
}

