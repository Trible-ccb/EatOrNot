package com.android.eatingornot.activitys;


import java.util.HashMap;
import java.util.List;

import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;

import com.android.eatingornot.datamodel.Food;
import com.android.eatingornot.weibo.WeiboEntity;
import com.android.eatingornot.widget.CustomNaviBar;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.weibo.net.WeiboException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class EattingOrNotMainActivity extends Activity {
    /** Called when the activity is first created. */
	ProgressDialog dialog;

	HashMap<Integer, ImageButton> imgButtons;
	Button btnHome,btnMyFavor,btnClear;
	List<View> mNavibars;
	Animation btn_zoom_out;
	static String SICK_NAME;
	EaterApplication mApp;
	
	CustomNaviBar mNaviBar;
	List<Food> hotFoods;
	Task  hotFoodTask;
	public  static String getSickName(){
		return SICK_NAME;
	}
	//
	public static int diseaseSet[][] = {{R.string.yun_qi,R.drawable.pregnant},
			{R.string.gao_xue_ya,R.drawable.hypertensive},
			{R.string.tang_niao_bing,R.drawable.diabetes},
			
			{R.string.gao_xue_zhi,R.drawable.hyperlipidemia},
			{R.string.guan_xin_bing,R.drawable.coronary_heart_disease},
			{R.string.zhi_fang_gan,R.drawable.fatty_liver}};
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public static void displayMyself(Activity from,boolean isBack){
		Intent intent = new Intent(from,EattingOrNotMainActivity.class);
		from.startActivity(intent);
		if (isBack){
			from.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		} else {
			from.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
        
//        from.finish();
	}
	
	@SuppressLint("UseSparseArrays")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.onError(this);
        UmengUpdateAgent.update(this);
        UserLoger.upload(this, getText(R.string.EattingOrNot).toString(), getText(R.string.action_setup).toString());
        setContentView(R.layout.home);
        mApp = (EaterApplication) getApplication();
        imgButtons = new HashMap<Integer, ImageButton>();
        initView();

    }
	
	private void initView(){
		btnClear = (Button) findViewById(R.id.title_btn_left);
//		btnClear.setVisibility(View.VISIBLE);

		mNaviBar = new CustomNaviBar(this,0);
		
		ImageButton btnLeft,btnMiddle,btnRight,btnSecLeft,btnSecMiddle,btnSecRight;
		btnLeft = (ImageButton)findViewById(R.id.btn_left);
		btnLeft.setImageResource(diseaseSet[0][1]);
		imgButtons.put(diseaseSet[0][1], btnLeft);
		
		btnMiddle = (ImageButton)findViewById(R.id.btn_middle);
		btnMiddle.setImageResource(diseaseSet[1][1]);
		imgButtons.put(diseaseSet[1][1], btnMiddle);
		
		btnRight = (ImageButton)findViewById(R.id.btn_right);
		btnRight.setImageResource(diseaseSet[2][1]);
		imgButtons.put(diseaseSet[2][1], btnRight);
		
		btnSecLeft = (ImageButton)findViewById(R.id.btn_sec_left);
		btnSecLeft.setImageResource(diseaseSet[3][1]);
		imgButtons.put(diseaseSet[3][1], btnSecLeft);
		
		btnSecMiddle = (ImageButton)findViewById(R.id.btn_sec_middle);
		btnSecMiddle.setImageResource(diseaseSet[4][1]);
		imgButtons.put(diseaseSet[4][1], btnSecMiddle);
		
		btnSecRight = (ImageButton)findViewById(R.id.btn_sec_right);
		btnSecRight.setImageResource(diseaseSet[5][1]);
		imgButtons.put(diseaseSet[5][1], btnSecRight);
		
		OnClickListener lstner = new OnClickListener() {			
			@Override
			public void onClick(View v) {
				LogWorker.i("view on click:" );
				if (v instanceof ImageButton){
					LogWorker.i("view on click,startAnimation" );
					((ImageButton)v).startAnimation(btn_zoom_out);
				}
				LogWorker.i("view on click,endAnimation" );
				switch (v.getId()) {
					case R.id.btn_left:
						SICK_NAME = getResources().getString(diseaseSet[0][0]);
						break;
					case R.id.btn_middle:
						SICK_NAME = getResources().getString(diseaseSet[1][0]);
						break;
					case R.id.btn_right:
						SICK_NAME = getResources().getString(diseaseSet[2][0]);
						break;
					case R.id.btn_sec_left:
						SICK_NAME = getResources().getString(diseaseSet[3][0]);
						break;
					case R.id.btn_sec_middle:
						SICK_NAME = getResources().getString(diseaseSet[4][0]);
					break;
					case R.id.btn_sec_right:
						SICK_NAME = getResources().getString(diseaseSet[5][0]);
						break;
					default:
						break;
				}
				UserLoger.upload(EattingOrNotMainActivity.this, SICK_NAME, getText(R.string.action_view).toString());
				showDialog("", getText(R.string.loading).toString());
				SeachFoodsActivity.displayMyself(EattingOrNotMainActivity.this);

			}
		};
		btnClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StorageManager.instance().clearUserData(EattingOrNotMainActivity.this);
			}
		});
		btnLeft.setOnClickListener(lstner); 
		btnMiddle.setOnClickListener(lstner);
		btnRight.setOnClickListener(lstner);
		btnSecRight.setOnClickListener(lstner);
		btnSecLeft.setOnClickListener(lstner);
		btnSecMiddle.setOnClickListener(lstner);
		btn_zoom_out = AnimationUtils.loadAnimation(this, R.anim.zoom_out);

	}

	private void showDialog(String title,String msg){
		if (dialog != null){
			dialog = null;
		}
		dialog = ProgressDialog.show(this, title, msg);
		dialog.setCancelable(true);
	}

	private void stopDialog(){
		if (dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}

	}
	
	boolean isOnBackTwice;
	long clickTime = 0;
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	public void onBackPressed() {
		int during = 1500;
		if (!isOnBackTwice){
			long tmp = clickTime;
			clickTime = System.currentTimeMillis();
			if ( tmp == 0 || clickTime - tmp > during + during * 0.5){
				Toast.makeText(this, R.string.click_again_to_qiut, during).show();
			} else {
				isOnBackTwice = true;
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			}
			return;
		}
		
		super.onBackPressed();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		isOnBackTwice = false;
		mNaviBar.changeNavibar(0);
		LogWorker.i(this.getClass().getSimpleName()+":onResume");
		MobclickAgent.onResume(this);
		MobclickAgent.onEvent(this, "getInMainLayer");
		
//		String d =  StorageManager.instance().getDefaultDisease(this);
//		for ( int i = 0 ; i < 6 ; i++){
//			String ds =getString(diseaseSet[i][0]);
//			if ( d.equals(ds) ){
//				float x = imgButtons.get(diseaseSet[i][1]).getX();
//				float y = imgButtons.get(diseaseSet[i][1]).getY();
//				TextView t = new TextView(this);
//				t.setText("moren");
//				break;
//			}
//		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		LogWorker.i(this.getClass().getSimpleName()+":onDestroy");
		stopDialog();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		LogWorker.i(this.getClass().getSimpleName()+":onPause");
		stopDialog();
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onStop() {
		LogWorker.i(this.getClass().getSimpleName()+":onStop");
		MobclickAgent.onEvent(this, "getOutMainLayer");
		
		super.onStop();
	}
   
}