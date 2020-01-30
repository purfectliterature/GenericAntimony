package com.purfectliterature.antimony;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.purfectliterature.antimony.fragments.CheckInFragment;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.CardViewViewHolder> {
    // (1) Define an array of Store(s)
    private ArrayList<CheckInFragment.Store> listStores;
    private Context activityContext;
    public StoreAdapter(Context context, ArrayList<CheckInFragment.Store> stores) {
        activityContext = context;
        this.listStores = stores;
    }

    // (2) Initialise all views (components) in layout XML
    public class CardViewViewHolder extends RecyclerView.ViewHolder {
        TextView txtStoreName, txtStoreAddress;

        public CardViewViewHolder(@NonNull View itemView) {
            super(itemView);
            // itemView is "this" adapter (in this case, one CardView element)
            txtStoreName = itemView.findViewById(R.id.store_name);
            txtStoreAddress = itemView.findViewById(R.id.store_address);

            Typeface fontGraphikMedium = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Graphik-Medium.ttf");
            txtStoreName.setTypeface(fontGraphikMedium);
        }
    }

    // (3) Loop through every element in listStore array and bind the values to a holder (one RecyclerView element)
    @Override
    public void onBindViewHolder(@NonNull final StoreAdapter.CardViewViewHolder holder, int position) {
        // position is an index, and since listStores is an array of Store elements, set store as Store element of index position from listStores array.
        final CheckInFragment.Store store = listStores.get(position);

        // holder is this ViewHolder. Initialise values for all the components in this ViewHolder.
        holder.txtStoreName.setText(store.getName());
        holder.txtStoreAddress.setText(store.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new Store's details Activity
                Intent moveToProducts = new Intent(activityContext, ProductActivity.class);
                moveToProducts.putExtra(ProductActivity.STORE_ID, store.getId());
                moveToProducts.putExtra(ProductActivity.STORE_NAME, store.getName());
                moveToProducts.putExtra(ProductActivity.STORE_ADDRESS, store.getAddress());
                moveToProducts.putExtra(ProductActivity.STORE_PIC, store.getPersonInCharge());
                moveToProducts.putExtra(ProductActivity.STORE_PIC_CONTACT, store.getPersonInChargeContact());
                activityContext.startActivity(moveToProducts);
            }
        });
    }

    // (4) Create ViewHolder by inflating from stores_list layout XML
    @NonNull
    @Override
    public StoreAdapter.CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stores_list, parent, false);
        return new CardViewViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return listStores.size();
    }


}
