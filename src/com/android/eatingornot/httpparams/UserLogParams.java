package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class UserLogParams extends BaseHttpParams {

	public UserLogParams(String imei,String logTime,String logText,String actionType) {
		super(EaterApplication.HOST_URL + "newbea");
		addParams("type", "userLog");
		addParams("clientAgent", "Android");
		addParams("IMEI", imei);
		addParams("logTime", logTime);
		addParams("logText", logText);
		addParams("actionType", actionType);
	}

}
