package com.purfectliterature.antimony;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    static final String USER_TOKEN = "user_token";
    static final String USER_ID = "user_id";

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLoginPrefs(String token, String userId, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_TOKEN, token);
        editor.putString(USER_ID, userId);
        editor.apply();
    }

    public static String getUserToken(Context context) {
        return getSharedPreferences(context).getString(USER_TOKEN, null);
    }

    public static String getUserId(Context context) {
        return getSharedPreferences(context).getString(USER_ID, null);
    }

    public static void clearLoginPrefs(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(USER_TOKEN);
        editor.remove(USER_ID);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        return (getSharedPreferences(context).contains(USER_TOKEN) && getSharedPreferences(context).contains(USER_ID));
    }
}
