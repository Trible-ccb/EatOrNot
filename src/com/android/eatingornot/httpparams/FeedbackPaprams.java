package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class FeedbackPaprams extends BaseHttpParams {

	public FeedbackPaprams(String content,String contact,String uid) {
		super(EaterApplication.HOST_URL + "newcom");
		addParams("clientAgent", "Andoird");
		addParams("type", "feedback");
		addParams("content", content);
		addParams("contact", contact);
		addParams("uid", uid);
	}

}
