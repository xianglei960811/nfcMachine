package com.develop.xdk.df.nfcmachine.entity;

public class AccountUser {
    String cardID,data,isHandl,name;
    double cashMoney,subsidyMoney,cousumeMoney;
    int id,isOnline;

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    public double getCashMoney() {
        return cashMoney;
    }

    public void setCashMoney(double cashMoney) {
        this.cashMoney = cashMoney;
    }

    public double getSubsidyMoney() {
        return subsidyMoney;
    }

    public void setSubsidyMoney(double subsidyMoney) {
        this.subsidyMoney = subsidyMoney;
    }

    public double getCousumeMoney() {
        return cousumeMoney;
    }

    public void setCousumeMoney(double cousumeMoney) {
        this.cousumeMoney = cousumeMoney;
    }

    public String getIsHandl() {
        return isHandl;
    }

    public void setIsHandl(String isHandl) {
        this.isHandl = isHandl;
    }
}
