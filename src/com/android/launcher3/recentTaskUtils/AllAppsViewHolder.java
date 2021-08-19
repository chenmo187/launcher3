package com.android.launcher3.recentTaskUtils;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.android.launcher3.R;

public class AllAppsViewHolder extends RecyclerView.ViewHolder{

	TextView tvAppName;
	ImageView ivIcon;
	LinearLayout ll_itemRoot;
	
	public AllAppsViewHolder(View itemView) {
		super(itemView);
		tvAppName=(TextView) itemView.findViewById(R.id.tv_appName);
		ivIcon=(ImageView) itemView.findViewById(R.id.iv_icon);
		ll_itemRoot=(LinearLayout) itemView.findViewById(R.id.ll_itemRoot);
		
	}

}
