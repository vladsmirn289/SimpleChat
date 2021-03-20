package com.webSocket.simpleChat.model;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class UserInfo {
    @NotNull
    @ColumnDefault("")
    private String realName = "";

    @NotNull
    @ColumnDefault("user-male-circle.png")
    private String avatar = "user-male-circle.png";

    @NotNull
    @ColumnDefault("")
    private String bio = "";

    @NotNull
    @ColumnDefault("")
    private String birthday = "";

    @NotNull
    @ColumnDefault("")
    private String country = "";

    @NotNull
    @ColumnDefault("")
    private String phoneNumber = "";

    public UserInfo() {

    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
