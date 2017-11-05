package com.sk392.kr.carmony.Item;

/**
 * Created by sk392 on 2016-09-20.
 */
public class ResultItem {
    private float rationgCar;

    public String getLateCost() {
        return lateCost;
    }

    public void setLateCost(String lateCost) {
        this.lateCost = lateCost;
    }

    private float rationPerson;
    private String tvCarModel, tvCarYears, tvExCost, tvExOnedayCost, tvExDeliveryCost, imgProfile, imgCar, carId, ownerName, lateCost, isOneday;

    public ResultItem(float rationgCar, float rationPerson, String tvCarModel, String tvCarYears
            , String tvExCost, String imgProfile, String imgCar, String carId, String ownerName
            , String tvExOnedayCost, String tvExDeliveryCost, String isOneday, String lateCost) {
        this.rationgCar = rationgCar;
        this.rationPerson = rationPerson;
        this.tvCarModel = tvCarModel;
        this.tvCarYears = tvCarYears;
        this.tvExCost = tvExCost;
        this.imgProfile = imgProfile;
        this.imgCar = imgCar;
        this.carId = carId;
        this.ownerName = ownerName;
        this.tvExDeliveryCost = tvExDeliveryCost;
        this.tvExOnedayCost = tvExOnedayCost;
        this.isOneday = isOneday;
        this.lateCost = lateCost;
    }

    public String getIsOneday() {
        return isOneday;
    }

    public void setIsOneday(String isOneday) {
        this.isOneday = isOneday;
    }

    public String getTvExOnedayCost() {
        return tvExOnedayCost;
    }

    public void setTvExOnedayCost(String tvExOnedayCost) {
        this.tvExOnedayCost = tvExOnedayCost;
    }

    public String getTvExDeliveryCost() {
        return tvExDeliveryCost;
    }

    public void setTvExDeliveryCost(String tvExDeliveryCost) {
        this.tvExDeliveryCost = tvExDeliveryCost;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public float getRationgCar() {

        return rationgCar;

    }

    public void setRationgCar(float rationgCar) {
        this.rationgCar = rationgCar;
    }

    public float getRationPerson() {
        return rationPerson;
    }

    public void setRationPerson(float rationPerson) {
        this.rationPerson = rationPerson;
    }

    public String getTvCarModel() {
        return tvCarModel;
    }

    public void setTvCarModel(String tvCarModel) {
        this.tvCarModel = tvCarModel;
    }

    public String getTvCarYears() {
        return tvCarYears;
    }

    public void setTvCarYears(String tvCarYears) {
        this.tvCarYears = tvCarYears;
    }

    public String getTvExCost() {
        return tvExCost;
    }

    public void setTvExCost(String tvExCost) {
        this.tvExCost = tvExCost;
    }

    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }

    public String getImgCar() {
        return imgCar;
    }

    public void setImgCar(String imgCar) {
        this.imgCar = imgCar;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

}
