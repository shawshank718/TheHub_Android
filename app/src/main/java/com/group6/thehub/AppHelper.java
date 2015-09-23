package com.group6.thehub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sathwiksingari on 9/18/15.
 */
public class AppHelper {

    private Context mContext;

    private final String USER_PREFS = "com.group6.thehub.user_prefs";

    public final String END_POINT = "http://10.19.94.42/";

    public AppHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void slideInStayStill(Intent intent) {
        mContext.startActivity(intent);
        ((AppCompatActivity) mContext).overridePendingTransition(R.anim.push_left_in, 0);
    }

    public void slideUpPushUp(Intent intent) {
        mContext.startActivity(intent);
        ((AppCompatActivity) mContext).overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }

    public void slideDownPushDown(Intent intent) {
        mContext.startActivity(intent);
        ((AppCompatActivity) mContext).overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }

    public void saveToUserPrefs(String key, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String getFromUserPrefs(String key, String defaultValue) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, defaultValue);
        return value;
    }
}
