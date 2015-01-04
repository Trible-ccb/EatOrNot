package com.android.eatingornot.datamodel;

public class RequestRetMsg {

	boolean ActionCode;
	boolean errorMsg;
	
	public boolean isErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(boolean errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isActionCode() {
		return ActionCode;
	}

	public void setActionCode(boolean actionCode) {
		ActionCode = actionCode;
	} 
}
