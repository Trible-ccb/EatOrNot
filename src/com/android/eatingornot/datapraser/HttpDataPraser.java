package com.android.eatingornot.datapraser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.android.eatingornot.datamodel.CreateUsAtWeibo;
import com.android.eatingornot.datamodel.FoodOfDiseaseBean;
import com.android.eatingornot.datamodel.RequestRetMsg;
import com.android.eatingornot.datamodel.Food;
import com.android.eatingornot.datamodel.TokenInfo;
import com.android.eatingornot.datamodel.UserInfo;
import com.android.eatingornot.datamodel.WeiBoUserData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HttpDataPraser {

	//解析食物详细信息
	public static FoodOfDiseaseBean getFoodDetailBean(InputStream is){
		FoodOfDiseaseBean mBean;
		Gson gson = new Gson();
		mBean = gson.fromJson(new InputStreamReader(is),
				FoodOfDiseaseBean.class);
		return mBean;
	}
	
	public static List<Food> getFoodList(InputStream is){
		List<Food> foods = new ArrayList<Food>();
		Gson gson = new Gson();
		foods = gson.fromJson(new InputStreamReader(is), new TypeToken<List<Food>>(){}.getType());
		return foods;
	}
	public static UserInfo getUserInfo(InputStream is){
		UserInfo mBean;
		Gson gson = new Gson();
		mBean = gson.fromJson(new InputStreamReader(is),
				UserInfo.class);
		return mBean;
	}
	public static WeiBoUserData getWeiboUserData(String is){
		WeiBoUserData mBean;
		Gson gson = new Gson();
		mBean = gson.fromJson(is,WeiBoUserData.class);
		return mBean;
	}
	public static TokenInfo getTokenInfo(String is){
		TokenInfo mBean;
		Gson gson = new Gson();
		mBean = gson.fromJson(is,TokenInfo.class);
		return mBean;
	}
	public static RequestRetMsg getFavorRetMsg(InputStream is){
		RequestRetMsg mBean;
		Gson gson = new Gson();
		mBean = gson.fromJson(new InputStreamReader(is),
				RequestRetMsg.class);
		return mBean;
	}
	
	public static RequestRetMsg getSendEmailMsg(InputStream is ){
		RequestRetMsg mBean;
		Gson gson = new Gson();
		mBean = gson.fromJson(new InputStreamReader(is),
				RequestRetMsg.class);
		return mBean;
	}
	public static CreateUsAtWeibo getCreateMsg(String is){
		CreateUsAtWeibo mBean;
		Gson gson = new Gson();
		mBean = gson.fromJson(is,CreateUsAtWeibo.class);
		return mBean;
	}
}
