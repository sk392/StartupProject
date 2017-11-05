package com.sk392.kr.carmony.Item;

/**
 * Created by sk392 on 2016-10-12.
 */

public class CarModelItem {
    private String model;
    private boolean ischeck;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public CarModelItem(String model, boolean ischeck) {
        this.model = model;
        this.ischeck = ischeck;
    }

    public boolean getIscheck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }
}
