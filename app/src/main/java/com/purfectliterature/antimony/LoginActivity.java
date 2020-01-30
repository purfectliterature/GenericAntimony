package com.purfectliterature.antimony;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    private MaterialButton mLoginBtn;
    private TextInputEditText mStaffIdInput;
    private MaterialToolbar mToolbar;
    private RequestQueue requestQueue;
    private TextView mToolbarTitle;
    private TextInputLayout mStaffIdInputLayout;
    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Assign layout objects
        mLoginBtn = findViewById(R.id.login_submit_btn);
        mStaffIdInput = findViewById(R.id.login_edit_text);
        mToolbar = findViewById(R.id.login_toolbar);
        mToolbarTitle = findViewById(R.id.toolbar_title);
        mStaffIdInputLayout = findViewById(R.id.login_text_input_layout);

        // Style font to Graphik-Bold
        Typeface fontGraphikBold = Typeface.createFromAsset(getAssets(), "fonts/Graphik-Bold.ttf");
        mToolbarTitle.setTypeface(fontGraphikBold);

        requestQueue = Volley.newRequestQueue(this);

        setSupportActionBar(mToolbar);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });

        mStaffIdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    doLogin();
                    return true;
                }
                return false;
            }
        });
    }

    private void doLogin() {
        final String staffId = mStaffIdInput.getText().toString();

        if (TextUtils.isEmpty(staffId)) {
            mStaffIdInput.setError(getString(R.string.field_empty));
            mStaffIdInput.requestFocus();
        } else {
            String url = getString(R.string.apiLogin);
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject values = new JSONObject(response).getJSONObject("values");
                                String token = values.getString("token");
                                String userId = values.getString("user_id");

                                Preferences.setLoginPrefs(token, userId, getBaseContext());

                                startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                                finish();

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
                                mStaffIdInput.setError(getString(R.string.error_user_not_found));
                                mStaffIdInput.requestFocus();
                                break;
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
                        Log.d(TAG, error.toString());
                    }
                }
            }
            ) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", staffId);

                    return params;
                }
            };
            requestQueue.add(postRequest);
        }

    }
}
