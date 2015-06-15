package com.scribblernotebooks.scribblernotebooks.HelperClasses;

/**
 * Created by Jibin_ism on 13-Jun-15.
 */
public class User {
    String name="", email="", mobile="", facebookId="",
            googleId="", coverImage="", profilePic="", password="", token="", mixpanelId="";

    public User(String name, String facebookId, String googleId, String coverImage, String profilePic, String password, String token, String mixpanelId) {
        this.name = name;
        this.facebookId = facebookId;
        this.googleId = googleId;
        this.coverImage = coverImage;
        this.profilePic = profilePic;
        this.password = password;
        this.token = token;
        this.mixpanelId = mixpanelId;
    }

    public User(String name, String email, String mobile, String token, String mixpanelId) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.token = token;
        this.mixpanelId = mixpanelId;
    }

    public String getName() {
        return name;
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

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
