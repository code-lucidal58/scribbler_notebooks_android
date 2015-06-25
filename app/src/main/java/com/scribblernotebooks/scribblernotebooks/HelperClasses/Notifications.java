package com.scribblernotebooks.scribblernotebooks.HelperClasses;

/**
 * Created by Jibin_ism on 26-May-15.
 */
public class Notifications {

    String id;
    String text,url;

    public Notifications() {
        this.id=String.valueOf(Integer.MIN_VALUE);
        this.text="";
        this.url="";
    }

    public Notifications(String  id, String text, String url) {
        this.id=id;
        this.text=text;
        this.url=url;
    }

    public void setNotificationId(String  id){
        this.id=id;
    }

    public void setNotificationText(String text){
        this.text=text;
    }

    public void setNotificationImgUrl(String url){
        this.url=url;
    }

    public String getNotificationId(){
        return this.id;
    }

    public String getNotificationText(){
        return this.text;
    }

    public String getNotificationImageUrl(){
        return this.url;
    }


}
