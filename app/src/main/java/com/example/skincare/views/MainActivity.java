package com.example.skincare.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skincare.R;
import com.example.skincare.imp.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 4000; // 4 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        PreferenceManager preferenceManager = new PreferenceManager(this);
        String token = preferenceManager.getToken();


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if(token != null){
                // User is already logged in, navigate to Home/Main
                Intent intent = new Intent(this, HomePageActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Navigate to Login/Register
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);
    }
}