package com.example.user.dhakabd.model;

import com.google.firebase.database.Exclude;

public class Content {

    private String image;
    private String title;
    private String description;
    private String phone_number;
    private String website;

    public String getMap() {
        return map;
    }

    public String getPhone_number() {
        return phone_number;
    }

    private String map;

    @Exclude
    private String content_id;

    public Content(){}

    public Content(String image, String title, String description, String phone_number, String website) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.phone_number = phone_number;
        this.website = website;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhone_number_text(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }
}
