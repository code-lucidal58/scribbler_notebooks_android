package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import java.io.Serializable;

/**
 * Created by Jibin_ism on 19-May-15.
 */
public class Deal implements Serializable {

    String id, title, category, shortDescription, imageUrl, longDescription;
    Boolean isFav,isFeatured;

    public Deal(){
        super();
    }

    public Deal(String id, String title, String category, String shortDescription, String imageUrl, Boolean isFav) {
        this(id, title, category, shortDescription, imageUrl, isFav, "");
    }

    public Deal(String id, String title, String category, String shortDescription, String imageUrl, Boolean isFav, String longDesciption) {
        this.title = title;
        this.category = category;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.isFav = isFav;
        this.id = id;
        this.longDescription = longDesciption;
    }

    public Deal (String id, String title, String category, String shortDescription, String imageUrl,String longDesciption, Boolean isFav,Boolean isFeatured)
    {
        this.title = title;
        this.category = category;
        this.shortDescription = shortDescription;
        this.longDescription = longDesciption;
        this.imageUrl = imageUrl;
        this.isFav = isFav;
        this.id = id;
        this.isFeatured=isFeatured;
    }


    /**
     * Sending statistics to the server about like and share
     */
    public void sendLikeStatus(Boolean isFav){
        //Code to synchronise with server
    }

    public void sendShareStatus(){
        //Code to synchronise with server
    }

    /**
     * Getter and Setter functions
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setIsFav(Boolean isFav) {
        this.isFav = isFav;
        sendLikeStatus(isFav);
    }

    public void setIsFeatured(Boolean isFeatured){
        this.isFeatured=isFeatured;
    }

    public void setLongDescription(String longDescription){ this.longDescription=longDescription; }

    public void setId(String id) {
        this.id = id;
    }

    public String getId(){ return this.id; }

    public String getTitle() {
        return this.title;
    }

    public String getCategory() {
        return this.category;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Boolean isFavorited() {
        return this.isFav;
    }

    public String getLongDescription(){return this.longDescription;}

    public Boolean isFeatured()
    {
        return this.isFeatured;
    }

}
