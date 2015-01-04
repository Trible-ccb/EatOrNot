package com.android.eatingornot.weibo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Toast;


import ccb.android.net.framkwork.HttpLoader;
import ccb.android.net.framkwork.SimpleAsynHttpDowload;
import ccb.android.net.framkwork.HttpLoader.CacheHelper;
import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.android.net.framkwork.HttpLoader.dataParser;
import ccb.android.net.framkwork.HttpLoader.downLoadListener;
import ccb.java.android.utils.FileUtils;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;
import ccb.java.android.utils.StringUtil;

import com.android.eatingornot.activitys.EaterApplication;
import com.android.eatingornot.activitys.FavorListActivity;
import com.android.eatingornot.activitys.R;
import com.android.eatingornot.activitys.UserLoger;
import com.android.eatingornot.activitys.UserLoginActivity;
import com.android.eatingornot.datamodel.CreateUsAtWeibo;
import com.android.eatingornot.datamodel.UserInfo;
import com.android.eatingornot.datamodel.WeiBoUserData;
import com.android.eatingornot.datapraser.HttpDataPraser;
import com.android.eatingornot.httpparams.BangdinglParams;
import com.android.eatingornot.httpparams.UserRegisterParams;
import com.android.eatingornot.httpparams.WeiboLoginParams;
import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;

public class AuthDialogListener implements WeiboDialogListener {
    private Notification mNotification;
    private Activity mActivity;
    EaterApplication mApp;
    WeiBoUserData weiboUserInfo = null;
    Class callBackClass;
    long authorTime;
    public AuthDialogListener(Activity activity,Class callback) {
        mActivity = activity;
        mNotification = new Notification(mActivity);
        callBackClass = callback;
        mApp = (EaterApplication) mActivity.getApplication();
    }
    @Override
    public void onComplete(Bundle values) {
    	Utility.setAuthorization(new Oauth2AccessTokenHeader());
        String token = values.getString("access_token");
        String expires_in = values.getString("expires_in");
        String uid = values.getString("uid");
        LogWorker.i(mActivity.getResources().getText(R.string.weibo_author_scs) + 
        		"access_token : " + token + "  expires_in: "
                + expires_in + " uid:" + uid );
        AccessToken accessToken = new AccessToken(token, WeiBoData.app_secret);
        accessToken.setExpiresIn(expires_in);
        Weibo.getInstance().setAccessToken(accessToken);
        authorTime = System.currentTimeMillis();
        //获取用户信息
        WeiboEntity entity = new WeiboEntity(mActivity, callBackClass);

        try {
        	weiboUserInfo = HttpDataPraser.getWeiboUserData(entity.showUserInfo(mActivity, uid));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WeiboException e) {
			e.printStackTrace();
		}
        //保存token
        
//        WeiBoUserData user = new WeiBoUserData();
		if (weiboUserInfo != null){
			weiboUserInfo.setWeiboId(uid);
			weiboUserInfo.setAccessToken(token);
			weiboUserInfo.setLifeTime(expires_in);
			weiboUserInfo.setAuthorTime("" + SystemClock.currentThreadTimeMillis());
//			weiboUserInfo.setProfile_image_url(profile_image_url)
		//持久化微博用户信息到本地
//		StorageManager.instance().setWeiBoUser(mActivity, weiboUserInfo);
//		mApp.setUserInfoByWeibo(weiboUserInfo,false);
		
		onwLogin();
		new CreateAndShareTask().execute();
		UserLoger.upload(mActivity, "weiboId:" + uid + "name:" + weiboUserInfo.getScreen_name(),
				mActivity.getResources().getText(R.string.action_weibo_login).toString());
		
			//上传微博信息到服务器

		
		}
		

    }
    

