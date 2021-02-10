package com.returno.tradeit.models;

public class Item {
private String itemId;
private String itemName;
private String itemDescription;
private int itemPrice;
private String itemTag;
private String itemImage;
private String itemPosterId;
private String itemCategory;

    public Item(String itemId, String itemName, String itemDescription, int itemPrice, String itemTag, String itemImage, String itemPosterId,String itemCategory) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemPrice = itemPrice;
        this.itemTag = itemTag;
        this.itemImage = itemImage;
        this.itemPosterId = itemPosterId;
        this.itemCategory=itemCategory;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public String getItemTag() {
        return itemTag;
    }

    public String getItemImage() {
        return itemImage;
    }

    public String getItemPosterId() {
        return itemPosterId;
    }
}
