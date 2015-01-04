package com.android.eatingornot.weibo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.android.eatingornot.datamodel.RequestRetMsg;
import com.android.eatingornot.datamodel.UserInfo;
import com.android.eatingornot.datamodel.WeiBoUserData;
import com.android.eatingornot.datapraser.HttpDataPraser;
import com.android.eatingornot.httpparams.BangdinglParams;
import com.android.eatingornot.httpparams.UserRegisterParams;
import com.weibo.net.AccessToken;
import com.weibo.net.AccessTokenHeader;
import com.weibo.net.DialogError;
import com.weibo.net.Oauth2AccessToken;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Token;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;

public class BangdingDialogListener implements WeiboDialogListener {
    private Notification mNotification;
    private Activity mActivity;
    EaterApplication mApp;
    String userId;
    Class callBackClass;
    long authorTime;
    WeiBoUserData weiboUserInfo = null;
    public BangdingDialogListener(Activity activity,Class callback,String uid) {
        mActivity = activity;
        mNotification = new Notification(mActivity);
        callBackClass = callback;
        mApp = (EaterApplication) mActivity.getApplication();
        userId = uid;
    }
    @Override
    public void onComplete(Bundle values) {
    	Utility.setAuthorization(new Oauth2AccessTokenHeader());
        String token = values.getString("access_token");
        String expires_in = values.getString("expires_in");
        String weiboId = values.getString("uid");
        LogWorker.i(mActivity.getResources().getText(R.string.weibo_author_scs) + 
        		"access_token : " + token + "  expires_in: "
                + expires_in + " uid:" + weiboId );
        AccessToken accessToken = new AccessToken(token, WeiBoData.app_secret);
        accessToken.setExpiresIn(expires_in);
        Weibo.getInstance().setAccessToken(accessToken);
        
        //获取用户信息
        WeiboEntity entity = new WeiboEntity(mActivity, callBackClass);
        authorTime = System.currentTimeMillis();
        try {
        	weiboUserInfo = HttpDataPraser.getWeiboUserData(entity.showUserInfo(mActivity, weiboId));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //保存token
        
//        WeiBoUserData user = new WeiBoUserData();
		if (weiboUserInfo != null){
			weiboUserInfo.setWeiboId(weiboId);
			weiboUserInfo.setAccessToken(token);
			weiboUserInfo.setLifeTime(expires_in);
			weiboUserInfo.setAuthorTime("" + SystemClock.currentThreadTimeMillis());
			
		}

		//持久化微博用户信息到本地
//		StorageManager.instance().setWeiBoUser(mActivity, weiboUserInfo);
//		mApp.setUserInfoByWeibo(weiboUserInfo,true);

		//上传微博信息到服务器
		onBangding(userId);
		//记录行为信息
		UserLoger.upload(mActivity, "weiboId:" + weiboId + "name:" + weiboUserInfo.getScreen_name(),
				mActivity.getResources().getText(R.string.action_weibo_login).toString());
		

    }
//    RequestRetMsg bMsg;
    UserInfo uInfo;
	protected void onBangding(String uid) {

		Task bangdingTask = HttpLoader.getInstance(mActivity).getTask(
				new  BangdinglParams(uid,weiboUserInfo));
		bangdingTask.setDataParser(new dataParser() {			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				if (is == null)return false;
				uInfo = HttpDataPraser.getUserInfo(is);
				return true;
			}
		});
		bangdingTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int taskId, String result) {
				stopDialog();
				if (result.equals(HttpLoader.DOWLOAD_SUCCESS)){
					LogWorker.i("after banding: " + uInfo.toString());
					if (uInfo.getErrorMsg() == 0){
						uInfo.setWeiboAuthorTime(authorTime);
						uInfo.setWeiboExpiresIn(weiboUserInfo.getLifeTime());
						uInfo.setWeiboExpiresIn(weiboUserInfo.getLifeTime());
						uInfo.setWeiboToken(weiboUserInfo.getAccessToken());
						uInfo.setWeiboImgUrl(weiboUserInfo.getProfile_image_url());
						uInfo.setWeiboId(weiboUserInfo.getWeiboId());
						uInfo.setWeiboName(weiboUserInfo.getScreen_name());
						mApp.setUserInfo(uInfo);
//						if (!mActivity.getClass().getSimpleName().equals(callBackClass.getSimpleName())){
							
					        Intent intent = new Intent();
					        intent.setClass(mActivity, callBackClass);
					        LogWorker.i("intent to = " + callBackClass);
					        mActivity.startActivity(intent);
					        mActivity.finish();
//						}

					} else {
						Toast.makeText(mActivity, mActivity.getResources().getText(R.string.bangding_weibo_failed), Toast.LENGTH_SHORT
						).show();
						mApp.setWeiboLogin(false);
					}
				} else {
					Toast.makeText(mActivity, mActivity.getResources().getText(R.string.bangding_weibo_failed), Toast.LENGTH_SHORT
					).show();
					mApp.setWeiboLogin(false);
				}

			}
			
		});
		
		//执行注册
		final SimpleAsynHttpDowload loader = new SimpleAsynHttpDowload(HttpLoader.getInstance(mActivity));
		showDialog(mActivity, "","",new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				loader.cancel(true);
			}
		});
		loader.execute(bangdingTask);
//		HttpLoader.getInstance(mActivity).downloadTask(bangdingTask);
		
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
    	LogWorker.showToast(mActivity, "绑定失败:"+e.getMessage());
    }

    @Override
    public void onCancel() {
//        mNotification.toast("Auth cancel");
    	LogWorker.showToast(mActivity, "绑定取消");
    }

    @Override
    public void onWeiboException(WeiboException e) {
//        mNotification.toast("Auth exception : " + e.getMessage());
    	LogWorker.showToast(mActivity, "绑定错误:"+e.getMessage());
    }
}
