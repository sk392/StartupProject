package com.sk392.kr.carmony.Item;

import java.util.List;

/**
 * Created by sk392 on 2016-09-23.
 */
public class ReservationItem {
    public int type, reservationType;
    public String text;
    public List<ReservationItem> invisibleChildren;
    private String carModel, carYear, resStart, resEnd, carId, reservationId, imageCar, cost, deliveryCost, lateCost, ownerId, onedayCost;

    public String getIsdelivery() {
        return isdelivery;
    }

    public void setIsdelivery(String isdelivery) {
        this.isdelivery = isdelivery;
    }

    public String getIsoneday() {
        return isoneday;
    }

    public void setIsoneday(String isoneday) {
        this.isoneday = isoneday;
    }

    String isdelivery;
    String isoneday;
    String reservationState;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getReservationType() {
        return reservationType;
    }

    public void setReservationType(int reservationType) {
        this.reservationType = reservationType;
    }

    String userId;

    public String getCarModel() {
        return this.carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getImageCar() {
        return this.imageCar;
    }

    public void setImageCar(String imageCar) {
        this.imageCar = imageCar;
    }

    public String getResEnd() {
        return this.resEnd;
    }

    public void setResEnd(String resEnd) {
        this.resEnd = resEnd;
    }

    public String getCarYear() {
        return this.carYear;
    }

    public void setCarYear(String resNum) {
        this.carYear = resNum;
    }

    public String getResStart() {
        return this.resStart;
    }

    public void setResStart(String resStart) {
        this.resStart = resStart;
    }


    public int getType() {

        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ReservationItem> getInvisibleChildren() {
        return invisibleChildren;
    }

    public void setInvisibleChildren(List<ReservationItem> invisibleChildren) {
        this.invisibleChildren = invisibleChildren;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationState() {
        return reservationState;
    }

    public void setReservationState(String reservationState) {
        this.reservationState = reservationState;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ReservationItem(int type, String imageCar, String carModel, String carYear, String resStart
            , String resEnd, String carId, String reservationId, int reservationType, String userId
            , String cost, String deliveryCost, String onedayCost, String lateCost, String isdelivery, String isoneday
            , String reservationState, String ownerId) {
        this.carModel = carModel;
        this.imageCar = imageCar;
        this.carYear = carYear;
        this.ownerId = ownerId;

        this.resStart = resStart;
        this.resEnd = resEnd;
        this.type = type;
        this.carId = carId;
        this.reservationId = reservationId;
        this.reservationType = reservationType;
        this.userId = userId;
        this.lateCost = lateCost;
        this.deliveryCost = deliveryCost;
        this.cost = cost;
        this.onedayCost = onedayCost;
        this.isdelivery = isdelivery;
        this.isoneday = isoneday;
        this.reservationState = reservationState;

    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(String deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public String getLateCost() {
        return lateCost;
    }

    public void setLateCost(String lateCost) {
        this.lateCost = lateCost;
    }

    public String getOnedayCost() {
        return onedayCost;
    }

    public void setOnedayCost(String onedayCost) {
        this.onedayCost = onedayCost;
    }

    public ReservationItem(int type, String text) {
        this.type = type;
        this.text = text;
    }
}