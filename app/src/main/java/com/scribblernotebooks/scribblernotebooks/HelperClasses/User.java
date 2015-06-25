package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import java.util.List;

/**
 * Created by Jibin_ism on 13-Jun-15.
 */
public class User {
    String name="", email="", mobile="", coverImage="", profilePic="", token="", mixpanelId="", location="", college="";
    List<String> likedDeals;

    public User(String name, String email, String mobile, String token, String mixpanelId) {
        this.name = name;
        this.email = email;

        this.token = token;
        this.mixpanelId = mixpanelId;
        this.mobile = mobile;
    }

    public User() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getLikedDeals() {
        return likedDeals;
    }

    public void setLikedDeals(List<String> likedDeals) {
        this.likedDeals = likedDeals;
    }

    public User(String name, String email, String mobile, String coverImage, String profilePic, String token, String mixpanelId) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.coverImage = coverImage;
        this.profilePic = profilePic;
        this.token = token;
        this.mixpanelId = mixpanelId;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getName() {
        return name ;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMixpanelId() {
        return mixpanelId;
    }

    public void setMixpanelId(String mixpanelId) {
        this.mixpanelId = mixpanelId;
    }
}
