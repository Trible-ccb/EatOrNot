package com.android.eatingornot.activitys;


import com.umeng.analytics.MobclickAgent;

import ccb.android.net.framkwork.HttpLoader;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class WelcomeActivity extends Activity implements AnimationListener{
	
	Animation animIn , animOut;
	ImageView welcomeView;
	
	boolean isFirstTime;
	String mFirstTimeTag = "isFirstTime";
	String sharedFileName = "吃否";
	private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		MobclickAgent.updateOnlineConfig(this);		
		initBackGroundObj();
		
		checkIsFirstTime();
		animIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		animOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		animIn.setAnimationListener(this);
		animOut.setAnimationListener(this);
		
		welcomeView = (ImageView) findViewById(R.id.welcome);
		

	}
	
	private void initBackGroundObj(){
		HttpLoader.initLoader(this);
		StorageManager.initStorage(this);

	}
	private void checkIsFirstTime(){
		isFirstTime = StorageManager.instance().getIsFirstTimeLauch(this);
//		isFirstTime = false;
		if (isFirstTime){
			StorageManager.instance().setIsFirstTimeLauch(this,false);
		}
		
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation == animIn){
			welcomeView.startAnimation(animOut);
		} else if (animation == animOut){
			welcomeView.setVisibility(View.INVISIBLE);
			
			EattingOrNotMainActivity.SICK_NAME = StorageManager.instance().getDefaultDisease(this);
	        if (EattingOrNotMainActivity.SICK_NAME != null && !EattingOrNotMainActivity.SICK_NAME.equals("")){
	        	showDialog("", "");
	        	EattingOrNotMainActivity.displayMyself(this,false);
	        	SeachFoodsActivity.displayMyself(WelcomeActivity.this);
	        	finish();
	        	return;
	        }
			if(isFirstTime){
				gotoDirectorForTheFirstTime();
			} else {
				goIntoApp();
			}
			
		}
	}
	private void gotoDirectorForTheFirstTime(){
		DirectorActivity.displayMyself(this);
		finish();
	}
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	private void goIntoApp(){

		EattingOrNotMainActivity.displayMyself(this,false);
		finish();
	}
	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		
	}
	private void showDialog(String title,String msg){
		if (dialog != null){
			dialog = null;
		}
		dialog = ProgressDialog.show(this, title, msg);
	}
	@Override
	protected void onStart() {
		welcomeView.startAnimation(animIn);
		super.onStart();
	}
	@Override
	protected void onDestroy() {
		LogWorker.i(this.getClass().getSimpleName()+":onDestroy");
		stopDialog();
		super.onDestroy();
	}

	private void stopDialog() {
		if (dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}
		
	}
}
