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
package com.android.launcher3.allapps;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.android.launcher3.AppInfo;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.PromiseAppInfo;
import com.android.launcher3.leftnavigationCons;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.views.BaseLeftNavigation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A utility class to maintain the collection of all apps.
 */
public class AllAppsStore {

    private PackageUserKey mTempKey = new PackageUserKey(null, null);
    private final HashMap<ComponentKey, AppInfo> mComponentToAppMap = new HashMap<>();
    private final List<OnUpdateListener> mUpdateListeners = new ArrayList<>();
    private final ArrayList<ViewGroup> mIconContainers = new ArrayList<>();

    private boolean mDeferUpdates = false;
    private boolean mUpdatePending = false;

    public Collection<AppInfo> getApps() {
       // Log.d("AllappsStore", "getApps");
        return mComponentToAppMap.values();
    }

    /**
     * Sets the current set of apps.
     */
    public void setApps(List<AppInfo> apps) {
        Log.d("AllappsStore", "setApps size: " + apps.size());
        mComponentToAppMap.clear();
        appInfoList.clear();//add by xiaoyu 2021 08 13
        addOrUpdateApps(apps);
    }

    public AppInfo getApp(ComponentKey key) {
        return mComponentToAppMap.get(key);
    }

    public void setDeferUpdates(boolean deferUpdates) {
        if (mDeferUpdates != deferUpdates) {
            mDeferUpdates = deferUpdates;

            if (!mDeferUpdates && mUpdatePending) {
                notifyUpdate();
                mUpdatePending = false;
            }
        }
    }

 //   public static List<String> userDefIcon = new ArrayList<>();//需要排序名单

//    {
//        userDefIcon.add("com.android.settings");
//        userDefIcon.add("com.suding.speedplay");
//        userDefIcon.add("com.carsyso.bluetooth");
//        userDefIcon.add("com.suding.onstepbtmusic");
//        userDefIcon.add("com.suding.apkinstaller");
//        userDefIcon.add("com.spotify.music");
//        userDefIcon.add("org.codeaurora.gallery");
//        userDefIcon.add("com.cyanogenmod.filemanager");
//        userDefIcon.add("com.netflix.mediaclient");
//        userDefIcon.add("com.waze");
//        userDefIcon.add("com.google.android.inputmethod.latin");
//        userDefIcon.add("com.google.android.apps.maps");
//        userDefIcon.add("com.google.android.apps.youtube.music");
//    }

    /**
     * Adds or updates existing apps in the list
     */
    private final List<AppInfo> appInfoList = new ArrayList<>();

    public void addOrUpdateApps(List<AppInfo> apps) {
        Log.d("AllappsStore", "addOrUpdateApps: apps:" + apps.size());
        appInfoList.addAll(apps);//取图标使用(左侧任务栏 所有app)，不需要分顺序
        for (AppInfo app : apps) {
            mComponentToAppMap.put(app.toComponentKey(), app);
        }

        notifyUpdate();
        //app图标信息全部准备就绪了，左侧开始设置图标  2021 08 13
        BaseLeftNavigation.get().initDefTaskIcon();//开机设置默认的图标
    }


    //取出实际图标，包括被替换过的
    public Bitmap getCurrentApkIcon(String pkg) {
        if (appInfoList != null && appInfoList.size() > 0) {
            Log.d("AllappsStore", "内存app信息数：" + appInfoList.size() + "  mComponentToAppMap:" + mComponentToAppMap.size());
            for (AppInfo appInfo : appInfoList) {
                if (appInfo.pkgName.equals(pkg)) {
                    return appInfo.iconBitmap;
                }
            }
        } else {
            Log.d("AllappsStore", "app信息清空了 mComponentToAppMap size:" + mComponentToAppMap.size());
        }

        return null;
    }

    //可以获取到当前所有app的信息，包含图标，pkg，title等数据
    public List<AppInfo> getAppInfoList() {
        return appInfoList;
    }

    /**
     * Removes some apps from the list.
     */
    public void removeApps(List<AppInfo> apps) {
        for (AppInfo app : apps) {
            Log.d("AllappsStore", "removeApps :" + app.pkgName);
            mComponentToAppMap.remove(app.toComponentKey());
            appInfoList.remove(app);
           // allAppParseCach.remove(app);
        }
        notifyUpdate();
    }


    private void notifyUpdate() {
        if (mDeferUpdates) {
            mUpdatePending = true;
            return;
        }
        int count = mUpdateListeners.size();
        for (int i = 0; i < count; i++) {
            mUpdateListeners.get(i).onAppsUpdated();
        }
    }

    public void addUpdateListener(OnUpdateListener listener) {
        mUpdateListeners.add(listener);
    }

    public void removeUpdateListener(OnUpdateListener listener) {
        mUpdateListeners.remove(listener);
    }

    public void registerIconContainer(ViewGroup container) {
        if (container != null) {
            mIconContainers.add(container);
        }
    }

    public void unregisterIconContainer(ViewGroup container) {
        mIconContainers.remove(container);
    }

    public void updateIconBadges(Set<PackageUserKey> updatedBadges) {
        updateAllIcons((child) -> {
            if (child.getTag() instanceof ItemInfo) {
                ItemInfo info = (ItemInfo) child.getTag();
                if (mTempKey.updateFromItemInfo(info) && updatedBadges.contains(mTempKey)) {
                    child.applyBadgeState(info, true /* animate */);
                }
            }
        });
    }

    public void updatePromiseAppProgress(PromiseAppInfo app) {
        updateAllIcons((child) -> {
            if (child.getTag() == app) {
                child.applyProgressLevel(app.level);
            }
        });
    }

    private void updateAllIcons(IconAction action) {
        for (int i = mIconContainers.size() - 1; i >= 0; i--) {
            ViewGroup parent = mIconContainers.get(i);
            int childCount = parent.getChildCount();

            for (int j = 0; j < childCount; j++) {
                View child = parent.getChildAt(j);
                if (child instanceof BubbleTextView) {
                    action.apply((BubbleTextView) child);
                }
            }
        }
    }

    public interface OnUpdateListener {
        void onAppsUpdated();
    }

    public interface IconAction {
        void apply(BubbleTextView icon);
    }
}
