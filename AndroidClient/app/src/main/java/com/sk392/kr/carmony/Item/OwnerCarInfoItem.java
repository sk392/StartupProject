package com.sk392.kr.carmony.Item;

/**
 * Created by sk392 on 2016-10-18.
 */

public class OwnerCarInfoItem {
    private String carImage;
    private String carModel,carYear,carinfoId;

    public OwnerCarInfoItem(String carImage,String carModel, String carYear,String carinfoId){
        this.carImage = carImage;
        this.carModel = carModel;
        this.carYear = carYear;
        this.carinfoId = carinfoId;

    }

    public String getCarinfoId() {
        return carinfoId;
    }

    public void setCarinfoId(String carinfoId) {
        this.carinfoId = carinfoId;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarYear() {
        return carYear;
    }

    public void setCarYear(String carYear) {
        this.carYear = carYear;
    }
}
