package com.develop.xdk.df.nfcmachine.entity;

/**
* @author df
* @version 创建时间：2018年7月18日 上午11:19:47
* @Description 类描述
*/
public class ComsumeParam extends BaseParam{
	private double spendMoney;
	private String computer,spendTime,windowNumber,spendDate,authCode;

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public double getSpendMoney() {
		return spendMoney;
	}
	public void setSpendMoney(double spendMoney) {
		this.spendMoney = spendMoney;
	}
	public String getComputer() {
		return computer;
	}
	public String getSpendDate() {
		return spendDate;
	}
	public void setSpendDate(String spendDate) {
		this.spendDate = spendDate;
	}
	public void setComputer(String computer) {
		this.computer = computer;
	}
	public String getSpendTime() {
		return spendTime;
	}
	public void setSpendTime(String spendTime) {
		this.spendTime = spendTime;
	}
	public String getWindowNumber() {
		return windowNumber;
	}
	public void setWindowNumber(String windowNumber) {
		this.windowNumber = windowNumber;
	}
	
}
