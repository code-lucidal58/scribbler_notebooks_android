package com.scribblernotebooks.scribblernotebooks.HelperClasses;

/**
 * Created by Jibin_ism on 31-Jul-15.
 */
public class College {
    String id="",name="";

    public College(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
