package com.android.eatingornot.activitys;

import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.android.eatingornot.datamodel.TokenInfo;
import com.android.eatingornot.datamodel.UserInfo;
import com.android.eatingornot.datamodel.WeiBoUserData;
import com.android.eatingornot.weibo.WeiBoData;
import com.android.eatingornot.weibo.WeiboEntity;
import com.umeng.analytics.MobclickAgent;
import com.weibo.net.AccessToken;
import com.weibo.net.Oauth2AccessToken;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Token;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;


import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;
import ccb.java.android.utils.StringUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class EaterApplication extends Application {

	public final static String IP = "http://192.168.4.107:8080/eatornot/";
	public final static String SERVER_IP = "http://118.123.116.195:8088/";
	public static String HOST_URL = "";
	public static String HD_IMG_URL = "images/bigimg/";
	public static String IMG_URL = "images/smimg/";
	public final static int YI = 0;
	public final static int JI = 1;
	public final static int KE = 2;
	public static String IMEI ;
	public final static boolean READ_HTTP_CACHE = false;
	public final static boolean LOCAL_DEBUG = false;
	UserInfo userInfo;
	boolean login;
	boolean isWeiboLogin;
	boolean isLoginOut;
	
	public boolean isLoginOut() {
		SharedPreferences spf = StorageManager.instance().getSPF(this);
		isLoginOut = spf.getBoolean("isLoginOut", false);
		return isLoginOut;


	}
	public void setLoginOut(boolean isLoginOut) {
		Editor e = StorageManager.instance().getSPF(this).edit();
		e.putBoolean("isLoginOut", isLoginOut);
		e.commit();
		this.isLoginOut = isLoginOut;
	}
	public EaterApplication() {
	}
	private void initURL(){
		if (LOCAL_DEBUG){
			HOST_URL = IP;
		} else {
			HOST_URL = SERVER_IP;
		}
		HD_IMG_URL = HOST_URL + HD_IMG_URL;
		IMG_URL = HOST_URL + IMG_URL;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		MobclickAgent.onError(this);
		LogWorker.i(this.getClass().getSimpleName() + " :onCreate");
		userInfo = new UserInfo();
		initURL();
		if ( checkCallingOrSelfPermission(android.Manifest.permission.READ_PHONE_STATE )
				== PackageManager.PERMISSION_GRANTED ){
			IMEI = ((TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		} else {
			IMEI = "";
		}
		LogWorker.i("Imei = " + IMEI);
		initUserInfo();
	}
	
	private void initUserInfo(){
		
		SharedPreferences spf = StorageManager.instance().getSPF(this);
		String username ;
		String password ;
		String email;
		String imgUrl;
		String userid;
		String registerDate;
		int state = 0;
		
		String weiboToken;
		String weiboId;
		String weiboName;
		String weiboImgUrl;
		String weiboExpiresIn ;
		long weiboAuthorTime;
		
			username = spf.getString("username", "");
			password = spf.getString("password", "");
			password = StringUtil.decodeString(password);
			
			email = spf.getString("email", "");
			imgUrl = spf.getString("imageUrl", "");
			userid = spf.getString("userid", "0");
			registerDate = spf.getString("registerDate", "");
			state = spf.getInt("state", 0);
			
			weiboToken = spf.getString("weiboToken" + userid, "");
			weiboId = spf.getString("weiboId" + userid, "");
			weiboName = spf.getString("weiboName" + userid, "");
			weiboImgUrl = spf.getString("weiboImgUrl" + userid, "");
			weiboExpiresIn = spf.getString("weiboExpiresIn" + userid, "0");
			weiboAuthorTime = spf.getLong("weiboAuthorTime" + userid, 0);
					
			userInfo.setEmail(email);
			userInfo.setUserID(userid);
			userInfo.setRegisterDate(registerDate);
			userInfo.setUserImgUrl(imgUrl);
			userInfo.setUserName(username);
			userInfo.setUserState(state);
			userInfo.setPWD(password);
			
			userInfo.setWeiboAuthorTime(weiboAuthorTime);
			userInfo.setWeiboToken(weiboToken);
			userInfo.setWeiboId(weiboId);
			userInfo.setWeiboName(weiboName);
			userInfo.setWeiboExpiresIn(weiboExpiresIn);
			userInfo.setWeiboImgUrl(weiboImgUrl);
		
			Utility.setAuthorization(new Oauth2AccessTokenHeader());
//			Oauth2AccessToken;
			AccessToken token = new AccessToken(weiboToken, WeiBoData.app_secret);
	        token.setExpiresIn(weiboExpiresIn);
	        Weibo.getInstance().setAccessToken(token);
	        LogWorker.i("init token = " + token.getToken() + " uid = " + userid);
			boolean isTokenValid = isTokenValid();
			
		if (isTokenValid){
			setWeiboLogin(true);
		}
		if (!TextUtils.isEmpty(password)){
			setIsLogin(true);
		}
		if ( isLoginOut() ){
			setWeiboLogin(false);
			setIsLogin(false);
		}
		LogWorker.i("isTokenValid=" + isTokenValid + " password=" + password + " isLoginOut=" + isLoginOut());
		LogWorker.i("isWeiboLogin=" + isWeiboLogin() + " isLogin=" + isLogin());
		
	}
	public boolean isLogin(){
		if (isLoginOut())return false;
		return login;
		
	}
	public void setIsLogin(boolean login){
		this.login = login;
	}
	
	public UserInfo getUserInfo(){
		return userInfo;
	}

	public boolean isWeiboLogin() {
		if (isLoginOut())return false;
		return isWeiboLogin;
	}
	public void setWeiboLogin(boolean isWeiboLogin) {
		this.isWeiboLogin = isWeiboLogin;
	}
	public void setUserInfo(String username,String email,String imageUrl,
			String userid,String registerDate,int state,String weiboToken,String password,String weiboName,String weiboId){
		SharedPreferences spf = StorageManager.instance().getSPF(this);
		Editor edt = spf.edit();
		edt.putString("username", username);
		edt.putString("email", email);
		edt.putString("imageUrl", email);
		edt.putString("userid", userid);
		edt.putString("registerDate", registerDate);
		edt.putString("weiboToken", weiboToken);
		edt.putInt("state", state);
		edt.putString("password", password);
		edt.putString("weiboName", weiboName);
		edt.putString("weiboId", weiboId);
		edt.commit();
		this.login = true;
	}
	
	public void setUserInfo(UserInfo info){
		SharedPreferences spf = StorageManager.instance().getSPF(this);
		Editor edt = spf.edit();
		edt.putString("username", info.getUserName());
		edt.putString("password", info.getPWD());
		edt.putString("email", info.getEmail());
		edt.putString("imageUrl", info.getUserImgUrl());
		edt.putString("userid", info.getUserID());
		edt.putString("registerDate", info.getRegisterDate());
		edt.putInt("state", info.getUserState());
		
		edt.putString("weiboToken" + info.getUserID(), info.getWeiboToken());
		edt.putLong("weiboAuthorTime" + info.getUserID(), info.getWeiboAuthorTime());
		edt.putString("weiboName" + info.getUserID(), info.getWeiboName());
		edt.putString("weiboId" + info.getUserID(), info.getWeiboId());
		String exi = info.getWeiboExpiresIn();
        LogWorker.i("set weiboToken = " + info.getWeiboToken() + " uid = " + info.getUserID());
		if (TextUtils.isEmpty(exi)){
			exi = "0";
		}
		edt.putString("weiboExpiresIn" + info.getUserID(),exi);
		edt.putString("weiboImgUrl" + info.getUserID(), info.getWeiboImgUrl());
		edt.commit();
		
		AccessToken token = new AccessToken(info.getWeiboToken(), WeiBoData.app_secret);
        token.setExpiresIn(exi);
        Weibo.getInstance().setAccessToken(token);
        
		userInfo = info;
		setLoginOut(false);
		if (TextUtils.isEmpty(userInfo.getPWD())){
			setIsLogin(false);
		} else {
			setIsLogin(true);
		}
		if (isTokenValid()){
			LogWorker.i("isTokenValid true");
			setWeiboLogin(true);
		} else {
			LogWorker.i("isTokenValid false");
			setWeiboLogin(false);
		}
	}
	
	public void refreshTokenInfo(TokenInfo info){
		String uid = userInfo.getUserID();
		SharedPreferences spf = StorageManager.instance().getSPF(this);
		Editor edt = spf.edit();
		edt.putLong("weiboAuthorTime" + uid, Long.valueOf(info.getCreate_at()) * 1000 );
		if ( !TextUtils.isEmpty(info.getUid()) ){
			edt.putString("weiboId" + uid, info.getUid());
		}

		String exi = info.getExpire_in();
		if ( !TextUtils.isEmpty(exi) ){
			edt.putString("weiboExpiresIn" + uid,exi);
			Weibo.getInstance().getAccessToken().setExpiresIn(exi);
		}
		LogWorker.i("after refresh : weiboExpiresIn=" + exi);
		edt.commit();
	}
	public boolean isTokenValid(){
		Token mAccessToken = Weibo.getInstance().getAccessToken();
		LogWorker.i("currentTimeMillis = " + System.currentTimeMillis() + "uExpiresIn = " + userInfo.getWeiboExpiresIn()
				 + "authorTime" + userInfo.getWeiboAuthorTime()
				 + " mAccessToken.getExpiresIn()= " + mAccessToken.getExpiresIn());
		
        return Weibo.getInstance().isSessionValid();
	}
	public boolean unBangdingWeiboData(){
		SharedPreferences spf = StorageManager.instance().getSPF(this);
		Editor edt = spf.edit();
		edt.putString("weiboToken" + userInfo.getUserID(), "");
		edt.putLong("weiboAuthorTime" + userInfo.getUserID(), 0);
		edt.putString("weiboName" + userInfo.getUserID(), "");
		edt.putString("weiboId" + userInfo.getUserID(), "");
		edt.putString("weiboExpiresIn" + userInfo.getUserID(), "0");
		edt.putString("weiboImgUrl" + userInfo.getUserID(), "");
		setWeiboLogin(false);
		return edt.commit();
	}
	public boolean clearWeiboData(){
		Editor e = StorageManager.instance().getSPF(this).edit();
		e.putString("weiboId", "");
		isWeiboLogin = false;
		return e.commit();
	}
	public boolean isConnectInternet() {

		ConnectivityManager conManager = 
			(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}
}
