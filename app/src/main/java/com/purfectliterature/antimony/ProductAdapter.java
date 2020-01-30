package com.purfectliterature.antimony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.CardViewViewHolder> {
    private ArrayList<ProductActivity.Product> listProducts;
    private Context activityContext;
    public ProductAdapter(Context context, ArrayList<ProductActivity.Product> products) {
        this.listProducts = products;
        activityContext = context;
    }

    @NonNull
    @Override
    public CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_list, parent, false);
        return new CardViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewViewHolder holder, int position) {
        ProductActivity.Product product = listProducts.get(position);
        holder.productName.setText(product.getName() + " " + product.getSize());
        holder.productType.setText(product.getType());

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatIDR = NumberFormat.getCurrencyInstance(localeID);

        String formattedPrice;

        try {
            formattedPrice = formatIDR.format(Double.valueOf(product.getPrice()));
        } catch (NumberFormatException error) {
            formattedPrice = product.getPrice();
        }

        holder.productPrice.setText(formattedPrice);
    }

    @Override
    public int getItemCount() {
        return listProducts.size();
    }

    public class CardViewViewHolder extends RecyclerView.ViewHolder {
        private TextView productName, productPrice, productType;

        public CardViewViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productType = itemView.findViewById(R.id.product_type);

            Typeface fontGraphikMedium = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Graphik-Medium.ttf");
            productName.setTypeface(fontGraphikMedium);
        }
    }
}
