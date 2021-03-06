package com.returno.tradeit.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.returno.tradeit.utils.Constants;

import timber.log.Timber;

public class DBHelper extends SQLiteOpenHelper {

    static final String TITLE="title";
    static final String DESCRIPTION="descrip";
    static final String IMAGE="imageUri";
    static final String PRICE="price";
    static final String ITEM_TABLE="Table_Items";
    static final String ITEM_ID="item_id";
    static final String CATEG="categ";
    static final String _ID="Id";
    static final String DB_NAME="tradeit_chats.DB";
    static final String USER_ID ="user_id";
    static final String TABLE_NAME="chats";

    static final String USERS_TABLE="TABLE_USERS";

    private static final int DB_VERSION=2;


    DBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){

        try {
            String CREATE_ITEM_TABLE = "create table if not exists " + ITEM_TABLE + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    USER_ID + " TEXT NOT NULL," + ITEM_ID + " TEXT NOT NULL," + TITLE + " TEXT NOT NULL," + DESCRIPTION + " TEXT NOT NULL," + IMAGE + "" +
                    " TEXT NOT NULL," + PRICE + " TEXT NOT NULL," + CATEG + " TEXT NOT NULL,"+Constants.ITEM_TAG+" TEXT NOT NULL)";

            database.execSQL(CREATE_ITEM_TABLE);

            String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + USERS_TABLE + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                    Constants.USER_ID + " TEXT NOT NULL ," + Constants.USER_NAME + " TEXT NOT NULL ," + Constants.PHONE + " TEXT NOT NULL, " +
                    Constants.USER_IMAGE + " TEXT NOT NULL )";

            database.execSQL(CREATE_USERS_TABLE);
            Timber.e("Table created");
            //database.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ITEM_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+USERS_TABLE);

        onCreate(db);
        //db.close();
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // super.onDowngrade(db, oldVersion, newVersion);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ITEM_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+USERS_TABLE);


        onCreate(db);
db.close();
    }
}
