package com.android.eatingornot.activitys;

import java.io.InputStream;

import ccb.android.net.framkwork.HttpLoader;
import ccb.android.net.framkwork.HttpLoader.CacheHelper;
import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.android.net.framkwork.HttpLoader.dataParser;
import ccb.android.net.framkwork.HttpLoader.downLoadListener;
import ccb.android.net.framkwork.SimpleAsynHttpDowload;
import ccb.java.android.utils.FileUtils;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;
import ccb.java.android.utils.StringUtil;

import com.android.eatingornot.datamodel.RequestRetMsg;
import com.android.eatingornot.datamodel.UserInfo;
import com.android.eatingornot.datapraser.HttpDataPraser;
import com.android.eatingornot.httpparams.ForgetPSWParams;
import com.android.eatingornot.httpparams.UserLoginParams;
import com.android.eatingornot.httpparams.UserRegisterParams;
import com.android.eatingornot.tool.Tool;
import com.android.eatingornot.weibo.WeiboEntity;
import com.android.eatingornot.widget.CustomNaviBar;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class UserLoginActivity extends Activity{

	EditText edtLoginEmail,edtRegisterEmail,edtUername,edtLoginPsw,edtRegisterPsw;
	LinearLayout loginLayout,registerLayout;
	RelativeLayout rlScrollerContener;
	ScrollView svScrollerView;
	
	Button btnLogin,btnRegister;
	Button btnSubmitLogin,btnSubmitRegister;
	Button btnWeiboLogin,btnCreateToUs,btnShareApp;
	Button btnBack;
	TextView tvTitle,tvForgetPsw;;
	
	UserInfo userInfo;
	EaterApplication mApp;
	
	public static boolean isCreate,isShare;
	
	private static Activity fromActivity;
	@SuppressLint("NewApi")
	public static void displayMyself(Activity from){
		fromActivity = from;
		Intent intent = new Intent(from,UserLoginActivity.class);
		from.startActivity(intent);
        from.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//        from.finish();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login);
		MobclickAgent.onError(this);
		UserLoger.upload(this, getText(R.string.UserLogin).toString(), getText(R.string.action_setup).toString());
		mApp = (EaterApplication) getApplication();
		isCreate = isShare = true;
		initView();
		
		
	}

	private void initView() {
		 new CustomNaviBar(this, 1);
		 tvTitle = (TextView) findViewById(R.id.title_text);
//		 tvTitle.setText(R.string.favor);
		 btnBack = (Button) findViewById(R.id.title_btn_left);
		 btnBack.setVisibility(View.VISIBLE);
		 
		 tvForgetPsw = (TextView) findViewById(R.id.forget_psw);
		 
		 edtLoginEmail = (EditText) findViewById(R.id.email);
		 edtLoginEmail.setText(mApp.getUserInfo().getEmail());
		 
		 edtRegisterEmail = (EditText) findViewById(R.id.register_email);
		 edtUername = (EditText) findViewById(R.id.username);
		 edtLoginPsw = (EditText) findViewById(R.id.password);
		 edtLoginPsw.setText(mApp.getUserInfo().getPWD());
		 
		 edtRegisterPsw = (EditText) findViewById(R.id.register_password);
		 loginLayout = (LinearLayout) findViewById(R.id.login_layout);
		 registerLayout = (LinearLayout) findViewById(R.id.register_layout);
		 btnLogin = (Button) findViewById(R.id.btn_login);
		 btnRegister = (Button) findViewById(R.id.btn_register);
		 btnSubmitLogin = (Button) findViewById(R.id.submit_login);
		 btnSubmitRegister = (Button) findViewById(R.id.submit_register);
		 btnWeiboLogin = (Button) findViewById(R.id.weibo_login);
		 btnCreateToUs = (Button) findViewById(R.id.btn_create_us);
		 btnShareApp = (Button) findViewById(R.id.btn_share_to_weibo);
		 btnShareApp.setSelected(isShare);
		 btnCreateToUs.setSelected(isCreate);
		 OnClickListener lsn = new OnClickListener() {			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_login:
					onLoginButton();
					break;
				case R.id.btn_register:
					onRegisterButton();
					break;
				case R.id.submit_login:
					onSubmitLogin();
					break;
				case R.id.submit_register:
					onSubmitRegister();
					break;
				case R.id.weibo_login:
					onWeiboLogin();
					break;
				case R.id.title_btn_left:
					onBack();
					break;
				case R.id.forget_psw:
					onForgetPsw();
					break;
				case R.id.btn_create_us:
					isCreate = !isCreate;
					btnCreateToUs.setSelected(isCreate);
					break;
				case R.id.btn_share_to_weibo:
					isShare = !isShare;
					btnShareApp.setSelected(isShare);
					break;
				default:
					break;
				}
				
			}
		};
		btnShareApp.setOnClickListener(lsn);
		btnCreateToUs.setOnClickListener(lsn);
		tvForgetPsw.setOnClickListener(lsn);
		btnBack.setOnClickListener(lsn);
		btnLogin.setOnClickListener(lsn);
		btnRegister.setOnClickListener(lsn);
		btnSubmitLogin.setOnClickListener(lsn);
		btnSubmitRegister.setOnClickListener(lsn);
		btnWeiboLogin.setOnClickListener(lsn);
		
		svScrollerView = (ScrollView) findViewById(R.id.sv_layout);
		rlScrollerContener = (RelativeLayout) findViewById(R.id.rl_contener);
		
		showLoginLayout();
		onLoginButton();
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		LogWorker.i("sc h = " + svScrollerView.getHeight()
				+ " rl h = " + rlScrollerContener.getHeight()
				+ " ll h = " + loginLayout.getHeight());
		if ( rlScrollerContener.getHeight() < svScrollerView.getHeight()){
			rlScrollerContener.setMinimumHeight(svScrollerView.getHeight() - Tool.dip2px(this, 16));
		}
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
		if (cancel != null){
			dialog.setCancelable(true);
		}

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
	 RequestRetMsg sendEmailMsg;
	private void onForgetPsw(){
		ForgetPswActivity.displayMyself(this);
	}
	protected void onWeiboLogin() {
		WeiboEntity weiboEntity = new WeiboEntity(this,FavorListActivity.class);
		FavorListActivity.fromActivity = fromActivity;
		weiboEntity.login();
		
	}
	private void switchTaps(int idx){
		
		if ( idx == 0){
			btnLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_tap_left));
			btnRegister.setBackgroundDrawable(getResources().getDrawable(R.drawable.not_select_tap_right));
			
		} else if ( idx == 1){
			btnLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.not_select_tap_left));
			btnRegister.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_tap_right));
		}

	}
	protected void onSubmitRegister() {
		String username = edtUername.getText().toString();
		String registeremail = edtRegisterEmail.getText().toString();
		final String password = edtRegisterPsw.getText().toString();
		
		String md5Psw = StringUtil.encodeString(password);
		
		if (!StringUtil.isValidEmail(registeremail)){
			edtRegisterEmail.setHint(R.string.email_name_error);
			LogWorker.showToast(this, getText(R.string.email_name_error));
			return;
		}
		if (!StringUtil.isValidPassword(password)){
			edtRegisterPsw.setHint(R.string.password_text_error);
			LogWorker.showToast(this, getText(R.string.password_text_error));
			return ;
		}
		Task registerTask = HttpLoader.getInstance(this).getTask(
				new UserRegisterParams(username, registeremail, md5Psw));
		final String userInfoPath = StorageManager.instance().getAPPCachePath() 
		+ "userInfo.txt";
		if ( EaterApplication.READ_HTTP_CACHE ){
			registerTask.setCacheHelper(new CacheHelper() {
				
				@Override
				public boolean onStoreDataInCache(Task t, InputStream is) {
					return FileUtils.storeInputStream(userInfoPath, is, "utf-8");
				}
				
				@Override
				public InputStream getCache(Task t) {
					InputStream is = null;
					try {
						is = getAssets().open("userInfo.txt");
					} catch (Exception e) {
						LogWorker.i(e.getMessage());
						e.printStackTrace();
					}
					return is;
				}
			});
		}
		registerTask.setDataParser(new dataParser() {			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				if (is == null)return false;
				userInfo = HttpDataPraser.getUserInfo(is);
				if (userInfo != null && userInfo.getUserID() != null){
					LogWorker.i("registerTask user id = " + userInfo.getUserID());
					//保存用户数据
					
					return true;
				}
				return false;
			}
		});
		registerTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int taskId, String result) {
				if (result.equals(HttpLoader.DOWLOAD_SUCCESS)){
//					showErrorDlg(getText(R.string.sweat_tip).toString(), getText(R.string.register_scs).toString());
					mApp.setUserInfo(userInfo);
//					userInfo.setPWD(password);
					FavorListActivity.displayMyself(fromActivity);
					finish();
				} else {
					if (userInfo != null && userInfo.getErrorMsg() == 1){
						showErrorDlg(getText(R.string.sweat_tip).toString(), getText(R.string.email_had_reg).toString());
					} else {
						showErrorDlg(getText(R.string.sweat_tip).toString(), getText(R.string.register_failed).toString());
					}
					
				}
				
			}
		});
		
		//执行注册
		showDialog(getText(R.string.sweat_tip).toString(), getText(R.string.register_text).toString());
		SimpleAsynHttpDowload loader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		loader.execute(registerTask);
		
	}
	protected void onSubmitLogin() {
		
		String email = edtLoginEmail.getText().toString();
		final String password = edtLoginPsw.getText().toString();
		
		String md5Psw = StringUtil.encodeString(password);
		
//		boolean inPutIsOk = true;
		if (!StringUtil.isValidEmail(email)){
			edtLoginEmail.setHint(R.string.email_name_error);
			LogWorker.showToast(this, getText(R.string.email_name_error));
			return;
		}
		if (!StringUtil.isValidPassword(password)){
			edtLoginPsw.setHint(R.string.password_text_error);
			LogWorker.showToast(this, getText(R.string.password_text_error));
			return ;
		}
		Task loginTask = HttpLoader.getInstance(this).getTask(
				new UserLoginParams(email, md5Psw,false));
		final String userInfoPath = StorageManager.instance().getAPPCachePath() 
		+ "userInfo.txt";
		if ( EaterApplication.READ_HTTP_CACHE ){
			loginTask.setCacheHelper(new CacheHelper() {
				
				@Override
				public boolean onStoreDataInCache(Task t, InputStream is) {
					// TODO Auto-generated method stub
					return FileUtils.storeInputStream(userInfoPath, is, "utf-8");
				}
				
				@Override
				public InputStream getCache(Task t) {
					// TODO Auto-generated method stub
					InputStream is = null;
					try {
	//					is = FileUtils.openFileInputStream(fooddetailPath);
						is = getAssets().open("userInfo.txt");
					} catch (Exception e) {
						LogWorker.i(e.getMessage());
						e.printStackTrace();
					}
					return is;
				}
			});
		}
		loginTask.setDataParser(new dataParser() {			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				if (is == null){
					LogWorker.i("login is == null");
					return false;
				}
				userInfo = HttpDataPraser.getUserInfo(is);
				LogWorker.i("userInfo = " + userInfo);
				if (userInfo != null && userInfo.getUserID() != null){
					LogWorker.i("loginTask user id = " + userInfo.getUserID());
					
					return true;
				}
				return false;
			}
		});
		loginTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int taskId, String result) {
				if (result.equals(HttpLoader.DOWLOAD_SUCCESS)){
//					showErrorDlg(getText(R.string.sweat_tip).toString(), getText(R.string.login_scs).toString());

					mApp.setUserInfo(userInfo);
//					userInfo.setPWD(password);
					FavorListActivity.displayMyself(fromActivity);
					finish();
				} else {
					showErrorDlg(getText(R.string.sweat_tip).toString(), getText(R.string.login_failed).toString());
				}
				
			}
		});
		
		//执行登录
		showDialog(getText(R.string.sweat_tip).toString(), getText(R.string.login_text).toString());
		SimpleAsynHttpDowload loader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		loader.execute(loginTask);

	}
	protected void onRegisterButton() {
		tvTitle.setText(R.string.user_register);
		switchTaps(1);
		showRegisterLayout();
		
	}
	protected void onLoginButton() {
		tvTitle.setText(R.string.user_login);
		switchTaps(0);
		showLoginLayout();
		
	}
	private void showLoginLayout(){
		registerLayout.setVisibility(View.GONE);
		loginLayout.setVisibility(View.VISIBLE);
	}
	private void showRegisterLayout(){
		registerLayout.setVisibility(View.VISIBLE);
		loginLayout.setVisibility(View.GONE);
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
	@SuppressLint("NewApi")
	private void onBack(){
//		Intent intent = new Intent(this,fromActivity.getClass());
//		startActivity(intent);
		 finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
       
//		EattingOrNotMainActivity.displayMyself(this, true);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		onBack();
		super.onBackPressed();
	}
	@Override
	protected void onStart() {
		super.onStart();
	}

	
}
