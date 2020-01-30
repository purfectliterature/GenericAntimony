package com.purfectliterature.antimony.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.purfectliterature.antimony.LoginActivity;
import com.purfectliterature.antimony.Preferences;
import com.purfectliterature.antimony.R;
import com.purfectliterature.antimony.StoreAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckInFragment extends Fragment {
    private TextInputLayout tilSearch;
    private TextInputEditText edtSearch;
    private TextView txtTitleCheckIn;
    private RecyclerView rvStoresList;

    private RequestQueue requestQueue;
    private String urlGetStores;

    private ArrayList<Store> stores = new ArrayList<>();

    private String TAG = "CheckInFragment"; // for debugging purposes

    public CheckInFragment() {
        // Required empty public constructor

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilSearch = getView().findViewById(R.id.checkIn_search_input_layout);
        edtSearch = getView().findViewById(R.id.checkIn_search_edit_text);
        txtTitleCheckIn = getView().findViewById(R.id.check_in_toolbar_title);
        rvStoresList = getView().findViewById(R.id.stores_list);

        tilSearch.setHintEnabled(false); // Disables "flying" hint text when TextInputEditText is in focus
        Typeface fontGraphikBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Graphik-Bold.ttf");
        txtTitleCheckIn.setTypeface(fontGraphikBold);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                doSearch(edtSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch(edtSearch.getText().toString());
                    edtSearch.clearFocus();
                    hideKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        tilSearch.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtSearch.setText("");
                edtSearch.clearFocus();
                doSearch("");
                hideKeyboard(getActivity());
            }
        });

        getStores();

    }

    private void doSearch(String searchQuery) {
        Context context = (getContext() != null? getContext() : getActivity().getApplicationContext());
        ArrayList<Store> storesFiltered = new ArrayList<>();
        for (int i = 0; i < stores.size(); i++) {
            Store tempStore = stores.get(i);
            if (tempStore.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                storesFiltered.add(tempStore);
            }
        }

        populateRecyclerView(context, storesFiltered);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        urlGetStores = getString(R.string.apiGetStores);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check_in, container, false);
    }

    public class Store {
        private String id;
        private String name;
        private String address;
        private String personInCharge;
        private String personInChargeContact;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPersonInCharge() {
            return personInCharge;
        }

        public void setPersonInCharge(String personInCharge) {
            this.personInCharge = personInCharge;
        }

        public String getPersonInChargeContact() {
            return personInChargeContact;
        }

        public void setPersonInChargeContact(String personInChargeContact) {
            this.personInChargeContact = personInChargeContact;
        }
    }

    private void getStores() {
        final Context context = (getContext() != null? getContext() : getActivity().getApplicationContext());
        requestQueue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlGetStores,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            JSONArray storesResponseArray = responseObject.getJSONArray("values");
                            ArrayList<String> storesStringArray = new ArrayList<String>();
                            for (int i = 0; i < storesResponseArray.length(); i++) {
                                JSONObject storeSingularData = storesResponseArray.getJSONObject(i);
                                Store newStore = new Store();
                                newStore.setId(storeSingularData.getString("store_id"));
                                newStore.setName(storeSingularData.getString("name"));
                                newStore.setAddress(storeSingularData.getString("address"));
                                newStore.setPersonInCharge(storeSingularData.getString("pic"));
                                newStore.setPersonInChargeContact(storeSingularData.getString("pic_contact"));
                                stores.add(newStore);
                            }

                            populateRecyclerView(context, stores);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    switch (error.networkResponse.statusCode) {
                        case 404:
                            // Try-catch statement to anticipate if Activity has stopped but Toast is still called, where in that case, context = null
                            try {
                                Preferences.clearLoginPrefs(context);
                                Toast.makeText(context, R.string.error_user_not_found, Toast.LENGTH_LONG).show();
                            } catch (IllegalStateException illegalStateExceptionError) {
                                Log.d(TAG, "IllegalStateException");
                            }
                            break;
                        case 440:
                            try {
                                Preferences.clearLoginPrefs(context);
                                Toast.makeText(context, R.string.error_session_expired, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(context, LoginActivity.class));
                                getActivity().finish();
                            } catch (IllegalStateException illegalStateExceptionError) {
                                Log.d(TAG, "IllegalStateException");
                            }
                            break;
                        case 498:
                            try {
                                Preferences.clearLoginPrefs(context);
                                Toast.makeText(context, R.string.error_invalid_token, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(context, LoginActivity.class));
                                getActivity().finish();
                            } catch (IllegalStateException illegalStateExceptionError) {
                                Log.d(TAG, "IllegalStateException");
                            }
                            break;
                    }
                } else {
                    try {
                        Toast.makeText(context, getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
                        Log.d(TAG, error.toString());
                    } catch (IllegalStateException errorUnknown) {
                        Log.d(TAG, "IllegalStateException");
                    }

                }
            }
        }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String token = Preferences.getUserToken(context);
                String userId = Preferences.getUserId(context);
                params.put("token", token);
                params.put("user_id", userId);

                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    private void populateRecyclerView(Context context, ArrayList<Store> storesParsed) {
        rvStoresList.setHasFixedSize(true);
        rvStoresList.setLayoutManager(new LinearLayoutManager(context));
        StoreAdapter storeAdapter = new StoreAdapter(context, storesParsed);
        rvStoresList.setAdapter(storeAdapter);
    }

}
