package com.android.eatingornot.datamodel;

public class WeiBoUserData {

//	String uid;
	String authorTime;//授权时间、毫秒
	String life;//生命周期
	String accessToken;
//	String userName;
//	String weiboImgUrl;
	String  weiboId;
	String name;
	String profile_image_url;
	String email;
	String screen_name;
	public String getScreen_name() {
		return screen_name;
	}
	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}
	public String getWeiboId() {
		return weiboId;
	}
	public void setWeiboId(String weiboId) {
		this.weiboId = weiboId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProfile_image_url() {
		return profile_image_url;
	}
	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}


	public String getAuthorTime() {
		return authorTime;
	}
	public void setAuthorTime(String authorTime) {
		this.authorTime = authorTime;
	}

	public String getLifeTime() {
		return life;
	}
	public void setLifeTime(String lifeTime) {
		this.life = lifeTime;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String toString(){
		return  "weiboUserData：" 
				 + " name=" + name
				 + " weiboId=" + weiboId
				 + " weiboName=" + screen_name
				 + " weiboToken=" + accessToken
		 		 + " weiboExpiresIn=" + life;
		
	}
}
