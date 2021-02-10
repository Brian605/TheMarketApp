package com.returno.tradeit.models;
public class CategoryItem {

    private String post_title, post_desc, post_url, uid,username;

    public CategoryItem(String post_title, String post_desc, String post_url, String uid, String username) {
        this.post_title = post_title;
        this.post_desc = post_desc;
        this.post_url =post_url;
        this.username = uid;
        this.uid=username;
    }

    public CategoryItem() {
    }

    public String getPrice(){
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPost_desc(String post_desc) {
        this.post_desc = post_desc;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public void setPost_url(String post_url) {
        this.post_url = post_url;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPost_url() {
        return post_url;
    }

    public String getPost_title() {
        return post_title;
    }

    public String getDesc() {
        return post_desc;
    }

    public String getPost_desc() {
        return post_desc;
    }

    public String getUid() {
        return uid;
    }

}
