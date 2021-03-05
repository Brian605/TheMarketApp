package com.returno.tradeit.models;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class Item {
private String itemId;
private String itemName;
private String itemDescription;
private int itemPrice;
private String itemTag;
private String itemImage;
private String itemPosterId;
private String itemCategory;
private Uri itemImageUri;
private int counter;
private int fav;

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

    public Item(){

    }

    public void setItemImageUri(Uri itemImageUri) {
        this.itemImageUri = itemImageUri;
    }

    public Uri getItemImageUri() {
        return itemImageUri;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    public int getFav() {
        return fav;
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


    @BindingAdapter({"counter"})
    public static void setNewCounter(TextView textView,int counter){
        textView.setText(counter);
    }

    @BindingAdapter("setFav")
    public static void setFav(ImageView imageView,int res){
        imageView.setImageResource(res);
    }
}
