package com.develop.xdk.df.nfcmachine.entity;

public class LocalUser {
    private  String cardID,name,data,accontid;
    private  double cashMOney,subsidyMoney;
    private int isLoss;//0：未挂失；1：挂失状态

    public String getAccontid() {

        return accontid;
    }

    public void setAccontid(String accontid) {
        this.accontid = accontid;
    }

    public int getIsLoss() {
        return isLoss;
    }

    public void setIsLoss(int isLoss) {
        this.isLoss = isLoss;
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

    public String getName() {
        return name;
    }

    public double getCashMOney() {
        return cashMOney;
    }

    public double getSubsidyMoney() {
        return subsidyMoney;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCashMOney(double cashMOney) {
        this.cashMOney = cashMOney;
    }

    public void setSubsidyMoney(double subsidyMoney) {
        this.subsidyMoney = subsidyMoney;
    }

    @Override
    public String toString() {
        return "LocalUser{" +
                ", cardID=" + cardID +
                ", name=" + name +
                ",subsidyMoney =" + subsidyMoney +
                ", cashMOney=" + cashMOney +
                ", data=" + data +
                "}";
    }
}
