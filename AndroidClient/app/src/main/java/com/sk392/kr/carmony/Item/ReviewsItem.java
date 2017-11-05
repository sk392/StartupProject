package com.sk392.kr.carmony.Item;

/**
 * Created by SK392 on 2016-11-07.
 */

public class ReviewsItem {
    private String content,date,name,image;
    private float score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ReviewsItem(String content, String date, String image,String name,float score) {
        this.content = content;
        this.date = date;
        this.image = image;
        this.name = name;
        this.score =score;
    }
}
