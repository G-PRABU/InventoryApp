package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;
import java.text.DecimalFormat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    public interface ProductOnClickHandler {
        void onClick(int id);
    }
    private ProductOnClickHandler mClickHandler;

    public ProductAdapter(Context context,ProductOnClickHandler onClickHandler) {
        mContext = context;
        mClickHandler = onClickHandler;
    }

    @Override
    public int getItemCount() {
        if(mCursor == null) return 0;
        return mCursor.getCount();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(mContext).inflate(R.layout.product_list,parent,false);
        return new ProductViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String name = mCursor.getString(mCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        double price = mCursor.getDouble(mCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        int quantity = mCursor.getInt(mCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        int supplierId = mCursor.getInt(mCursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_ID));
        holder.productName.setText(name);
        holder.productPrice.setText(mContext.getString(R.string.product_price_text)+" "+new DecimalFormat("##.##").format(price));
        holder.productQuantity.setText(mContext.getString(R.string.product_availability_text)+" "+quantity);
        holder.cursorPosition = position;
        holder.name = name;
        holder.price = price;
        holder.quantity = quantity;
        holder.supplierId = supplierId;
        holder.productBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.productBuyUpdate();
            }
        });
    }

    public void swapCursor(Cursor cursor){
        mCursor = cursor;
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.product_buy_button)
        Button productBuyButton;
        @BindView(R.id.product_quantity)
        TextView productQuantity;
        @BindView(R.id.product_price)
        TextView productPrice;
        @BindView(R.id.product_name)
        TextView productName;
        private String name;
        private double price;
        private int quantity;
        private int supplierId;
        private int cursorPosition;

        ProductViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
            view.setOnClickListener(this);
        }

        private void productBuyUpdate(){
            if(quantity>0) {
                mCursor.moveToPosition(cursorPosition);
                Uri currentProduct = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, mCursor.getInt(mCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_ID)));
                ContentValues productValues = new ContentValues();
                productValues.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
                productValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
                productValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);
                productValues.put(ProductEntry.COLUMN_SUPPLIER_ID, supplierId);
                int productRowAffected = mContext.getContentResolver().update(currentProduct,productValues,null,null);
                if (productRowAffected > 0) {
                    Toast.makeText(mContext,mContext.getString(R.string.product_buy_message),Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext,"Failed",Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(mContext,mContext.getString(R.string.product_not_available_message),Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(cursorPosition);
            mClickHandler.onClick(mCursor.getInt(mCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_ID)));
        }
    }
}
