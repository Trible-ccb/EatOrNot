package com.android.eatingornot.activitys;

import java.util.Date;

import com.android.eatingornot.httpparams.UserLogParams;

import ccb.android.net.framkwork.HttpLoader;
import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.android.net.framkwork.SimpleAsynHttpDowload;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

public class UserLoger {

	
	public static void upload(Activity a,String logText,String actionType){
		String imei = ((TelephonyManager)a.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		String logTime = "" + new Date().getTime();
		Task logTask = HttpLoader.getInstance(a).getTask(new UserLogParams(imei, logTime, logText, actionType));
		//set some listener for this task
		
		SimpleAsynHttpDowload logLoader = new SimpleAsynHttpDowload(HttpLoader.getInstance(a));
		logLoader.execute(logTask);
	}
}
