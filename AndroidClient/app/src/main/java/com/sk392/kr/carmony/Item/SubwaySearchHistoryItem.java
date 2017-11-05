package com.sk392.kr.carmony.Item;

/**
 * Created by SK392 on 2017-05-24.
 */

public class SubwaySearchHistoryItem {
    private String subwaySearchText;
    private String subwaySearchDate;

    public String getSubwaySearchText() {
        return subwaySearchText;
    }

    public void setSubwaySearchText(String subwaySearchText) {
        this.subwaySearchText = subwaySearchText;
    }

    public String getSubwaySearchDate() {
        return subwaySearchDate;
    }

    public void setSubwaySearchDate(String subwaySearchDate) {
        this.subwaySearchDate = subwaySearchDate;
    }

    public SubwaySearchHistoryItem(String subwaySearchText, String subwaySearchDate) {

        this.subwaySearchText = subwaySearchText;
        this.subwaySearchDate = subwaySearchDate;
    }
}
