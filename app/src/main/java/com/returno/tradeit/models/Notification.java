package com.returno.tradeit.models;

import java.io.Serializable;

public class Notification implements Serializable {
    private String itemid,price,branch,title,notification_id;

    public Notification(String itemid, String price, String branch, String title,String notification_id) {
        this.itemid = itemid;
        this.price = price;
        this.branch = branch;
        this.title = title;
        this.notification_id=notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Notification() {
    }

}
