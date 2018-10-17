package com.develop.xdk.df.nfcmachine.entity;

public class OffLineParam extends  ComsumeParam{
    String isHandle;
Double pdCashmoney,pdSubsidymoney;
    public String getIsHandle() {
        return isHandle;
    }

    public Double getPdCashmoney() {
        return pdCashmoney;
    }

    public void setPdCashmoney(Double pdCashmoney) {
        this.pdCashmoney = pdCashmoney;
    }

    public Double getPdSubsidymoney() {
        return pdSubsidymoney;
    }

    public void setPdSubsidymoney(Double pdSubsidymoney) {
        this.pdSubsidymoney = pdSubsidymoney;
    }

    public void setIsHandle(String isHandle) {
        this.isHandle = isHandle;
    }
}
