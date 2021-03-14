package com.returno.tradeit.models;
public class CategoryItem {

    private String post_title, post_desc, post_url;

    public CategoryItem(String post_title, String post_desc, String post_url) {
        this.post_title = post_title;
        this.post_desc = post_desc;
        this.post_url =post_url;
    }


    public CategoryItem() {
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

    public String getPost_url() {
        return post_url;
    }

    public String getPost_title() {
        return post_title;
    }

    public String getPost_desc() {
        return post_desc;
    }

}
