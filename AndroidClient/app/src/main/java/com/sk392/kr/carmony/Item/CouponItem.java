package com.sk392.kr.carmony.Item;

import java.util.HashMap;

/**
 * Created by SK392 on 2016-12-10.
 */

public class CouponItem {
    private String type,couponId,userCouponId,disCredit,wDay,wEnd,limitDate;
    private HashMap<String,String> carShapeList;

    public CouponItem(String type, String couponId, String disCredit, String wDay, String wEnd, HashMap<String,String> carShapeList, String limitDate,String userCouponId) {
        this.type = type;
        this.couponId = couponId;
        this.disCredit = disCredit;
        this.wDay = wDay;
        this.wEnd = wEnd;
        this.carShapeList = carShapeList;
        this.limitDate = limitDate;
        this.userCouponId = userCouponId;
    }

    public String getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(String userCouponId) {
        this.userCouponId = userCouponId;
    }

    public String getwEnd() {

        return wEnd;
    }

    public void setwEnd(String wEnd) {
        this.wEnd = wEnd;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getDisCredit() {
        return disCredit;
    }

    public void setDisCredit(String disCredit) {
        this.disCredit = disCredit;
    }

    public String getwDay() {
        return wDay;
    }

    public void setwDay(String wDay) {
        this.wDay = wDay;
    }

    public HashMap<String,String> getCarShapeList() {
        return carShapeList;
    }

    public void setCarShapeList(HashMap<String,String> carShapeList) {
        this.carShapeList = carShapeList;
    }

    public String getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(String limitDate) {
        this.limitDate = limitDate;
    }
}
