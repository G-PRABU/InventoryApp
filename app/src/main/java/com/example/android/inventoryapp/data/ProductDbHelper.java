package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;
import com.example.android.inventoryapp.data.ProductContract.SupplierEntry;
public class ProductDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;
    ProductDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCT_TABLE =  "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry.COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
                + ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL,"
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
                + ProductEntry.COLUMN_SUPPLIER_ID + " INTEGER NOT NULL)";

        String CREATE_SUPPLIER_TABLE = "CREATE TABLE " + SupplierEntry.TABLE_NAME + " ("
                + SupplierEntry.COLUMN_SUPPLIER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SupplierEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL,"
                + SupplierEntry.COLUMN_SUPPLIER_PHONE_NO + " INTEGER NOT NULL)";

        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_SUPPLIER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ProductEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+SupplierEntry.TABLE_NAME);
        onCreate(db);
    }
}
