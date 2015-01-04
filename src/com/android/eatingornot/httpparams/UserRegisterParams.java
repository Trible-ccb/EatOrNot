package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class UserRegisterParams extends BaseHttpParams {

	public UserRegisterParams(String username,String useremail,String password){
		super(EaterApplication.HOST_URL + "registerm");
		addParams("type", "register");
		addParams("clientAgent", "Android");
		addParams("username", username);
		addParams("useremail", useremail);
		addParams("password", password);
	}
}
