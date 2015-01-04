/**
 * 
 */
package com.android.eatingornot.activitys;

import java.io.InputStream;

import ccb.android.net.framkwork.HttpLoader;
import ccb.android.net.framkwork.SimpleAsynHttpDowload;
import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.android.net.framkwork.HttpLoader.dataParser;
import ccb.android.net.framkwork.HttpLoader.downLoadListener;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StringUtil;

import com.android.eatingornot.datamodel.RequestRetMsg;
import com.android.eatingornot.datapraser.HttpDataPraser;
import com.android.eatingornot.httpparams.ForgetPSWParams;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author Administrator
 *
 */
public class ForgetPswActivity extends Activity {

	EditText edtEmail;
	Button btnSubmit,btnBack;
	TextView tvTitle;
	
	@Override
	public void onBackPressed() {
		onBack();
		super.onBackPressed();
	}
	public static void displayMyself(Activity from){
		Intent intent = new Intent(from,ForgetPswActivity.class);
		from.startActivity(intent);
		from.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_psw);
		MobclickAgent.onError(this);
		UserLoger.upload(this, getText(R.string.ForgetPsw).toString(), getText(R.string.action_setup).toString());
		initView();
	}

	@Override
	protected void onDestroy() {
		stopDialog();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	private void initView(){
		 tvTitle = (TextView) findViewById(R.id.title_text);
		 tvTitle.setText(R.string.forget_psw);
		 tvTitle.setVisibility(View.VISIBLE);
		 btnBack = (Button) findViewById(R.id.title_btn_left);
		 btnBack.setVisibility(View.VISIBLE);
		 edtEmail = (EditText) findViewById(R.id.edt_email);
		
		 btnSubmit = (Button) findViewById(R.id.submit_forget_psw);
		 OnClickListener lsn = new OnClickListener() {			
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
						case R.id.title_btn_left :
							onBack();
						break;
						case R.id.submit_forget_psw :
							onForgetPsw();
						break;
					}
				}
		 };
		btnBack.setOnClickListener(lsn);
		btnSubmit.setOnClickListener(lsn);
	}
		RequestRetMsg sendEmailMsg;
		private void onForgetPsw(){
			String email = edtEmail.getText().toString();
			if ( !StringUtil.isValidEmail(email)){
				String tip = getText(R.string.email_name_error).toString();
				if (TextUtils.isEmpty(email)){
					tip = tip + getText(R.string.tip_for_input_null).toString();
				}
				LogWorker.showToast(this, tip);
				return ;
			}
			Task sendEmailTask = HttpLoader.getInstance(this).getTask(new ForgetPSWParams(email));
			sendEmailTask.setDataParser(new dataParser() {
				
				@Override
				public boolean onDataParser(Task t, InputStream is) {
					sendEmailMsg = HttpDataPraser.getSendEmailMsg(is);
					if (sendEmailMsg == null){
						return false;
					}
					return true;
				}
			});
			sendEmailTask.setDownLoadDoneListener(new downLoadListener() {
				
				@Override
				public void onDownLoadDone(int taskId, String result) {
					stopDialog();
					if (result.equals(HttpLoader.DOWLOAD_SUCCESS)){
						if (sendEmailMsg.isErrorMsg()){
							showErrorDlg(getText(R.string.sweat_tip).toString(),
									getText(R.string.email_have_send).toString());
						} else {
							showErrorDlg(getText(R.string.sweat_tip).toString(),
									getText(R.string.email_have_send_failed).toString());
						}
					} else {
						showErrorDlg(getText(R.string.sweat_tip).toString(),
								getText(R.string.server_connect_failed).toString());
					}
				}
			});

			final SimpleAsynHttpDowload loader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
			showDialog(getText(R.string.sweat_tip).toString(), getText(R.string.email_sending).toString(),
					new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							loader.cancel(true);
						}
					});
			loader.execute(sendEmailTask);
		}
		ProgressDialog dialog;
		private  void showDialog(String title,String msg){
			if (dialog != null){
				dialog = null;
			}
			dialog = ProgressDialog.show(this, title, msg);
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
		private void onBack(){
			 finish();
			 overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	       
		}

}