    UserInfo uInfo;
	protected void onwLogin() {
		LogWorker.i(weiboUserInfo.toString());
		Task wLoginTask = HttpLoader.getInstance(mActivity).getTask(
				new  WeiboLoginParams(weiboUserInfo));
		wLoginTask.setDataParser(new dataParser() {			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				if (is == null){
					LogWorker.i("login is == null");
					return false;
				}
				uInfo = HttpDataPraser.getUserInfo(is);
		        LogWorker.i("onDataParser user info:" + uInfo.toString());
		        
				if (uInfo != null && !TextUtils.isEmpty(uInfo.getUserID())){
					LogWorker.i("loginTask user id = " + uInfo.getUserID());
					return true;
				}
				return false;
			}
		});
		wLoginTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int taskId, String result) {
				stopDialog();
				if (result.equals(HttpLoader.DOWLOAD_SUCCESS)){
					
					if (uInfo.getErrorMsg() == 0){
						uInfo.setWeiboAuthorTime(authorTime);
						uInfo.setWeiboExpiresIn(weiboUserInfo.getLifeTime());
						uInfo.setWeiboToken(weiboUserInfo.getAccessToken());
						uInfo.setWeiboImgUrl(weiboUserInfo.getProfile_image_url());
						uInfo.setWeiboId(weiboUserInfo.getWeiboId());
						uInfo.setWeiboName(weiboUserInfo.getScreen_name());
						mApp.setUserInfo(uInfo);
				        LogWorker.i("after weibo login,setting user info:" + mApp.getUserInfo().toString());
//				        if (!mActivity.getClass().getSimpleName().equals(callBackClass.getSimpleName())){
					        Intent intent = new Intent();
					        intent.setClass(mActivity, callBackClass);
					        LogWorker.i("intent to = " + callBackClass);
					        mActivity.startActivity(intent);
					        mActivity.finish();
//					        mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.zoom_exit);
					}else {
						Toast.makeText(mActivity, mActivity.getResources().getText(R.string.login_failed).toString() +
								":" +
								mActivity.getResources().getText(R.string.server_connect_failed).toString(),
								Toast.LENGTH_SHORT
						).show();
//						mApp.setLoginOut(true);
			        }
				
				} else {
//					showErrorDlg(getText(R.string.sweat_tip).toString(), getText(R.string.login_failed).toString());
					Toast.makeText(mActivity, mActivity.getResources().getText(R.string.login_failed), Toast.LENGTH_SHORT
					).show();
//					mApp.setLoginOut(true);
					LogWorker.i(mActivity.getResources().getText(R.string.login_failed).toString());
				}

			}
		});

		
		//执行注册
		final SimpleAsynHttpDowload load = new SimpleAsynHttpDowload(HttpLoader.getInstance(mActivity));
		showDialog(mActivity, "", "", new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				load.cancel(true);
			}
		});
		load.execute(wLoginTask);
//		HttpLoader.getInstance(mActivity).downloadTask(wLoginTask);
	}
	ProgressDialog dialog;
	private  void showDialog(Activity a,String title,String msg,DialogInterface.OnCancelListener cancel){
		if (dialog != null){

			dialog = null;
		}
		dialog = ProgressDialog.show(a, title, msg);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(cancel);
	}

	private void stopDialog(){
		if (dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}

	}

    @Override
    public void onError(DialogError e) {
//        mNotification.toast("Auth error : " + e.getMessage());
    	LogWorker.showToast(mActivity, "授权失败:"+e.getMessage());
    }

    @Override
    public void onCancel() {
//        mNotification.toast("Auth cancel");
    	LogWorker.showToast(mActivity, "授权取消");
    }

    @Override
    public void onWeiboException(WeiboException e) {
//        mNotification.toast("Auth exception : " + e.getMessage());
    	LogWorker.showToast(mActivity, "授权错误:"+e.getMessage());
    }
    CreateUsAtWeibo msg;
    class CreateAndShareTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
	        WeiboEntity entity = new WeiboEntity(mActivity, callBackClass);
	        try {
	        	if ( UserLoginActivity.isCreate ){
					String s = entity.createToUs();
					msg = HttpDataPraser.getCreateMsg(s);
	        	} else {
	        		entity.destoryUs();
	        	}

			} catch (WeiboException e) {
				LogWorker.i(" error code=" + e.getStatusCode()
						+ " msg = " + e.getMessage());
				e.printStackTrace();
			}
			String shareRet = null;
			try {
				if ( UserLoginActivity.isShare ){
					shareRet = entity.update(Weibo.getInstance(), WeiBoData.weibo_appkey,
							mActivity.getResources().getString(R.string.share_our_app), "", "");
//					shareRet = entity.upload(Weibo.getInstance(), WeiBoData.weibo_appkey, 
//							mActivity.getResources().getString(R.string.er_wei_ma_url), 
//							mActivity.getResources().getString(R.string.share_our_app), "", "");
				}

			} catch (WeiboException e) {
				
				e.printStackTrace();
			}
			LogWorker.i("shareRet = " + shareRet);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (msg != null){
				LogWorker.i("msg code = " + msg.getError_code()
						+ " msg = " + msg.getError());
			}
			LogWorker.i("msg = null");
			super.onPostExecute(result);
		}
    	
    }
}
