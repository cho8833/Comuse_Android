package com.example.comuse.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.comuse.DataManager.FirebaseVar;
import com.example.comuse.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build();
            FirebaseVar.db = FirebaseFirestore.getInstance();
            FirebaseVar.db.setFirestoreSettings(settings);
            mAuth = FirebaseAuth.getInstance();

        } catch (Exception e) {
            Log.e(TAG,"splashactivity error",e);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseVar.user = mAuth.getCurrentUser();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}
