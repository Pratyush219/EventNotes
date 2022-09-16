package com.example.eventnotes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventnotes.MainActivity;
import com.example.eventnotes.R;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    private Handler handler;
    private Runnable r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        handler = new Handler();
        Objects.requireNonNull(getSupportActionBar()).hide();
        r = () -> {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            try {
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        handler.postDelayed(r, 2000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(r);
    }
}