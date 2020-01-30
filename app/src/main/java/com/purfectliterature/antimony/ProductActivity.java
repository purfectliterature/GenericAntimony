package com.purfectliterature.antimony;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {
    public static final String STORE_ID = "storeId",
            STORE_NAME = "storeName",
            STORE_ADDRESS = "storeAddress",
            STORE_PIC = "storePic",
            STORE_PIC_CONTACT = "storePicContact";

    private MaterialToolbar tbrProductToolbar;
    private TextView tbrStoreName;
    private TextView tbrStoreAddress;
    private RecyclerView rvProductsList;

    private RequestQueue requestQueue;
    private String urlGetProductsfromStoreId;
    private String TAG = "ProductActivity";

    private ArrayList<Product> products = new ArrayList<>();

    public class Product {
        private String name, size, type, price;
        private boolean isFocus;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public boolean isFocus() {
            return isFocus;
        }

        public void setFocus(boolean focus) {
            isFocus = focus;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        urlGetProductsfromStoreId = getString(R.string.apiGetProductsfromStoreId);

        tbrProductToolbar = findViewById(R.id.product_toolbar);
        tbrStoreName = findViewById(R.id.product_toolbar_title);
        tbrStoreAddress = findViewById(R.id.product_toolbar_subtitle);
        rvProductsList = findViewById(R.id.products_list);

        tbrStoreName.setText(getIntent().getStringExtra(STORE_NAME));
        Typeface fontGraphikBold = Typeface.createFromAsset(getAssets(), "fonts/Graphik-Bold.ttf");
        tbrStoreName.setTypeface(fontGraphikBold);

        tbrStoreAddress.setText(getIntent().getStringExtra(STORE_ADDRESS));
        Typeface fontGraphikMedium = Typeface.createFromAsset(getAssets(), "fonts/Graphik-Medium.ttf");
        tbrStoreAddress.setTypeface(fontGraphikMedium);

        setSupportActionBar(tbrProductToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        tbrProductToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getProductsfromStoreId();
    }

    private void getProductsfromStoreId() {
        requestQueue = Volley.newRequestQueue(getBaseContext());
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray productsResponseArray = responseObject.getJSONArray("values");
                    for (int i = 0; i < productsResponseArray.length(); i++) {
                        JSONObject productSingularData = productsResponseArray.getJSONObject(i);
                        Product newProduct = new Product();
                        newProduct.setName(productSingularData.getString("product_name"));
                        newProduct.setSize(productSingularData.getString("product_size"));
                        newProduct.setPrice(productSingularData.getString("product_price"));
                        newProduct.setType(productSingularData.getString("product_type"));
                        newProduct.setFocus(productSingularData.getString("focus") == "1");
                        products.add(newProduct);
                    }

                    populateRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    switch (error.networkResponse.statusCode) {
                        case 404:
                            Preferences.clearLoginPrefs(getBaseContext());
                            Toast.makeText(getBaseContext(), R.string.error_user_not_found, Toast.LENGTH_LONG).show();
                            break;
                        case 440:
                            Preferences.clearLoginPrefs(getBaseContext());
                            Toast.makeText(getBaseContext(), R.string.error_session_expired, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getBaseContext(), LoginActivity.class));
                            finish();
                            break;
                        case 498:
                            Preferences.clearLoginPrefs(getBaseContext());
                            Toast.makeText(getBaseContext(), R.string.error_invalid_token, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getBaseContext(), LoginActivity.class));
                            finish();
                            break;
                    }
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
                    Log.d(TAG, error.toString());
                }
            }
        };

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlGetProductsfromStoreId, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String token = Preferences.getUserToken(getBaseContext());
                String userId = Preferences.getUserId(getBaseContext());
                String storeId = getIntent().getStringExtra(STORE_ID);
                params.put("token", token);
                params.put("user_id", userId);
                params.put("store_id", storeId);
                return params;
            }
        };

        requestQueue.add(postRequest);
    }

    private void populateRecyclerView() {
        if (products.size() <= 0) {
            Product emptyProduct = new Product();
            emptyProduct.setName(getString(R.string.no_products_sold_yet));
            emptyProduct.setSize("");
            emptyProduct.setPrice("");
            emptyProduct.setType(getString(R.string.market_products));
            emptyProduct.setFocus(false);
            products.add(emptyProduct);
        }
        rvProductsList.setLayoutManager(new LinearLayoutManager(this));
        ProductAdapter productAdapter = new ProductAdapter(this, products);
        rvProductsList.setAdapter(productAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.products_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void makeToastCopiedToClipboard(CharSequence number) {
        Toast.makeText(this, number + " " + getText(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_pic:
                // Add code to show PIC dialog
                final String picContactNumber = getIntent().getStringExtra(STORE_PIC_CONTACT);

                Dialog.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (!TextUtils.isEmpty(picContactNumber)) {
                                    Intent moveToDialer = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + picContactNumber));
                                    startActivity(moveToDialer);
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                if (!TextUtils.isEmpty(picContactNumber)) {
                                    CharSequence number = picContactNumber;
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Telephone", number));
                                    makeToastCopiedToClipboard(number);
                                }
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String picDetails = getIntent().getStringExtra(STORE_PIC) + "\n" + picContactNumber;
                builder.setMessage(picDetails)
                        .setPositiveButton(getString(R.string.pic_dial), dialogClickListener)
                        .setNegativeButton(getString(R.string.pic_copy), dialogClickListener)
                        .setNeutralButton(getString(R.string.close), dialogClickListener)
                        .show();
        }
        return super.onOptionsItemSelected(item);
    }
}
