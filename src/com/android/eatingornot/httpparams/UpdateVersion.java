package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class UpdateVersion extends BaseHttpParams {

	public UpdateVersion(String curversion) {
		super(EaterApplication.HOST_URL);
		addParams("type", "updateVersion");
		addParams("clientAgent", "Android");
		addParams("version", curversion);
		// TODO Auto-generated constructor stub
	}

	
}
