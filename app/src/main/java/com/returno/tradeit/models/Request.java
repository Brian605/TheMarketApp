package com.returno.tradeit.models;

public class Request {
    private static Request request;
    private String userName;
    private String userPhone;
    private String requestItem;
    private String userId;
    private String requestId;

    public Request(String userName, String userPhone, String requestItem, String userId, String requestId) {
        this.userName = userName;
        this.userPhone = userPhone;
        this.requestItem = requestItem;
        this.userId = userId;
        this.requestId = requestId;
    }
    public Request(){

    }

    public static Request getInstance(){
        if (request ==null){
            request =new Request();
        }

        return request;
    }

    public Request withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public static Request getRequest() {
        return request;
    }


    public String getUserName() {
        return userName;
    }

    public Request withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public Request withUserPhone(String userPhone) {
        this.userPhone = userPhone;
        return this;
    }

    public String getRequestItem() {
        return requestItem;
    }

    public Request withRequestItem(String requestItem) {
        this.requestItem = requestItem;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Request withUserId(String userId) {
        this.userId = userId;
        return this;
    }
    public Request build(){
        return this;
    }
}
