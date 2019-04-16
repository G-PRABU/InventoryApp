package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;


public class ProductProvider extends ContentProvider {

    ProductDbHelper mProductDbHelper;
    public static final int PRODUCT = 100;
    public static final int PRODUCT_ID = 101;
    public static final int SUPPLIER = 200;
    public static final int SUPPLIER_ID = 201;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS,PRODUCT);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS+"/#",PRODUCT_ID);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_SUPPLIER,SUPPLIER);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_SUPPLIER+"/#",SUPPLIER_ID);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public boolean onCreate() {
        mProductDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return insertProduct(uri,values);
            case SUPPLIER:
                return insertSupplier(uri,values);
            default:
                throw new IllegalArgumentException("Invalid uri cannot insert :"+ uri);
        }
    }

    private Uri insertProduct(Uri uri,ContentValues values){
        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
        String name = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if(name == null)
            throw new IllegalArgumentException("Product should required name");
        Double price = values.getAsDouble(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        if(price == null)
            throw new IllegalArgumentException("Product should required price");
        Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if(quantity == null && quantity <0)
            throw new IllegalArgumentException("Product requires valid quantity");
        Integer supplierId = values.getAsInteger(ProductContract.ProductEntry.COLUMN_SUPPLIER_ID);
        if(supplierId == null  & supplierId <0)
            throw new IllegalArgumentException("Product required supplier id");
        long id = database.insert(ProductContract.ProductEntry.TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    private Uri insertSupplier(Uri uri,ContentValues values){
        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
        String name = values.getAsString(ProductContract.SupplierEntry.COLUMN_SUPPLIER_NAME);
        if(name == null)
            throw new IllegalArgumentException("Supplier name should required");
        Long number = values.getAsLong(ProductContract.SupplierEntry.COLUMN_SUPPLIER_PHONE_NO);
        if(number == null){
            throw new IllegalArgumentException("Supplier number should required");
        }
        long id = database.insert(ProductContract.SupplierEntry.TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case PRODUCT:
                return updateProduct(uri,values,selection,selectionArgs);
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry.COLUMN_PRODUCT_ID +"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri,values,selection,selectionArgs);
            case SUPPLIER_ID:
                selection = ProductContract.SupplierEntry.COLUMN_SUPPLIER_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateSupplier(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update Product :" + uri);
        }
    }

    private int updateProduct(Uri uri,ContentValues values,String selection,String[] selectionArgs){
        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
        String name = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if(name == null)
            throw new IllegalArgumentException("Product should required name");
        Double price = values.getAsDouble(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        if(price == null)
            throw new IllegalArgumentException("Product should required price");
        Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if(quantity == null && quantity <0)
            throw new IllegalArgumentException("Product requires valid quantity");
        Integer supplierId = values.getAsInteger(ProductContract.ProductEntry.COLUMN_SUPPLIER_ID);
        if(supplierId == null  & supplierId <0)
            throw new IllegalArgumentException("Product required supplier id");

        if(values.size() == 0) {
            return 0;
        }
        int row = database.update(ProductContract.ProductEntry.TABLE_NAME,values,selection,selectionArgs);
        if(row != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return row;
    }

    private int updateSupplier(Uri uri,ContentValues values,String selection,String[] selectionArgs){
        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
        String name = values.getAsString(ProductContract.SupplierEntry.COLUMN_SUPPLIER_NAME);
        if(name == null)
            throw new IllegalArgumentException("Supplier name should required");
        Long number = values.getAsLong(ProductContract.SupplierEntry.COLUMN_SUPPLIER_PHONE_NO);
        if(number == null){
            throw new IllegalArgumentException("Supplier number should required");
        }
        if(values.size() == 0) {
            return 0;
        }
        int row = database.update(ProductContract.SupplierEntry.TABLE_NAME,values,selection,selectionArgs);
        if(row != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return row;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case PRODUCT:
                return deleteProduct(uri,selection,selectionArgs);
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry.COLUMN_PRODUCT_ID +"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return deleteProduct(uri,selection,selectionArgs);
            case SUPPLIER_ID:
                selection = ProductContract.SupplierEntry.COLUMN_SUPPLIER_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return deleteSupplier(uri,selection,selectionArgs);
            case SUPPLIER:
                return deleteProduct(uri,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Delete Product :" + uri);
        }
    }

    private int deleteProduct(Uri uri,String selection,String[] selectionArgs) {
        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
        int row = database.delete(ProductContract.ProductEntry.TABLE_NAME,selection,selectionArgs);
        if(row!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return row;
    }

    private int deleteSupplier(Uri uri,String selection,String[] selectionArgs) {
        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
        int row = database.delete(ProductContract.SupplierEntry.TABLE_NAME,selection,selectionArgs);
        if(row!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return row;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = mProductDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch(match) {
            case PRODUCT:
                if(TextUtils.isEmpty(sortOrder)) sortOrder = ProductContract.ProductEntry.COLUMN_PRODUCT_ID + " ASC";
                cursor = sqLiteDatabase.query(ProductContract.ProductEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry.COLUMN_PRODUCT_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = sqLiteDatabase.query(ProductContract.ProductEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case SUPPLIER:
                cursor = sqLiteDatabase.query(ProductContract.SupplierEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case SUPPLIER_ID:
                selection = ProductContract.SupplierEntry.COLUMN_SUPPLIER_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = sqLiteDatabase.query(ProductContract.SupplierEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }
}
