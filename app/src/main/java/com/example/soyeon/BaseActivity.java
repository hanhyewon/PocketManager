package com.example.soyeon;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    } @Override
    protected void onStart() {
        Log.v(TAG, "onStart");
        super.onStart();
    }
    @Override protected void onRestart() {
        Log.v(TAG, "onRestart");
        super.onRestart();
    }
    @Override protected void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
    }
    @Override protected void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
    }
    @Override protected void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
    }
    @Override protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

}
