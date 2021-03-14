package com.returno.tradeit.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.returno.tradeit.callbacks.CompleteCallBacks;
import com.returno.tradeit.models.CategoryItem;
import com.returno.tradeit.models.Item;
import com.returno.tradeit.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class DatabaseManager {

    SQLiteDatabase database;
    final Context context;
    DBHelper helper;

   public DatabaseManager (Context c){
        this.context=c;
    }

    public DatabaseManager open() throws SQLException{
       this.helper=new DBHelper(this.context);
       try {
           this.database=this.helper.getWritableDatabase();
       }catch (IllegalStateException e){
           this.database=this.helper.getWritableDatabase();
       }

       return this;
    }

    public void close(){
       this.helper.close();
    }


    public void insertCategory(CategoryItem item){
        ContentValues contentValues=new ContentValues();
        contentValues.put(Constants.ITEM_TITLE,item.getPost_title());
        contentValues.put(Constants.ITEM_DESCRIPTION,item.getPost_desc());
        contentValues.put(Constants.IMAGE_URL,item.getPost_url());

        this.database.insert(DBHelper.CATEGORY_TABLE,null,contentValues);
    }

    public List<CategoryItem>getProductCategories() {
        List<CategoryItem> categoryItems = new ArrayList<>();
        String query = "SELECT * FROM " + DBHelper.CATEGORY_TABLE;
        Cursor cursor = this.database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String categoryTitle=cursor.getString(cursor.getColumnIndex(Constants.ITEM_TITLE));
                String desc=cursor.getString(cursor.getColumnIndex(Constants.ITEM_DESCRIPTION));
                String url=cursor.getString(cursor.getColumnIndex(Constants.IMAGE_URL));

                categoryItems.add(new CategoryItem(categoryTitle,desc,url));

            } while (cursor.moveToNext());

        }
        cursor.close();
        return categoryItems;
    }

   public void insertItems(Item item,String category){
     ContentValues contentValues=new ContentValues();
     contentValues.put(DBHelper.USER_ID,item.getItemPosterId());
     contentValues.put(DBHelper.ITEM_ID,item.getItemId());
     contentValues.put(DBHelper.TITLE,item.getItemName());
     contentValues.put(DBHelper.DESCRIPTION,item.getItemDescription());
     contentValues.put(DBHelper.IMAGE,item.getItemImage());
     contentValues.put(DBHelper.CATEG,category);
     contentValues.put(DBHelper.PRICE,item.getItemPrice());
     contentValues.put(Constants.ITEM_TAG,item.getItemTag());

     this.database.insert(DBHelper.ITEM_TABLE,null,contentValues);

   }

   public List<Item> getProducts(String category){
       List<Item> items=new ArrayList<>();
       String sql="SELECT * FROM "+DBHelper.ITEM_TABLE+" WHERE "+DBHelper.CATEG+" = '"+category+"' ;";
       Cursor cursor=this.database.rawQuery(sql,null);

       if (cursor.moveToFirst()){
           do {
              int dbId=cursor.getInt(cursor.getColumnIndex(DBHelper._ID));
              String itemId=cursor.getString(cursor.getColumnIndex(DBHelper.ITEM_ID));
              String desc=cursor.getString(cursor.getColumnIndex(DBHelper.DESCRIPTION));
              String title=cursor.getString(cursor.getColumnIndex(DBHelper.TITLE));
              String categ=cursor.getString(cursor.getColumnIndex(DBHelper.CATEG));
              String image=cursor.getString(cursor.getColumnIndex(DBHelper.IMAGE));
              String userId=cursor.getString(cursor.getColumnIndex(DBHelper.USER_ID));
              String price=cursor.getString(cursor.getColumnIndex(DBHelper.PRICE));
              String tag=cursor.getString(cursor.getColumnIndex(Constants.ITEM_TAG));
             // String id=String.valueOf(cursor.getInt(cursor.getColumnIndex(DBHelper)))

            items.add(new Item(itemId,title,desc,Integer.parseInt(price),tag,image,userId,category));
           }while (cursor.moveToNext());
       }
       cursor.close();
       return items;
   }

    public List<Item> getMyItems(String user){
        List<Item> items=new ArrayList<>();
        String sql="SELECT * FROM "+DBHelper.ITEM_TABLE+" WHERE "+DBHelper.USER_ID+" = '"+user+"' ;";
        Cursor cursor=this.database.rawQuery(sql,null);

        if (cursor.moveToFirst()){
            do {
                int dbId=cursor.getInt(cursor.getColumnIndex(DBHelper._ID));
                String itemId=cursor.getString(cursor.getColumnIndex(DBHelper.ITEM_ID));
                String desc=cursor.getString(cursor.getColumnIndex(DBHelper.DESCRIPTION));
                String title=cursor.getString(cursor.getColumnIndex(DBHelper.TITLE));
                String categ=cursor.getString(cursor.getColumnIndex(DBHelper.CATEG));
                String image=cursor.getString(cursor.getColumnIndex(DBHelper.IMAGE));
                String userId=cursor.getString(cursor.getColumnIndex(DBHelper.USER_ID));
                String price=cursor.getString(cursor.getColumnIndex(DBHelper.PRICE));
                String tag=cursor.getString(cursor.getColumnIndex(Constants.ITEM_TAG));
                // String id=String.valueOf(cursor.getInt(cursor.getColumnIndex(DBHelper)))

                items.add(new Item(itemId,title,desc,Integer.parseInt(price),tag,image,userId,categ));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public void getFavourites(String id, CompleteCallBacks listener){
               String sql="SELECT * FROM "+DBHelper.ITEM_TABLE+" WHERE "+DBHelper.ITEM_ID+" = '"+id+"' ;";
               Cursor cursor=this.database.rawQuery(sql,null);

               Item item=null;
               Timber.e(String.valueOf(cursor.getCount()));
               if (cursor.moveToFirst()){
                   do {
                       int dbId=cursor.getInt(cursor.getColumnIndex(DBHelper._ID));
                       String itemId=cursor.getString(cursor.getColumnIndex(DBHelper.ITEM_ID));
                       String desc=cursor.getString(cursor.getColumnIndex(DBHelper.DESCRIPTION));
                       String title=cursor.getString(cursor.getColumnIndex(DBHelper.TITLE));
                       String categ=cursor.getString(cursor.getColumnIndex(DBHelper.CATEG));
                       String image=cursor.getString(cursor.getColumnIndex(DBHelper.IMAGE));
                       String userId=cursor.getString(cursor.getColumnIndex(DBHelper.USER_ID));
                       String price=cursor.getString(cursor.getColumnIndex(DBHelper.PRICE));
                       String tag=cursor.getString(cursor.getColumnIndex(Constants.ITEM_TAG));

                       item=new Item(itemId,title,desc,Integer.parseInt(price),tag,image,userId,categ);
                   }while (cursor.moveToNext());
               }
               cursor.close();
               listener.onComplete(item);

    }


    public long getItemCount(String category){
       String sql="SELECT Id FROM "+DBHelper.ITEM_TABLE+" WHERE "+DBHelper.CATEG+" = '"+category+"';";
       Cursor cursor=database.rawQuery(sql,null);
       long count=cursor.getCount();
       cursor.close();
       return count;

   }


    public void deleteItem(String itemid) {
      int affected= this.database.delete(DBHelper.ITEM_TABLE,DBHelper.ITEM_ID+"='"+itemid+"'",null);
        Timber.e(String.valueOf(affected));
    }

}
