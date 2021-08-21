package com.android.launcher3.recentTaskUtils;

import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;


import com.android.launcher3.AppInfo;
import com.android.launcher3.R;

import java.util.List;

/**
 * @author Deson
 * @ClassName: AllAppsAdapter
 * @Description:所有应用adapter
 * @date 2019年12月23日 下午8:17:03
 */
public class AllAppsAdapter extends RecyclerView.Adapter<AllAppsViewHolder> {

    private Context context;

    private List<AppInfo> allApps;

    private LayoutInflater mLayoutInflater;

    private PackageManager mPackageManager;

    public void notifyAppData(List<AppInfo> allApps) {
        allApps.clear();
        allApps.addAll(allApps);
        notifyDataSetChanged();
    }

    public AllAppsAdapter(Context context, List<AppInfo> allApps) {
        this.allApps = allApps;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        mPackageManager = context.getPackageManager();

    }

    public static int currentFocusedPosition = -1;//当前图标焦点选中的下标
    public static int nextFocusIndex = -1;//下一个焦点的位置
    public static int lastFocusIndex = -1;//上一个焦点位置

    public void setNex2LastFocusIndex() {
        if (allApps == null || allApps.size() == 0) {
            Log.d("ATKEY", "setNex2LastFocusIndex: list null please check");
            return;
        }
        if (currentFocusedPosition < allApps.size()) {
            if (currentFocusedPosition == allApps.size() - 1) {
                //当前下标已经是最后一个，没有下一个了
                nextFocusIndex = -1;//最大值 到最后一个了 list.size-1;
            } else {
                nextFocusIndex = currentFocusedPosition + 1;
            }

            //当前选中位置的上一个
            if (currentFocusedPosition == 0) {
                lastFocusIndex = -1;//当前选中的是第一个，不存在上一个了
            } else if (currentFocusedPosition > 0) {
                lastFocusIndex = currentFocusedPosition - 1;
            }

            Log.d("ATKEY", "setNex2LastFocusIndex: All apk pager " +
                    " 上一个位置:" + lastFocusIndex +
                    " 当前:" + currentFocusedPosition +
                    " 下一个位置：" + nextFocusIndex);
        }

    }

    public void setFocusedAt(int position) {

    }

    @Override
    public int getItemCount() {
        return allApps.size();
    }

    public static boolean isAllAppFocused = false;//标记已经选中了"所有应用"中的图标 2021 05 12

    @Override
    public void onBindViewHolder(final AllAppsViewHolder vh, int position) {
        AppInfo info = allApps.get(position);
       // Drawable drawable = info.getApplicationInfo().loadIcon(mPackageManager);
       // Bitmap icon = BitmapUtil.drawableToBitmap(drawable);
        vh.ivIcon.setImageBitmap(info.iconBitmap);
        vh.tvAppName.setText(String.format("%s", allApps.get(position).title));
        vh.ivIcon.setOnClickListener(new ItemClickListener(position));
        vh.ivIcon.setOnLongClickListener(new ItemLongClickListener(position));


        //设置hover
        vh.ivIcon.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        // vh.ivIcon.setForeground(context.getResources().getDrawable(R.color.text_bg));
                        vh.ivIcon.setForeground(ContextCompat.getDrawable(context, R.drawable.all_app_icon_hove_selector));
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        //vh.ivIcon.setForeground(context.getResources().getDrawable(R.color.touming));
                        vh.ivIcon.setForeground(ContextCompat.getDrawable(context, R.color.touming));
                        break;
                }
                return false;
            }
        });

        //设置飞鼠上下左右建选择颜色
        vh.ivIcon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    isAllAppFocused = true;
                    currentFocusedPosition = vh.getAdapterPosition();
                    setNex2LastFocusIndex();//找出上一个和下一个的位置 2021 05 12
                    vh.ivIcon.setForeground(ContextCompat.getDrawable(context, R.drawable.all_app_icon_hove_selector));
                } else {
                    vh.ivIcon.setForeground(ContextCompat.getDrawable(context, R.color.touming));
                }
            }
        });
    }

    @Override
    public AllAppsViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        View view = mLayoutInflater.inflate(R.layout.layout_allapps_item, null);
        return new AllAppsViewHolder(view);

    }

    public OnAllAppsItemClickListener getOnAllAppsItemClickListener() {
        return onAllAppsItemClickListener;
    }

    public void setOnAllAppsItemClickListener(OnAllAppsItemClickListener onAllAppsItemClickListener) {
        this.onAllAppsItemClickListener = onAllAppsItemClickListener;
    }

    public class ItemClickListener implements OnClickListener {

        private int position;

        public ItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (onAllAppsItemClickListener != null && position < allApps.size()) {
                onAllAppsItemClickListener.onItemClick(allApps.get(position));
            }

        }

    }

    public class ItemLongClickListener implements OnLongClickListener {

        private int position;

        ItemLongClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onLongClick(View arg0) {
            if (onAllAppsItemClickListener != null && position < allApps.size()) {
                onAllAppsItemClickListener.onItemLongClick(allApps.get(position));
                return true;
            }
            return false;
        }

    }

    private OnAllAppsItemClickListener onAllAppsItemClickListener;

    public interface OnAllAppsItemClickListener {
        void onItemClick(AppInfo info);

        void onItemLongClick(AppInfo info);
    }

}
