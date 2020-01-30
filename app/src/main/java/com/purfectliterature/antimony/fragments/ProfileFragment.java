package com.purfectliterature.antimony.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.purfectliterature.antimony.LoginActivity;
import com.purfectliterature.antimony.MainActivity;
import com.purfectliterature.antimony.MainMenuActivity;
import com.purfectliterature.antimony.Preferences;
import com.purfectliterature.antimony.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private MaterialToolbar mToolbar;
    private ImageView mProfileImage;
    private TextView mProfileName, mProfileUsername, mProfileContact, mDistName, mDistArea, mDistAddress, mDistPic, mDistPicContact;
    private String TAG = "ProfileFragment";
    private RequestQueue requestQueue;
    private String urlGetUserProfile;
    private String urlGetDistributor;
    private MaterialButton btnLogout;
    TextView mProfileToolbarTitle;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mToolbar = getView().findViewById(R.id.profile_toolbar);
        mProfileImage = getView().findViewById(R.id.profile_image);
        mProfileName = getView().findViewById(R.id.profile_name);
        mProfileUsername = getView().findViewById(R.id.profile_username);
        mProfileContact = getView().findViewById(R.id.profile_contact);
        mDistName = getView().findViewById(R.id.profile_dist_name);
        mDistArea = getView().findViewById(R.id.profile_dist_area);
        mDistAddress = getView().findViewById(R.id.profile_dist_addr);
        mDistPic = getView().findViewById(R.id.profile_dist_pic);
        mDistPicContact = getView().findViewById(R.id.profile_dist_pic_contact);
        mProfileToolbarTitle = getView().findViewById(R.id.profile_toolbar_title);
        btnLogout = getView().findViewById(R.id.logout_button);

        Typeface fontGraphikBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Graphik-Bold.ttf");
        mProfileToolbarTitle.setTypeface(fontGraphikBold);

        mDistPicContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mDistPicContact.getText())) {
                    Intent moveToDialer = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mDistPicContact.getText()));
                    startActivity(moveToDialer);
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = (getContext() != null? getContext() : getActivity().getApplicationContext());

                Dialog.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Preferences.clearLoginPrefs(context);
                                Intent moveToMain = new Intent(context, MainActivity.class);
                                startActivity(moveToMain);
                                getActivity().finish();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.you_sure_to_logout))
                        .setPositiveButton(getString(R.string.logout_question_yes), dialogClickListener)
                        .setNegativeButton(getString(R.string.logout_question_no), dialogClickListener)
                        .show();

            }
        });

        mProfileContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mDistPicContact.getText())) {
                    Context context = (getContext() != null? getContext() : getActivity().getApplicationContext());
                    CharSequence number = mProfileContact.getText();
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(ClipData.newPlainText("Telephone", number));
                    Toast.makeText(context, number + " " + getText(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
                }
            }
        });


        UserProfile userProfile = new UserProfile();

        getUserProfile(userProfile);

        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        urlGetUserProfile = getString(R.string.apiGetUserProfile);
        urlGetDistributor = getString(R.string.apiGetDistributor);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void getDistributor(final UserProfile userProfile) {
        final Context context = (getContext() != null? getContext() : getActivity().getApplicationContext());
        requestQueue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlGetDistributor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject values = new JSONObject(response).getJSONObject("values");
                            String nameP = values.getString("name");
                            String addressP = values.getString("address");
                            String areaP = "Area " + values.getString("area");
                            String picP = values.getString("pic");
                            String picContactP = values.getString("pic_contact");

                            userProfile.setDistName(nameP );
                            userProfile.setDistAddr(addressP);
                            userProfile.setDistArea(areaP);
                            userProfile.setDistPic(picP);
                            userProfile.setDistPicContact(picContactP);

                            updateView(userProfile);

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

    private void getUserProfile(final UserProfile userProfile) {
        final Context context = (getContext() != null? getContext() : getActivity().getApplicationContext());
        requestQueue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlGetUserProfile,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject values = new JSONObject(response).getJSONObject("values");
                            String usernameP = values.getString("username");
                            String nameP = values.getString("name");
                            String contactP = values.getString("contact");


                            userProfile.setUsername(usernameP);
                            userProfile.setName(nameP);
                            userProfile.setContact(contactP);

                            getDistributor(userProfile);

                        } catch (JSONException e) {
                            Log.d(TAG, e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    switch (error.networkResponse.statusCode) {
                        case 404:
                            Preferences.clearLoginPrefs(context);
                            Toast.makeText(context, R.string.error_user_not_found, Toast.LENGTH_LONG).show();
                            break;
                        case 440:
                            Preferences.clearLoginPrefs(context);
                            Toast.makeText(context, R.string.error_session_expired, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(context, LoginActivity.class));
                            try {
                                getActivity().finish();
                            } catch (IllegalStateException illegalStateExceptionError) {
                                Log.d(TAG, "IllegalStateException");
                            }
                            break;
                        case 498:
                            Preferences.clearLoginPrefs(context);
                            Toast.makeText(context, R.string.error_invalid_token, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(context, LoginActivity.class));
                            try {
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
                    } catch (IllegalStateException illegalStateExceptionError) {
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

    private void updateView(UserProfile userProfile) {
        mProfileName.setText(userProfile.getName());
        mProfileUsername.setText(userProfile.getUsername());
        mProfileContact.setText(userProfile.getContact());
        mDistName.setText(userProfile.getDistName());
        mDistAddress.setText(userProfile.getDistAddr());
        mDistArea.setText(userProfile.getDistArea());
        mDistPic.setText(userProfile.getDistPic());
        mDistPicContact.setText(userProfile.getDistPicContact());
    }


    private class UserProfile {
        private String username;
        private String name;
        private String contact;
        private String distName;
        private String distArea;
        private String distAddr;
        private String distPic;
        private String distPicContact;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getDistName() {
            return distName;
        }

        public void setDistName(String distName) {
            this.distName = distName;
        }

        public String getDistArea() {
            return distArea;
        }

        public void setDistArea(String distArea) {
            this.distArea = distArea;
        }

        public String getDistAddr() {
            return distAddr;
        }

        public void setDistAddr(String distAddr) {
            this.distAddr = distAddr;
        }

        public String getDistPic() {
            return distPic;
        }

        public void setDistPic(String distPic) {
            this.distPic = distPic;
        }

        public String getDistPicContact() {
            return distPicContact;
        }

        public void setDistPicContact(String distPicContact) {
            this.distPicContact = distPicContact;
        }
    }

}


