package com.android.eatingornot.weibo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import ccb.android.net.framkwork.HttpLoader;
import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.android.net.framkwork.HttpLoader.dataParser;
import ccb.android.net.framkwork.HttpLoader.downLoadListener;
import ccb.android.net.framkwork.SimpleAsynHttpDowload;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;

import com.android.eatingornot.activitys.EaterApplication;
import com.android.eatingornot.activitys.R;
import com.android.eatingornot.datamodel.UserInfo;
import com.android.eatingornot.datapraser.HttpDataPraser;
import com.android.eatingornot.httpparams.BangdinglParams;
import com.android.eatingornot.httpparams.UnBangdinglParams;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.OldShareActivity;
import com.weibo.net.ShareActivity;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class WeiboEntity implements WeiboHandler {
    private Activity mActivity;
    private Weibo mWeibo;
    private Notification mNotification;
    Class callBackClass;
    public WeiboEntity(Activity activity,Class callback) {
        mActivity = activity;
        callBackClass = callback;
        mNotification = new Notification(mActivity);
        mWeibo = Weibo.getInstance();
    }

    @Override
    public void login() {
    	Utility.setAuthorization(new Oauth2AccessTokenHeader());
        mWeibo.setupConsumerConfig(WeiBoData.weibo_appkey,
        		WeiBoData.app_secret);
        // Oauth2.0
        // 隐式授权认证方式
        mWeibo.setRedirectUrl(WeiBoData.app_redirect);// 此处回调页内容应该替换为与appkey对应的应用回调页
        // 对应的应用回调页可在�?��者登陆新浪微博开发平台之后，
        // 进入我的应用--应用详情--应用信息--高级信息--授权设置--应用回调页进行设置和查看�?
        // 应用回调页不可为�?
        mWeibo.authorize(mActivity, new AuthDialogListener(mActivity,callBackClass));
    }

    @Override
    public boolean share(String content, String picPath) {
    	LogWorker.i("share...");
        File picFile = new File(picPath);
        if (!picFile.exists()) {
            mNotification.toast("图片" + picPath + "不存在！");
            picPath = null;
        }
        try {

            mWeibo.share2weibo(mActivity, mWeibo.getAccessToken().getToken(),
                    mWeibo.getAccessToken().getSecret(), content, picPath);
            Intent i = new Intent(mActivity, ShareActivity.class);
            mActivity.startActivity(i);

        } catch (WeiboException e) {
        	LogWorker.i(e.getMessage());
            e.printStackTrace();
        } finally {

        }
        return false;
    }

    @Override
    public String getPublicTimeline(Weibo weibo) throws MalformedURLException,
            IOException, WeiboException {
        String url = Weibo.SERVER + "statuses/public_timeline.json";
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", Weibo.getAppKey());
        String rlt = weibo.request(mActivity, url, bundle, "GET",
                mWeibo.getAccessToken());
        return rlt;
    }
    
    public String showUserInfo(Context context, String uid) throws MalformedURLException, IOException, WeiboException {
        String rlt = "";
        try {
            Weibo mWeibo = Weibo.getInstance();
            String url = Weibo.SERVER + "users/show.json";
            LogWorker.i(" Weibo.showUser().url " + url);
            WeiboParameters bundle = new WeiboParameters();
            bundle.add("source", Weibo.getAppKey());
            bundle.add("access_token", mWeibo.getAccessToken().getToken());
//            bundle.add("access_token", Weibo.getInstance().getAccessToken()
//                    .getToken());
            bundle.add("uid", uid);
            rlt = mWeibo.request(context, url, bundle, "GET",
                    mWeibo.getAccessToken());

            LogWorker.i("Weibo.showUser().rlt " + rlt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rlt;
    }
    public String showTokenInfo() throws WeiboException{
    	String TokenInfoURL =  "https://api.weibo.com/oauth2/get_token_info";
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("access_token", mWeibo.getAccessToken().getToken());
        String rlt;
        rlt = mWeibo.request(mActivity, TokenInfoURL, bundle, "POST", mWeibo.getAccessToken());
        LogWorker.i("showTokenInfo() =  " + rlt);
    	return rlt;
    }
    public String createToUs() throws WeiboException{
    	String createURL =  "https://api.weibo.com/2/friendships/create.json";
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("access_token", mWeibo.getAccessToken().getToken());
        bundle.add("source", WeiBoData.weibo_appkey);
        bundle.add("uid", "3328404390");
        String rlt;
        rlt = mWeibo.request(mActivity, createURL, bundle, "POST", mWeibo.getAccessToken());
        LogWorker.i("createUs() =  " + rlt);
    	return rlt;
    }
    public String destoryUs() throws WeiboException{
    	String createURL =  "https://api.weibo.com/2/friendships/destroy.json";
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("access_token", mWeibo.getAccessToken().getToken());
        bundle.add("source", WeiBoData.weibo_appkey);
        bundle.add("uid", "3328404390");
        String rlt;
        rlt = mWeibo.request(mActivity, createURL, bundle, "POST", mWeibo.getAccessToken());
        LogWorker.i("destoryUs() =  " + rlt);
    	return rlt;
    }
    @Override
    public String upload(Weibo weibo, String source, String file,
            String status, String lon, String lat) throws WeiboException {
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", source);
        bundle.add("pic", file);
        bundle.add("status", status);
        if (!TextUtils.isEmpty(lon)) {
            bundle.add("lon", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            bundle.add("lat", lat);
        }
        String rlt = "";
        String url = Weibo.SERVER + "statuses/upload.json";
        try {
            rlt = weibo.request(mActivity, url, bundle,
                    Utility.HTTPMETHOD_POST, mWeibo.getAccessToken());
        } catch (WeiboException e) {
            throw new WeiboException(e);
        }
        return rlt;
    }

    @Override
    public String update(Weibo weibo, String source, String status,
            String lon, String lat) throws WeiboException {
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", source);
        bundle.add("status", status);
        if (!TextUtils.isEmpty(lon)) {
            bundle.add("lon", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            bundle.add("lat", lat);
        }
        String rlt = "";
        String url = Weibo.SERVER + "statuses/update.json";
        rlt = weibo.request(mActivity, url, bundle, Utility.HTTPMETHOD_POST,
                mWeibo.getAccessToken());
        return rlt;
    }

	@Override
	public void bangding(String uid) {
		// TODO Auto-generated method stub
    	Utility.setAuthorization(new Oauth2AccessTokenHeader());
        mWeibo.setupConsumerConfig(WeiBoData.weibo_appkey,
        		WeiBoData.app_secret);
        // Oauth2.0
        // 隐式授权认证方式
        mWeibo.setRedirectUrl(WeiBoData.app_redirect);// 此处回调页内容应该替换为与appkey对应的应用回调页
        // 对应的应用回调页可在�?��者登陆新浪微博开发平台之后，
        // 进入我的应用--应用详情--应用信息--高级信息--授权设置--应用回调页进行设置和查看�?
        // 应用回调页不可为�?
        mWeibo.authorize(mActivity, new BangdingDialogListener(mActivity,callBackClass,uid));
	}

}
