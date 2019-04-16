package com.example.android.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";
    public static final String PATH_SUPPLIER = "supplier";

    public static final class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI =  BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PRODUCTS).build();
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_PRODUCT_ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_PRICE = "product_price";
        public static final String COLUMN_PRODUCT_QUANTITY = "product_quantity";
        public static final String COLUMN_SUPPLIER_ID = "supplier_id";
    }

    public static final class SupplierEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_SUPPLIER).build();
        public static final String TABLE_NAME = "suppliers";
        public static final String COLUMN_SUPPLIER_ID = BaseColumns._ID;
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE_NO = "suppler_phone_no";
    }
}
