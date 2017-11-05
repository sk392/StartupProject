package com.sk392.kr.carmony.Item;

/**
 * Created by SK392 on 2016-11-08.
 */

public class OwnerScheduleItem {
    //type =1은 아무런 일정이 없는 날, 2은 일정이 있는 날을 의미한다.
    private String dateMonth,dateDay,type;
    private int startHour,endHour;




    public String getType() {
        return type;
    }

    public String getDateMonth() {
        return dateMonth;
    }

    public void setDateMonth(String dateMonth) {
        this.dateMonth = dateMonth;
    }

    public String getDateDay() {
        return dateDay;
    }

    public void setDateDay(String dateDay) {
        this.dateDay = dateDay;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }
    public OwnerScheduleItem(String type,String dateMonth,String dateDay) {
        this.type = type;
        this.dateDay = dateDay;
        this.dateMonth = dateMonth;
    }
    public OwnerScheduleItem(String type,String dateMonth,String dateDay, int startHour, int endHour) {
        this.type = type;
        this.dateDay = dateDay;
        this.dateMonth = dateMonth;
        this.startHour = startHour;
        this.endHour = endHour;
    }
}
