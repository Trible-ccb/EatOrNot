package com.android.eatingornot.widget;

import java.util.ArrayList;

import com.android.eatingornot.activitys.EaterApplication;
import com.android.eatingornot.activitys.EattingOrNotMainActivity;
import com.android.eatingornot.activitys.FavorListActivity;
import com.android.eatingornot.activitys.R;
import com.android.eatingornot.activitys.UserLoginActivity;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CustomNaviBar {

	ArrayList<View> mNavibars;
	Activity mContext;
	EaterApplication mApp;
	private Button btnHome;
	private Button btnMyFavor;
	public CustomNaviBar(Activity c,int which){
		mContext = c;
		mApp = (EaterApplication) mContext.getApplication();
		btnHome = (Button)c.findViewById(R.id.btn_navi_1);
		btnMyFavor = (Button)c.findViewById(R.id.btn_navi_2);
		mNavibars = new ArrayList<View>();
		mNavibars.add(btnHome);
		mNavibars.add(btnMyFavor);

		OnClickListener navibarLstner = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_navi_1:
					changeNavibar(0);
					onHome();
					break;
				case R.id.btn_navi_2:
					changeNavibar(1);
					onFavor();
					break;

				default:
					break;
				}
			}
		};
		if (bar1Listener != null){
			btnHome.setOnClickListener(bar1Listener);
		} else {
			btnHome.setOnClickListener(navibarLstner);
		}
		if (bar2Listener != null){
			btnMyFavor.setOnClickListener(bar2Listener);
		} else {
			btnMyFavor.setOnClickListener(navibarLstner);
		}

		changeNavibar(which);
	}
	public void changeNavibar(int cur){
		
		int bars = mNavibars.size();
		
		for( int i = 0 ; i < bars ; i++){
			mNavibars.get(i).setBackgroundColor(color.transparent);
			mNavibars.get(i).setEnabled(true);
		}
		if (cur < 0 || cur >= bars )return;
		mNavibars.get(cur).setBackgroundResource(R.drawable.highlight);
		mNavibars.get(cur).setEnabled(false);
	}
	private void onHome(){
		if (!mContext.getClass().getSimpleName().equals(EattingOrNotMainActivity.class.getSimpleName())){
			EattingOrNotMainActivity.displayMyself(mContext,true);
		}

	}
	private void onFavor(){
		if (!mContext.getClass().getSimpleName().equals(FavorListActivity.class.getSimpleName())){
			if (mApp.isLogin() || mApp.isWeiboLogin()) {
				FavorListActivity.displayMyself(mContext);
			} else {
				UserLoginActivity.displayMyself(mContext);
			}

		}

	}
	OnClickListener bar1Listener,bar2Listener;
	public void setBar1Listener(OnClickListener l){
		bar1Listener = l;
	}
	public void setBar2Listener(OnClickListener l){
		bar2Listener = l;
	}
}
