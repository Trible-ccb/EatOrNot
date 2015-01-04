package com.android.eatingornot.datamodel;

import ccb.java.android.utils.LogWorker;

public class UserInfo {

	String userID;
	String userName;
	String email;
	String PWD;
	String registerDate;
	int userState;
	String userImgUrl;
	int errorMsg;//0正常，1注册邮箱重复,2登录密码错误
	
	String weiboId;
	String weiboName;
	String weiboToken;
	String weiboExpiresIn;
	String weiboImgUrl;
	long weiboAuthorTime;
	
	public long getWeiboAuthorTime() {
		return weiboAuthorTime;
	}
	public void setWeiboAuthorTime(long weiboAuthorTime) {
		this.weiboAuthorTime = weiboAuthorTime;
	}
	public String getWeiboExpiresIn() {
		return weiboExpiresIn;
	}
	public void setWeiboExpiresIn(String weiboExpiresIn) {
		this.weiboExpiresIn = weiboExpiresIn;
	}
	public String getWeiboImgUrl() {
		return weiboImgUrl;
	}
	public void setWeiboImgUrl(String weiboImgUrl) {
		this.weiboImgUrl = weiboImgUrl;
	}
	public String getWeiboId() {
		return weiboId;
	}
	public void setWeiboId(String weiboId) {
		this.weiboId = weiboId;
	}
	public String getWeiboName() {
		return weiboName;
	}
	public void setWeiboName(String weiboName) {
		this.weiboName = weiboName;
	}
	public int getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(int errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPWD() {
		return PWD;
	}
	public void setPWD(String pWD) {
		PWD = pWD;
	}
	public String getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}
	public String getWeiboToken() {
		return weiboToken;
	}
	public void setWeiboToken(String weiboToken) {
		this.weiboToken = weiboToken;
	}
	public int getUserState() {
		return userState;
	}
	public void setUserState(int userState) {
		this.userState = userState;
	}
	public String getUserImgUrl() {
		return userImgUrl;
	}
	public void setUserImgUrl(String userImgUrl) {
		this.userImgUrl = userImgUrl;
	}
	
	public String toString(){
		return  "userInfo： id=" + userID 
				 + " name=" + userName
				 + " weiboId=" + weiboId
				 + " weiboName=" + weiboName
				 + " weiboToken=" + weiboToken
		 		 + " weiboExpiresIn=" + weiboExpiresIn;
		
	}
}
