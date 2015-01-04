package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class FoodDetailParams extends BaseHttpParams {

	public FoodDetailParams(String foodName,String diseaseName) {
		super(EaterApplication.HOST_URL + "viewdfoodm");
		addParams("clientAgent", "Android");
		addParams("type", "foodDetail");
		addParams("foodName", foodName);
		addParams("diseaseName", diseaseName);
	}
	

}
