package com.scribblernotebooks.scribblernotebooks.HelperClasses;

/**
 * Created by Jibin_ism on 26-May-15.
 */
public class Notifications {

    int id;
    String text,url;

    public Notifications() {
        this.id=Integer.MIN_VALUE;
        this.text="";
        this.url="";
    }

    public Notifications(int id, String text, String url) {
        this.id=id;
        this.text=text;
        this.url=url;
    }

    public void setNotificationId(int id){
        this.id=id;
    }

    public void setNotificationText(String text){
        this.text=text;
    }

    public void setNotificationImgUrl(String url){
        this.url=url;
    }

    public int getNotificationId(){
        return this.id;
    }

    public String getNotificationText(){
        return this.text;
    }

    public String getNotificationImageUrl(){
        return this.url;
    }


}
