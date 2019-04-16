package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.android.inventoryapp.data.ProductContract.*;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.product_detail_name)
    EditText mProductNameEditText;
    @BindView(R.id.product_detail_price)
    EditText mProductPriceEditText;
    @BindView(R.id.product_detail_quantity)
    EditText mProductQuantityEditText;
    @BindView(R.id.product_quantity_increase)
    Button mProductQuantityIncrease;
    @BindView(R.id.product_quantity_decrease)
    Button mProductQuantityDecrease;
    @BindView(R.id.supplier_detail_name)
    EditText mSupplierNameEditText;
    @BindView(R.id.supplier_detail_phone_number)
    EditText mSupplierPhoneNumberEditText;
    private Uri mCurrentProductUri;
    private Uri mCurrentSupplierUri;
    private static final int EDITOR_PRODUCT_LOADER = 1;
    private static final int EDITOR_SUPPLIER_LOADER = 2;
    private boolean mProductHasChanged;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_editor);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if(mCurrentProductUri == null){
            setTitle(getString(R.string.product_detail_add));
        } else {
            setTitle(getString(R.string.product_detail_edit));
            getLoaderManager().initLoader(EDITOR_PRODUCT_LOADER,null,this);
        }
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mProductQuantityDecrease.setOnTouchListener(mTouchListener);
        mProductQuantityDecrease.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setText("0");
        mProductQuantityIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = mProductQuantityEditText.getText().toString();
                try {
                    if(!(value.isEmpty())) {
                        int quantity = Integer.parseInt(value);
                        quantity++;
                        mProductQuantityEditText.setText(String.valueOf(quantity));
                    } else {
                        mProductQuantityEditText.setText("1");
                    }
                } catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        });
        mProductQuantityDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value =mProductQuantityEditText.getText().toString();
                try {
                    if ((TextUtils.isEmpty(value)) || Integer.parseInt(value) > 0) {
                        int quantity = Integer.parseInt(value);
                        quantity--;
                        mProductQuantityEditText.setText(String.valueOf(quantity));
                    } else {
                        mProductQuantityEditText.setText("0");
                    }
                } catch(NumberFormatException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDeleteConformationDialogMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.product_detail_delete_conformation_message));
        builder.setPositiveButton(R.string.product_detail_delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.product_detail_delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedConformationDialogMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.product_detail_unsaved_message);
        builder.setPositiveButton(R.string.product_detail_unsaved_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.product_detail_unsaved_negative_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveProduct() {
        String productName = mProductNameEditText.getText().toString().trim();
        String productPrice = mProductPriceEditText.getText().toString().trim();
        String productQuantity = mProductQuantityEditText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhone = mSupplierPhoneNumberEditText.getText().toString().trim();
        if(!TextUtils.isEmpty(productName) && !TextUtils.isEmpty(productQuantity) && !TextUtils.isEmpty(productPrice) && !TextUtils.isEmpty(supplierName)
                && !TextUtils.isEmpty(supplierPhone)){
            ContentValues productValues = new ContentValues();
            ContentValues supplierValues = new ContentValues();
            productValues.put(ProductEntry.COLUMN_PRODUCT_NAME,productName);
            productValues.put(ProductEntry.COLUMN_PRODUCT_PRICE,Double.parseDouble(productPrice));
            productValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY,Integer.parseInt(productQuantity));
            supplierValues.put(SupplierEntry.COLUMN_SUPPLIER_NAME,supplierName);
            supplierValues.put(SupplierEntry.COLUMN_SUPPLIER_PHONE_NO,supplierPhone);
            if(mCurrentProductUri == null){
                Uri newSupplierUri = getContentResolver().insert(SupplierEntry.CONTENT_URI,supplierValues);
                productValues.put(ProductEntry.COLUMN_SUPPLIER_ID,ContentUris.parseId(newSupplierUri));
                Uri newProductUri = getContentResolver().insert(ProductEntry.CONTENT_URI,productValues);
                if(newProductUri == null || newSupplierUri==null) {
                    makeToast(getString(R.string.product_detail_saving_error));
                } else {
                    makeToast(getString(R.string.product_detail_saved_successfully));
                }
            } else {
                int supplierRowAffected = getContentResolver().update(mCurrentSupplierUri,supplierValues,null,null);
                productValues.put(ProductEntry.COLUMN_SUPPLIER_ID,ContentUris.parseId(mCurrentSupplierUri));
                int productRowAffected = getContentResolver().update(mCurrentProductUri,productValues,null,null);
                if(productRowAffected == 0 || supplierRowAffected == 0){
                    makeToast(getString(R.string.product_detail_update_failed));
                } else {
                    makeToast(getString(R.string.product_detail_updated_successfully));
                }
            }
        } else {
            makeToast(getString(R.string.product_detail_required_message));
        }
    }

    private void makeToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    private void deleteProduct() {
        int productRow = getContentResolver().delete(mCurrentProductUri,null,null);
        int supplierRow = getContentResolver().delete(mCurrentSupplierUri,null,null);
        if(productRow == 0 && supplierRow == 0){
            makeToast(getString(R.string.product_detail_deleting_failed));
        } else {
            makeToast(getString(R.string.product_detail_deleted_successfully));
        }
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mCurrentProductUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete_product_detail);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_delete_product_detail:
                showDeleteConformationDialogMessage();
                return true;
            case R.id.action_save_product_detail:
                saveProduct();
                finish();
                return true;
            case android.R.id.home:
                if(!mProductHasChanged){
                    NavUtils.navigateUpFromSameTask(ProductEditorActivity.this);
                    return true;
                } else {
                    showUnsavedConformationDialogMessage();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!mProductHasChanged){
            super.onBackPressed();
            return;
        }
        showUnsavedConformationDialogMessage();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] productProjection = {ProductEntry.COLUMN_PRODUCT_ID,
        ProductEntry.COLUMN_PRODUCT_NAME,
        ProductEntry.COLUMN_PRODUCT_PRICE,
        ProductEntry.COLUMN_PRODUCT_QUANTITY,
        ProductEntry.COLUMN_SUPPLIER_ID};
        String[] supplierProjection = {
                SupplierEntry.COLUMN_SUPPLIER_ID,
                SupplierEntry.COLUMN_SUPPLIER_NAME,
                SupplierEntry.COLUMN_SUPPLIER_PHONE_NO
        };
        if(id == EDITOR_PRODUCT_LOADER) {
            return new CursorLoader(this, mCurrentProductUri,productProjection,null,null,null);
        }
        else {
            return new CursorLoader(this, mCurrentSupplierUri,supplierProjection,null,null,null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(EDITOR_PRODUCT_LOADER == loader.getId()){
            if(data == null || data.getCount() < 1)
                return ;
            if(data.moveToFirst()) {
                int nameColumn = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
                int priceColumn = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
                int quantityColumn = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
                int supplierColumn = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_ID);
                mProductNameEditText.setText(data.getString(nameColumn));
                mProductPriceEditText.setText(String.valueOf(data.getDouble(priceColumn)));
                mProductQuantityEditText.setText(String.valueOf(data.getInt(quantityColumn)));
                mCurrentSupplierUri = ContentUris.withAppendedId(SupplierEntry.CONTENT_URI,data.getInt(supplierColumn));
                getLoaderManager().initLoader(EDITOR_SUPPLIER_LOADER,null,this);
            }
        } else {
            if (data == null || data.getCount() < 1)
                return;
            if (data.moveToFirst()) {
                int nameColumn = data.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_NAME);
                int phoneColumn = data.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_PHONE_NO);
                mSupplierNameEditText.setText(data.getString(nameColumn));
                mSupplierPhoneNumberEditText.setText(String.valueOf(data.getInt(phoneColumn)));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
    }
}
