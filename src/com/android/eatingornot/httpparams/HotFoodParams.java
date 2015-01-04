package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class HotFoodParams extends BaseHttpParams {

	public HotFoodParams(String diseaseName,int limitNum){
		super(EaterApplication.HOST_URL + "hotfoodm");
		addParams("clientAgent", "Andoird");
		addParams("type", "hotFood");
		addParams("diseaseName", diseaseName);
		addParams("limit", "" + limitNum);
	}
}
