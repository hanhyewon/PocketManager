package com.example.hyejin;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created By sinhyeongseob on 2019-05-22
 */
public class App extends Application {

    public static SharedPreferences mPref;
    public static SharedPreferences.Editor mPrefEdit;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mPref == null) {
            mPref = getSharedPreferences("kt_sang_sang", Context.MODE_PRIVATE);
        }
        if (mPrefEdit == null) {
            mPrefEdit = mPref.edit();
        }
    }
}
