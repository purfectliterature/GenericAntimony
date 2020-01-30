package com.purfectliterature.antimony;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Preferences.isLoggedIn(getBaseContext())) {
            Intent moveToMainMenu = new Intent(MainActivity.this, MainMenuActivity.class);
            startActivity(moveToMainMenu);
            finish();
        } else {
            Intent moveToLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(moveToLogin);
            finish();

        }
    }
}
