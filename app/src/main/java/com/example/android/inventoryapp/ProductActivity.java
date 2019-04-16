package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.example.android.inventoryapp.data.ProductContract.*;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,ProductAdapter.ProductOnClickHandler {

    private final static int PRODUCT_LOADER_ID = 0;
    private ProductAdapter mProductAdapter;
    @BindView(R.id.product_recycler_view)
    RecyclerView mProductRecyclerView;
    @BindView(R.id.product_add_button)
    Button mProductAddButton;
    @BindView(R.id.empty_list)
    View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        mProductRecyclerView.setLayoutManager(layoutManager);
        mProductRecyclerView.setHasFixedSize(true);
        mProductAdapter = new ProductAdapter(this,this);
        mProductRecyclerView.setAdapter(mProductAdapter);
        getLoaderManager().initLoader(PRODUCT_LOADER_ID,null,this);
        mProductAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this,ProductEditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    private void showDeleteAllConformationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.product_delete_all_conformation_message);
        builder.setPositiveButton(R.string.product_delete_all_positive_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAll();
            }
        });
        builder.setNegativeButton(R.string.product_delete_all_negative_message, new DialogInterface.OnClickListener() {
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

    private void deleteAll() {
        getContentResolver().delete(ProductEntry.CONTENT_URI,null,null);
        getContentResolver().delete(SupplierEntry.CONTENT_URI,null,null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_delete_all_products:
                showDeleteAllConformationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] columnProjection = {ProductEntry.COLUMN_PRODUCT_ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_ID};

        return new CursorLoader(this,ProductEntry.CONTENT_URI,columnProjection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProductAdapter.swapCursor(data);
        if(mProductAdapter.getItemCount() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mProductRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mEmptyView.setVisibility(View.INVISIBLE);
            mProductRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductAdapter.swapCursor(null);
    }

    @Override
    public void onClick(int id) {
        Intent intent = new Intent(ProductActivity.this,ProductEditorActivity.class);
        Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,id);
        intent.setData(uri);
        startActivity(intent);
    }
}
