package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class ForgetPSWParams extends BaseHttpParams {

	public ForgetPSWParams(String email) {
		super(EaterApplication.HOST_URL + "emailsend");
		addParams("clientAgent", "Andoird");
		addParams("type", "sendEmail");
		addParams("useremail", email);
	}

}
