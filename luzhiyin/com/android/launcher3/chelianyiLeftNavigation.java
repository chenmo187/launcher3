package com.android.launcher3;
/*
Created by xiaoyu on 2021/8/20

Describe:

*/


import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;

import com.android.launcher3.views.BaseLeftNavigation;

public class chelianyiLeftNavigation extends BaseLeftNavigation {
    private ConstraintLayout layout;

    @Override
    public int getRecentTaskNum() {
        return 3;

    }


    @Override
    public View getLayout() {
        layout = (ConstraintLayout) LayoutInflater.from(mContext).inflate(R.layout.chelianyi_left_navigation, mContext.getRootConstrain(), false);
        addLayout();
        return layout;
    }


    //添加到导航栏里
    private void addLayout() {
        mContext.getRootConstrain().addView(layout);//ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.MATCH_CONSTRAINT
        ConstraintSet set = new ConstraintSet();
        set.clone(mContext.getRootConstrain());
        set.constrainWidth(layout.getId(), ConstraintSet.MATCH_CONSTRAINT);
        set.constrainHeight(layout.getId(), ConstraintSet.MATCH_CONSTRAINT);
        set.connect(layout.getId(), ConstraintSet.TOP, mContext.getRootConstrain().getId(), ConstraintSet.TOP);
        set.connect(layout.getId(), ConstraintSet.BOTTOM, mContext.getRootConstrain().getId(), ConstraintSet.BOTTOM);
        set.connect(layout.getId(), ConstraintSet.START, mContext.getRootConstrain().getId(), ConstraintSet.START);
        set.connect(layout.getId(), ConstraintSet.END, mContext.getRootConstrain().getId(), ConstraintSet.END);
        TransitionManager.beginDelayedTransition(mContext.getRootConstrain());
        set.applyTo(mContext.getRootConstrain());
    }


    @Override
    public void WifiConnected(int rssi) {
        super.WifiConnected(rssi);
    }

    @Override
    public void WifiDisable() {
        super.WifiDisable();
    }

    @Override
    public void WifiEnable(int rssi) {
        super.WifiEnable(rssi);
    }

    @Override
    public void initDefTaskIcon() {
        super.initDefTaskIcon();
    }

}
