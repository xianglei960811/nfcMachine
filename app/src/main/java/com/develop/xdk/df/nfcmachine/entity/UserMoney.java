package com.develop.xdk.df.nfcmachine.entity;


/**
* @author df
* @version 创建时间：2018年7月18日 下午4:04:44
* @Description 类描述
*/
public class UserMoney extends PersonDossier{
	private double subsidymoney,cashmoney;

    public double getSubsidymoney() {
        return subsidymoney;
    }

    public double getCashmoney() {
        return cashmoney;
    }

    public void setSubsidymoney(double subsidymoney) {
        this.subsidymoney = subsidymoney;
    }

    public void setCashmoney(double cashmoney) {
        this.cashmoney = cashmoney;
    }
}
