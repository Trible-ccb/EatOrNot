package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class UserLoginParams extends BaseHttpParams {

	public UserLoginParams(String useremail,String password,boolean isWeiboLogin) {
		super(EaterApplication.HOST_URL + "loginm");
		addParams("type", "login");
		addParams("clientAgent", "Android");
		addParams("useremail", useremail);
		addParams("password", password);
		addParams("isWeiboLogin", "" + isWeiboLogin);
	}

}
