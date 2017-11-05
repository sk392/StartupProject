package com.sk392.kr.carmony.Item;

/**
 * Created by sk392 on 2016-09-08.
 */
public class OptionItem {
    private int image;
    private String titleKo,titleEn;
    private Boolean isChecked = false;
    public int getImage() {
        return this.image;
    }

    public OptionItem(int image, String titleKo, String titleEn, Boolean isChecked) {
        this.image = image;
        this.titleKo = titleKo;
        this.titleEn = titleEn;
        this.isChecked = isChecked;
    }

    public void setImage(int image) {
        this.image = image;

    }

    public String getTitleKo() {
        return titleKo;
    }

    public void setTitleKo(String titleKo) {
        this.titleKo = titleKo;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean checked) {
        isChecked = checked;
    }
}
