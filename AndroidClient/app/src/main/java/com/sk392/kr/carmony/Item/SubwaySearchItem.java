package com.sk392.kr.carmony.Item;

/**
 * Created by sk392 on 2016-10-14.
 */

public class SubwaySearchItem {
    private String subwayLine, subwayName, subwayCode, subwayExCode, subwayId;

    public String getSubwayId() {
        return subwayId;
    }

    public void setSubwayId(String subwayId) {
        this.subwayId = subwayId;
    }

    public SubwaySearchItem(String subwayLine, String subwayName, String subwayCode, String subwayExCode, String subwayId) {
        this.subwayLine = subwayLine;
        this.subwayName = subwayName;
        this.subwayCode = subwayCode;
        this.subwayExCode = subwayExCode;
        this.subwayId = subwayId;
    }

    public String getSubwayCode() {

        return subwayCode;
    }

    public void setSubwayCode(String subwayCode) {
        this.subwayCode = subwayCode;
    }

    public String getSubwayExCode() {
        return subwayExCode;
    }

    public void setSubwayExCode(String subwayExCode) {
        this.subwayExCode = subwayExCode;
    }

    public String getSubwayLine() {
        return subwayLine;
    }

    public void setSubwayLine(String subwayLine) {
        this.subwayLine = subwayLine;
    }

    public String getSubwayName() {
        return subwayName;
    }

    public void setSubwayName(String subwayName) {
        this.subwayName = subwayName;
    }
}
