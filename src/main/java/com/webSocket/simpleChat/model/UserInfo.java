package com.webSocket.simpleChat.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class UserInfo {
    @NotNull
    @Column(columnDefinition = "varchar(255) not null default ''")
    private String realName = "";

    @NotNull
    @Column(columnDefinition = "varchar(255) not null default 'user-male-circle.png'")
    private String avatar = "user-male-circle.png";

    @NotNull
    @Column(columnDefinition = "varchar(255) not null default ''")
    private String bio = "";

    @NotNull
    @Column(columnDefinition = "varchar(255) not null default ''")
    private String birthday = "";

    @NotNull
    @Column(columnDefinition = "varchar(255) not null default ''")
    private String country = "";

    @NotNull
    @Column(columnDefinition = "varchar(255) not null default ''")
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
