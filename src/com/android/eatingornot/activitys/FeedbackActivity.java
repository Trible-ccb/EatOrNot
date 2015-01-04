package com.android.eatingornot.activitys;

import java.io.InputStream;

import com.android.eatingornot.datamodel.RequestRetMsg;
import com.android.eatingornot.datapraser.HttpDataPraser;
import com.android.eatingornot.httpparams.FeedbackPaprams;
import com.android.eatingornot.httpparams.UserRegisterParams;
import com.umeng.analytics.MobclickAgent;

import ccb.android.net.framkwork.HttpLoader;
import ccb.android.net.framkwork.SimpleAsynHttpDowload;
import ccb.android.net.framkwork.HttpLoader.CacheHelper;
import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.android.net.framkwork.HttpLoader.dataParser;
import ccb.android.net.framkwork.HttpLoader.downLoadListener;
import ccb.java.android.utils.FileUtils;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FeedbackActivity extends Activity {
	
	EditText editFeedback, editContact;
	Button btnSubmit,btnBack;

	TextView tvTitle;
	EaterApplication mApp;
	static Activity fromActivity;
	ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		MobclickAgent.onError(this);
		UserLoger.upload(this, getText(R.string.UserFeedback).toString(), getText(R.string.action_setup).toString());
		mApp = (EaterApplication) getApplication();
		initView();
	}
	@SuppressLint("NewApi")
	public static void displayMyself(Activity from){
		fromActivity = from;
		Intent intent = new Intent(from,FeedbackActivity.class);
		from.startActivity(intent);
        from.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//        from.finish();
	}
	private void initView(){
		editContact = (EditText) findViewById(R.id.edit_contact);
		editFeedback = (EditText) findViewById(R.id.edit_feedback);
		btnSubmit = (Button) findViewById(R.id.btn_submit);
		btnBack = (Button) findViewById(R.id.title_btn_left);
		btnBack.setVisibility(View.VISIBLE);
		
		tvTitle = (TextView) findViewById(R.id.title_text);
		tvTitle.setText(R.string.feedback);
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_submit:
					onSubmit();
					break;
				case R.id.title_btn_left:
					onBack();
					break;
				default:
					break;
				}
				
			}
		};
		btnSubmit.setOnClickListener(listener);
		btnBack.setOnClickListener(listener);
	}
	@SuppressLint("NewApi")
	protected void onBack() {
//		Intent intent = new Intent(this,fromActivity.getClass());
//		startActivity(intent);
		 finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
       
		
	}
	protected void onSubmit() {
		LogWorker.i("onSubmit");
		
		doFeedbackTask();
		
	}
	Task mFeedbackTask;
	RequestRetMsg feedbackRetMsg;
	private void prepareFeedBackTask(){
		mFeedbackTask = HttpLoader.getInstance(this).getTask(
				new FeedbackPaprams(editFeedback.getText().toString(), editContact.getText().toString(),mApp.getUserInfo().getUserID()));
		final String FeedbackPath = StorageManager.instance().getAPPCachePath() 
		+ "Feedback.txt";
		if ( EaterApplication.READ_HTTP_CACHE ){
			mFeedbackTask.setCacheHelper(new CacheHelper() {
				
				@Override
				public boolean onStoreDataInCache(Task t, InputStream is) {
					// TODO Auto-generated method stub
					return FileUtils.storeInputStream(FeedbackPath, is, "utf-8");
				}
				
				@Override
				public InputStream getCache(Task t) {
					// TODO Auto-generated method stub
					InputStream is = null;
					try {
	//					is = FileUtils.openFileInputStream(fooddetailPath);
						is = getAssets().open("Feedback.txt");
					} catch (Exception e) {
						LogWorker.i(e.getMessage());
						e.printStackTrace();
					}
					return is;
				}
			});
		}
		mFeedbackTask.setDataParser(new dataParser() {
			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				if (is == null)return false;
				try{
//					mFavorFoods = (ArrayList<Food>) HttpDataPraser.getFoodList(is);
					feedbackRetMsg = HttpDataPraser.getFavorRetMsg(is);
					if (feedbackRetMsg == null){
						return false;
					}
					return true;
				} catch (Exception e) {
					return false;
				}
				
			}
		});
		mFeedbackTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int taskId, String result) {
				// TODO Auto-generated method stub
				if (result.equals(HttpLoader.DOWLOAD_SUCCESS)){
					stopDialog();
					if (feedbackRetMsg.isActionCode()){
						showErrorDlg(getText(R.string.sweat_tip).toString(), getText(R.string.uploading_suc).toString());
					} else {
						showErrorDlg(getText(R.string.sweat_tip).toString(),
								getText(R.string.uploading_failed).toString());
					}
				} else {
					showErrorDlg(getText(R.string.sweat_tip).toString(),
							getText(R.string.server_connect_failed).toString());
				}
			}
		});
	}
	private void doFeedbackTask(){
		prepareFeedBackTask();
		
		final SimpleAsynHttpDowload loader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		showDialog(getText(R.string.sweat_tip).toString(), getText(R.string.uploading_feedback).toString(),
				new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				loader.cancel(true);
			}
		});
		loader.execute(mFeedbackTask);
	}
	private  void showDialog(String title,String msg,DialogInterface.OnCancelListener cancel){
		if (dialog != null){
			dialog = null;
		}
		dialog = ProgressDialog.show(this, title, msg);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(cancel);
	}

	private void stopDialog(){
		if (dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}

	}
	private void showErrorDlg(String title, String msg) {
		if (dialog != null && dialog.isShowing()) {
			stopDialog();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog error_dlg = builder.create();
		error_dlg.setCancelable(true);
		error_dlg.setTitle(title);
		error_dlg.setMessage(msg);
		if ( !isFinishing() ){
			error_dlg.show();
		}

	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopDialog();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		onBack();
		super.onBackPressed();
	}
}
